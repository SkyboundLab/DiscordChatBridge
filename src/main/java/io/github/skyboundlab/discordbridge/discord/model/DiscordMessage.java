package io.github.skyboundlab.discordbridge.discord.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;
import java.util.List;

public record DiscordMessage(@NotNull String authorName, @NotNull String rawContent, @NotNull List<String> attachmentUrls,
                             @Nullable String topRoleName, @Nullable Color roleColor,
                             @Nullable Color displayColor) {

    public DiscordMessage {
        attachmentUrls = List.copyOf(attachmentUrls);
    }
}