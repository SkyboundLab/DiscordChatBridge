package net.aerh.discordbridge.discord;

import com.hypixel.hytale.logger.HytaleLogger;
import net.aerh.discordbridge.config.DiscordBridgeConfig;
import net.aerh.discordbridge.config.DiscordConfig;
import net.aerh.discordbridge.discord.model.DiscordMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;

public final class DiscordBotConnection implements AutoCloseable {

    private final DiscordBridgeConfig config;
    private final HytaleLogger logger;
    private final Consumer<DiscordMessage> relayToGameChat;
    private final CompletableFuture<Void> readyFuture = new CompletableFuture<>();
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    private JDA jda;
    private volatile TextChannel bridgeChannel;

    public DiscordBotConnection(
            @NotNull DiscordBridgeConfig config,
            @NotNull HytaleLogger logger,
            @NotNull Consumer<DiscordMessage> relayToGameChat
    ) {
        this.config = config;
        this.logger = logger;
        this.relayToGameChat = relayToGameChat;
    }

    @NotNull
    public CompletableFuture<Void> start() {
        try {
            DiscordConfig discordConfig = config.getDiscordConfig();
            BridgeListener listener = new BridgeListener(config, logger, readyFuture, relayToGameChat, this::onChannelReady);
            JDABuilder builder = JDABuilder.createDefault(discordConfig.getBotToken())
                    .setMemberCachePolicy(MemberCachePolicy.NONE)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .setActivity(Activity.playing(discordConfig.getPresenceMessage()))
                    .addEventListeners(listener);
            this.jda = builder.build();
        } catch (Throwable throwable) {
            readyFuture.completeExceptionally(throwable);
            return readyFuture;
        }

        return readyFuture;
    }

    public boolean isReady() {
        return readyFuture.isDone() && !readyFuture.isCompletedExceptionally() && bridgeChannel != null;
    }

    public void sendMessage(@NotNull String content) {
        TextChannel channel = this.bridgeChannel;
        if (channel == null) {
            logger.at(Level.FINE).log("Discord channel not ready; dropping message.");
            return;
        }

        channel.sendMessage(content)
                .queue(null, throwable -> logger.at(Level.WARNING)
                        .withCause(throwable)
                        .log("Failed to send chat message to Discord"));
    }

    public void sendEmbed(@NotNull String content, @NotNull String colorHex, @NotNull String contentType) {
        TextChannel channel = this.bridgeChannel;
        if (channel == null) {
            logger.at(Level.FINE).log("Discord channel not ready; dropping embed.");
            return;
        }

        try {
            java.awt.Color color = java.awt.Color.decode(colorHex);
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(color);

            if ("title".equalsIgnoreCase(contentType)) {
                embed.setTitle(content);
            } else {
                embed.setDescription(content);
            }

            channel.sendMessageEmbeds(embed.build())
                    .queue(null, throwable -> logger.at(Level.WARNING)
                            .withCause(throwable)
                            .log("Failed to send embed message to Discord"));
        } catch (NumberFormatException e) {
            logger.at(Level.WARNING).withCause(e).log("Invalid color hex: %s", colorHex);
        }
    }

    public void sendWebhookMessage(@NotNull String webhookUrl, @NotNull String username, @NotNull String content) {
        if (webhookUrl.isEmpty()) {
            logger.at(Level.WARNING).log("Webhook URL is empty; cannot send webhook message.");
            return;
        }

        // Send via HTTP POST to webhook URL
        java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();
        String json = String.format("{\"username\": \"%s\", \"content\": \"%s\"}", username.replace("\"", "\\\""), content.replace("\"", "\\\""));
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(webhookUrl))
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.discarding())
                .exceptionally(throwable -> {
                    logger.at(Level.WARNING).withCause(throwable).log("Failed to send webhook message to Discord");
                    return null;
                });
    }

    @Override
    public void close() {
        shutdown();
    }

    public void shutdown() {
        if (shuttingDown.compareAndSet(false, true) && jda != null) {
            jda.shutdownNow();
        }
    }

    private void onChannelReady(@NotNull TextChannel channel) {
        this.bridgeChannel = channel;
    }
}