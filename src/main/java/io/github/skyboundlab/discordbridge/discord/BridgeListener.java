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
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.logging.Level;

final class BridgeListener extends ListenerAdapter {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static final int MAX_FETCH_ATTEMPTS = 6;
    private static final long RETRY_DELAY_SECONDS = 5;

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
        resolveBridgeChannel(event.getJDA(), 0);
    }

    private void resolveBridgeChannel(@NotNull JDA jda, int attempt) {
        if (jda.getStatus() == JDA.Status.SHUTDOWN || readyFuture.isDone()) {
            return;
        }

        DiscordConfig discordConfig = config.getDiscordConfig();
        String channelId = discordConfig.getChannelId();
        String threadId = discordConfig.getThreadId();

        if (threadId != null && !threadId.isBlank()) {
            long threadIdLong;
            try {
                threadIdLong = Long.parseLong(threadId.trim());
            } catch (NumberFormatException e) {
                IllegalStateException exception = new IllegalStateException("Configured ThreadId '" + threadId + "' is not a valid Discord snowflake id", e);
                LOGGER.at(Level.SEVERE).withCause(exception).log("Discord bridge thread id invalid");
                readyFuture.completeExceptionally(exception);
                return;
            }

            ThreadChannel cached = jda.getThreadChannelById(threadIdLong);
            if (cached != null) {
                finishWithChannel(jda, cached);
                return;
            }

            LOGGER.at(Level.INFO).log("Thread with id %s not in cache, fetching it from Discord (attempt %d/%d)", threadId, attempt + 1, MAX_FETCH_ATTEMPTS + 1);
            fetchThreadChannel(jda, threadIdLong, attempt);
            return;
        }

        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            IllegalStateException exception = new IllegalStateException(
                    "Unable to find text channel with id " + channelId);
            LOGGER.at(Level.SEVERE).withCause(exception).log("Discord bridge channel missing");
            readyFuture.completeExceptionally(exception);
            return;
        }

        LOGGER.at(Level.INFO).log("Loaded Discord channel: %s", channel.getName());
        finishWithChannel(jda, channel);
    }

    private void fetchThreadChannel(@NotNull JDA jda, long threadId, int attempt) {
        Route.CompiledRoute route = Route.Channels.GET_CHANNEL.compile(Long.toUnsignedString(threadId));
        new RestActionImpl<ThreadChannel>(jda, route, (response, request) -> {
            DataObject json = response.getObject();

            int type = json.getInt("type", -1);
            if (type != 10 && type != 11 && type != 12) {
                throw new IllegalStateException("Configured ThreadId " + threadId + " is not a thread (Discord channel type " + type + "). Check ThreadId in config.json.");
            }

            long guildId = json.getLong("guild_id", 0L);
            if (jda.getGuildById(guildId) == null) {
                throw new IllegalStateException("Discord thread " + threadId + " is in a guild the bot is not a member of." + " Invite the bot to that server and restart.");
            }

            if (!(jda instanceof JDAImpl jdaImpl)) {
                throw new IllegalStateException("Unexpected JDA implementation: " + jda.getClass().getName());
            }

            ThreadChannel thread = jdaImpl.getEntityBuilder().createThreadChannel(json, guildId);
            if (thread == null) {
                throw new IllegalStateException("Discord returned thread " + threadId + " but JDA could not build it.");
            }

            return thread;
        }).queue(
                thread -> {
                    if (jda.getStatus() == JDA.Status.SHUTDOWN || readyFuture.isDone()) {
                        return;
                    }

                    LOGGER.at(Level.INFO).log("Fetched Discord thread: %s", thread.getName());

                    finishWithChannel(jda, thread);
                },
                failure -> {
                    if (jda.getStatus() == JDA.Status.SHUTDOWN || readyFuture.isDone()) {
                        return;
                    }

                    handleFetchFailure(jda, threadId, attempt, failure);
                });
    }

    private void handleFetchFailure(
            @NotNull JDA jda,
            long threadId,
            int attempt,
            @NotNull Throwable failure
    ) {
        if (failure instanceof IllegalStateException illegalState) {
            LOGGER.at(Level.SEVERE).withCause(illegalState).log("Discord bridge thread missing");
            readyFuture.completeExceptionally(illegalState);
            return;
        }

        if (failure instanceof ErrorResponseException errorResponse) {
            if (errorResponse.getErrorResponse() == ErrorResponse.UNKNOWN_CHANNEL) {
                IllegalStateException exception = new IllegalStateException("No Discord channel exists with id " + threadId + ". Check ThreadId in config.json: the thread may have been deleted," + " or the id may be a channel id rather than a thread id.");
                LOGGER.at(Level.SEVERE).withCause(exception).log("Discord bridge thread missing");
                readyFuture.completeExceptionally(exception);
                return;
            }
            if (errorResponse.getErrorResponse() == ErrorResponse.MISSING_ACCESS) {
                IllegalStateException exception = new IllegalStateException("Discord bot cannot access thread " + threadId + ". Check that the bot is in the server and can view the thread's" + " parent channel (View Channel permission).");
                LOGGER.at(Level.SEVERE).withCause(exception).log("Discord bridge thread missing");
                readyFuture.completeExceptionally(exception);
                return;
            }
        }

        if (isTransient(failure) && attempt < MAX_FETCH_ATTEMPTS) {
            LOGGER.at(Level.WARNING).withCause(failure).log("Transient Discord error while fetching thread %s, retrying in %d seconds (attempt %d/%d)", threadId, RETRY_DELAY_SECONDS, attempt + 2, MAX_FETCH_ATTEMPTS + 1);
            jda.getGatewayPool().schedule(() -> resolveBridgeChannel(jda, attempt + 1), RETRY_DELAY_SECONDS, TimeUnit.SECONDS);
            return;
        }

        IllegalStateException exception = new IllegalStateException("Unable to fetch Discord thread with id " + threadId + " from Discord", failure);
        LOGGER.at(Level.SEVERE).withCause(exception).log("Discord bridge thread missing");
        readyFuture.completeExceptionally(exception);
    }

    private static boolean isTransient(@NotNull Throwable failure) {
        Throwable current = failure;
        while (current != null) {
            if (current instanceof ErrorResponseException errorResponse && errorResponse.isServerError()) {
                return true;
            }

            if (current instanceof IOException || current instanceof TimeoutException) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }

    private void finishWithChannel(@NotNull JDA jda, @NotNull GuildMessageChannel bridgeChannel) {
        if (jda.getStatus() == JDA.Status.SHUTDOWN || readyFuture.isDone()) {
            return;
        }

        if (!bridgeChannel.canTalk()) {
            IllegalStateException exception = new IllegalStateException("Discord bot cannot talk in the configured bridge channel");
            LOGGER.at(Level.SEVERE).withCause(exception).log("Discord bridge channel unavailable");
            readyFuture.completeExceptionally(exception);
            return;
        }

        if (bridgeChannel instanceof ThreadChannel thread) {
            if (thread.isArchived()) {
                thread.getManager().setArchived(false).queue(
                        ignored -> joinThread(jda, thread),
                        failure -> {
                            LOGGER.at(Level.WARNING).withCause(failure).log("Could not unarchive Discord thread %s; messages to it may fail", thread.getName());
                            joinThread(jda, thread);
                        });
                return;
            }

            joinThread(jda, thread);

            return;
        }

        completeChannel(jda, bridgeChannel);
    }

    private void joinThread(@NotNull JDA jda, @NotNull ThreadChannel thread) {
        if (thread.isJoined()) {
            completeChannel(jda, thread);
            return;
        }

        thread.join().queue(
                ignored -> {
                    LOGGER.at(Level.INFO).log("Joined Discord thread: %s", thread.getName());
                    completeChannel(jda, thread);
                },
                failure -> {
                    LOGGER.at(Level.WARNING).withCause(failure).log("Could not join Discord thread %s; continuing anyway, " + "but Discord -> game relay may not receive its messages", thread.getName());
                    completeChannel(jda, thread);
                });
    }

    private void completeChannel(@NotNull JDA jda, @NotNull GuildMessageChannel bridgeChannel) {
        if (jda.getStatus() == JDA.Status.SHUTDOWN || readyFuture.isDone()) {
            return;
        }

        discordChannelUpdater.accept(bridgeChannel);

        LOGGER.at(Level.INFO).log("Discord bot connected as %s", jda.getSelfUser().getAsTag());

        readyFuture.complete(null);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        DiscordConfig discordConfig = config.getDiscordConfig();

        if (!config.isRelayDiscordToGame() || event.isFromType(ChannelType.PRIVATE) || !event.getChannel().getId().equals(getBridgeTargetId(discordConfig))) {
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
