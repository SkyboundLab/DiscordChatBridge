package net.aerh.discordbridge.discord;

import com.hypixel.hytale.logger.HytaleLogger;
import net.aerh.discordbridge.config.DiscordBridgeConfig;
import net.aerh.discordbridge.config.DiscordConfig;
import net.aerh.discordbridge.discord.model.DiscordMessage;
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