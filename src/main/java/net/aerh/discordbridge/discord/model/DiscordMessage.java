package net.aerh.discordbridge.discord.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;
import java.util.Collections;
import java.util.List;

public record DiscordMessage(String authorName, String rawContent, List<String> attachmentUrls,
                             @Nullable String topRoleName, @Nullable Color roleColor,
                             @Nullable Color displayColor) {

    public DiscordMessage(
            @NotNull String authorName,
            @NotNull String rawContent,
            @NotNull List<String> attachmentUrls,
            @Nullable String topRoleName,
            @Nullable Color roleColor,
            @Nullable Color displayColor
    ) {
        this.authorName = authorName;
        this.rawContent = rawContent;
        this.attachmentUrls = Collections.unmodifiableList(attachmentUrls);
        this.topRoleName = topRoleName;
        this.roleColor = roleColor;
        this.displayColor = displayColor;
    }

    @Override
    @NotNull
    public String authorName() {
        return authorName;
    }

    @Override
    @NotNull
    public String rawContent() {
        return rawContent;
    }

    @Override
    @NotNull
    public List<String> attachmentUrls() {
        return attachmentUrls;
    }
}