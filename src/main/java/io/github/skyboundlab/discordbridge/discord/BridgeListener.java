package io.github.skyboundlab.discordbridge.discord;

import com.hypixel.hytale.logger.HytaleLogger;
import io.github.skyboundlab.discordbridge.config.DiscordBridgeConfig;
import io.github.skyboundlab.discordbridge.config.DiscordConfig;
import io.github.skyboundlab.discordbridge.discord.model.DiscordMessage;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;

final class BridgeListener extends ListenerAdapter {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private final DiscordBridgeConfig config;
    private final CompletableFuture<Void> readyFuture;
    private final Consumer<DiscordMessage> relayToGameChat;
    private final Consumer<GuildMessageChannel> discordChannelUpdater;

    BridgeListener(
            @NotNull DiscordBridgeConfig config,
            @NotNull CompletableFuture<Void> readyFuture,
            @NotNull Consumer<DiscordMessage> relayToGameChat,
            @NotNull Consumer<GuildMessageChannel> discordChannelUpdater
    ) {
        this.config = config;
        this.readyFuture = readyFuture;
        this.relayToGameChat = relayToGameChat;
        this.discordChannelUpdater = discordChannelUpdater;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        resolveBridgeChannel(event.getJDA());
    }

    private void resolveBridgeChannel(@NotNull JDA jda) {
        if (jda.getStatus() == JDA.Status.SHUTDOWN) {
            return;
        }

        DiscordConfig discordConfig = config.getDiscordConfig();
        String channelId = discordConfig.getChannelId();
        String threadId = discordConfig.getThreadId();

        GuildMessageChannel bridgeChannel;

        if (threadId != null && !threadId.isBlank()) {
            ThreadChannel thread = jda.getThreadChannelById(threadId);
            if (thread == null) {
                // threads may not be in the cache yet at onReady since they are loaded from
                // a later THREAD_LIST_SYNC gateway event, so retry instead of permanently bailing
                LOGGER.at(Level.INFO).log("Thread with id %s not cached yet, retrying in 5 seconds", threadId);
                jda.getGatewayPool().schedule(() -> resolveBridgeChannel(jda), 5, TimeUnit.SECONDS);
                return;
            }

            LOGGER.at(Level.INFO).log("Loaded Discord thread: %s", thread.getName());
            bridgeChannel = thread;
        } else {
            TextChannel channel = jda.getTextChannelById(channelId);
            if (channel == null) {
                IllegalStateException exception = new IllegalStateException(
                        "Unable to find text channel with id " + channelId);
                LOGGER.at(Level.SEVERE).withCause(exception).log("Discord bridge channel missing");
                readyFuture.completeExceptionally(exception);
                return;
            }

            LOGGER.at(Level.INFO).log("Loaded Discord channel: %s", channel.getName());
            bridgeChannel = channel;
        }

        if (!bridgeChannel.canTalk()) {
            IllegalStateException exception = new IllegalStateException(
                    "Discord bot cannot talk in the configured bridge channel");
            LOGGER.at(Level.SEVERE).withCause(exception).log("Discord bridge channel unavailable");
            readyFuture.completeExceptionally(exception);
            return;
        }

        discordChannelUpdater.accept(bridgeChannel);
        LOGGER.at(Level.INFO).log("Discord bot connected as %s", jda.getSelfUser().getAsTag());
        readyFuture.complete(null);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        DiscordConfig discordConfig = config.getDiscordConfig();

        if (!config.isRelayDiscordToGame()
                || event.isFromType(ChannelType.PRIVATE)
                || !event.getChannel().getId().equals(getBridgeTargetId(discordConfig))) {
            return;
        }

        if (event.getAuthor().getIdLong() == event.getJDA().getSelfUser().getIdLong()) {
            return;
        }

        if (event.getAuthor().isBot() && discordConfig.isIgnoreBotMessages()) {
            return;
        }

        if (event.isWebhookMessage() && discordConfig.isIgnoreWebhookMessages()) {
            return;
        }

        List<String> attachments = event.getMessage().getAttachments().stream()
                .map(Message.Attachment::getUrl)
                .toList();

        Member member = event.getMember();
        String displayName = member != null ? member.getEffectiveName() : event.getAuthor().getName();
        Role topRole = member != null && !member.getRoles().isEmpty() ? member.getRoles().getFirst() : null;
        String topRoleName = topRole != null ? topRole.getName() : null;
        Color roleColor = topRole != null ? topRole.getColors().getPrimary() : null;
        Color displayColor = member != null ? member.getColors().getPrimary() : null;

        DiscordMessage bridgeMessage = new DiscordMessage(
                displayName,
                event.getMessage().getContentDisplay(),
                attachments,
                topRoleName,
                roleColor,
                displayColor
        );

        relayToGameChat.accept(bridgeMessage);
    }

    @NotNull
    private static String getBridgeTargetId(@NotNull DiscordConfig discordConfig) {
        String threadId = discordConfig.getThreadId();
        return threadId != null && !threadId.isBlank() ? threadId : discordConfig.getChannelId();
    }
}
