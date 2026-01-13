package net.aerh.discordbridge.discord;

import net.aerh.discordbridge.discord.model.DiscordMessage;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Utility class for sanitizing messages between Discord and the game.
 */
public final class MessageSanitizer {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    private MessageSanitizer() {
    }

    /**
     * Sanitizes a message going from the game to Discord.
     *
     * @param original      the original message content
     * @param allowMentions whether to allow @mentions
     * @return the sanitized message
     */
    @NotNull
    public static String sanitizeOutgoing(@NotNull String original, boolean allowMentions) {
        String value = WHITESPACE_PATTERN.matcher(original).replaceAll(" ").trim();

        if (!allowMentions) {
            value = preventMentions(value);
        }

        return value;
    }

    /**
     * Sanitizes a message coming from Discord to the game.
     *
     * @param message the Discord message
     * @return the sanitized message content
     */
    @NotNull
    public static String sanitizeIncoming(@NotNull DiscordMessage message) {
        StringBuilder builder = new StringBuilder();

        if (!message.rawContent().isBlank()) {
            builder.append(message.rawContent());
        }

        for (String attachment : message.attachmentUrls()) {
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append("(attachment: ").append(attachment).append(')');
        }

        return WHITESPACE_PATTERN.matcher(builder.toString()).replaceAll(" ").trim();
    }

    /**
     * Prevents Discord mentions by adding spaces after @ symbols.
     *
     * @param value the string to sanitize
     * @return the sanitized string with mentions disabled
     */
    @NotNull
    public static String preventMentions(@NotNull String value) {
        String result = value.replace("@everyone", "@ everyone").replace("@here", "@ here");
        result = result.replace("<@", "<@ ");
        result = result.replace("<@&", "<@& ");
        return result;
    }
}