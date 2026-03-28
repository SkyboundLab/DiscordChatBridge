package net.aerh.discordbridge.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Per-event configuration for toggling and formatting notifications.
 */
public final class EventMessageConfig {

    public static final BuilderCodec<EventMessageConfig> CODEC = BuilderCodec
            .builder(EventMessageConfig.class, EventMessageConfig::new)
            .append(new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                    (cfg, value) -> cfg.enabled = value,
                    cfg -> cfg.enabled)
            .add()
            .append(new KeyedCodec<>("Message", Codec.STRING),
                    (cfg, value) -> cfg.message = value,
                    cfg -> cfg.message)
            .add()
            .append(new KeyedCodec<>("Color", Codec.STRING),
                    (cfg, value) -> cfg.color = value,
                    cfg -> cfg.color)
            .add()
            .build();

    private boolean enabled = true;
    private String message = "";
    private String color = null;

    public EventMessageConfig() {
    }

    public EventMessageConfig(boolean enabled, @NotNull String message) {
        this.enabled = enabled;
        this.message = message;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @NotNull
    public String getMessage() {
        return message == null ? "" : message;
    }

    @Nullable
    public String getColor() {
        return color == null || color.isBlank() ? null : color;
    }

    /**
     * Parses the color string to an integer for Discord.
     * Supports formats: #RRGGBB or RRGGBB
     *
     * @return The color as an integer, or null if not set or invalid
     */
    @Nullable
    public Integer getColorAsInt() {
        String colorStr = getColor();
        if (colorStr == null) {
            return null;
        }
        try {
            if (colorStr.startsWith("#")) {
                colorStr = colorStr.substring(1);
            }
            return Integer.parseInt(colorStr, 16);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}