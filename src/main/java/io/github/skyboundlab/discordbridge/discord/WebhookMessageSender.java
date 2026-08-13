package io.github.skyboundlab.discordbridge.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.hypixel.hytale.logger.HytaleLogger;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.logging.Level;

public final class WebhookMessageSender implements AutoCloseable {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private final WebhookClient webhookClient;
    private final String avatarUrlFormat;

    public WebhookMessageSender(@NotNull String webhookUrl, @NotNull String avatarUrlFormat, @NotNull String threadId) {
        WebhookClientBuilder builder = new WebhookClientBuilder(webhookUrl);
        if (threadId != null && !threadId.isBlank()) {
            builder.setThreadId(Long.parseLong(threadId));
        }
        this.webhookClient = builder
                .setWait(false)
                .build();
        this.avatarUrlFormat = avatarUrlFormat;
    }

    public void sendPlayerMessage(@NotNull String username, @NotNull UUID playerUuid, @NotNull String message) {
        String avatarUrl = String.format(avatarUrlFormat, playerUuid.toString().replace("-", ""));

        WebhookMessageBuilder builder = new WebhookMessageBuilder()
                .setUsername(username)
                .setAvatarUrl(avatarUrl)
                .setContent(message);

        webhookClient.send(builder.build())
                .exceptionally(throwable -> {
                    LOGGER.at(Level.WARNING)
                            .withCause(throwable)
                            .log("Failed to send webhook message to Discord");
                    return null;
                });
    }

    @Override
    public void close() {
        webhookClient.close();
    }
}