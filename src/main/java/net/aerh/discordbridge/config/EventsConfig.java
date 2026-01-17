package net.aerh.discordbridge.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import org.jetbrains.annotations.NotNull;

/**
 * Configuration for event broadcast toggles.
 */
public final class EventsConfig {

    private static final class EmbedSettings {
        public static final BuilderCodec<EmbedSettings> CODEC = BuilderCodec
                .builder(EmbedSettings.class, EmbedSettings::new)
                .append(new KeyedCodec<>("Enable", Codec.BOOLEAN),
                        (cfg, value) -> cfg.enable = value,
                        cfg -> cfg.enable)
                .add()
                .append(new KeyedCodec<>("Color", Codec.STRING),
                        (cfg, value) -> cfg.color = value,
                        cfg -> cfg.color)
                .add()
                .build();

        private boolean enable;
        private String color;

        public EmbedSettings() {
            this.enable = false;
            this.color = "#5865F2";
        }

        public boolean isEnable() {
            return enable;
        }

        @NotNull
        public String getColor() {
            return color != null ? color : "#5865F2";
        }
    }

    private static final class EventSettings {
        public static final BuilderCodec<EventSettings> CODEC = BuilderCodec
                .builder(EventSettings.class, EventSettings::new)
                .append(new KeyedCodec<>("Enable", Codec.BOOLEAN),
                        (cfg, value) -> cfg.enable = value,
                        cfg -> cfg.enable)
                .add()
                .append(new KeyedCodec<>("Embed", EmbedSettings.CODEC),
                        (cfg, value) -> cfg.embed = value,
                        cfg -> cfg.embed)
                .add()
                .append(new KeyedCodec<>("Message", Codec.STRING),
                        (cfg, value) -> cfg.message = value,
                        cfg -> cfg.message)
                .add()
                .build();

        private boolean enable;
        private EmbedSettings embed;
        private String message;

        public EventSettings() {
            this.enable = false;
            this.embed = new EmbedSettings();
            this.message = "";
        }

        public EventSettings(boolean enable, boolean embedEnable, String embedColor, String message) {
            this.enable = enable;
            this.embed = new EmbedSettings();
            this.embed.enable = embedEnable;
            this.embed.color = embedColor;
            this.message = message;
        }

        public boolean isEnable() {
            return enable;
        }

        @NotNull
        public EmbedSettings getEmbed() {
            return embed;
        }

        @NotNull
        public String getMessage() {
            return message != null ? message : "";
        }
    }

    public static final BuilderCodec<EventsConfig> CODEC = BuilderCodec
            .builder(EventsConfig.class, EventsConfig::new)
            .append(new KeyedCodec<>("ServerStart", EventSettings.CODEC),
                    (cfg, value) -> cfg.serverStart = value,
                    cfg -> cfg.serverStart)
            .add()
            .append(new KeyedCodec<>("ServerStop", EventSettings.CODEC),
                    (cfg, value) -> cfg.serverStop = value,
                    cfg -> cfg.serverStop)
            .add()
            .append(new KeyedCodec<>("PlayerJoin", EventSettings.CODEC),
                    (cfg, value) -> cfg.playerJoin = value,
                    cfg -> cfg.playerJoin)
            .add()
            .append(new KeyedCodec<>("PlayerLeave", EventSettings.CODEC),
                    (cfg, value) -> cfg.playerLeave = value,
                    cfg -> cfg.playerLeave)
            .add()
            .append(new KeyedCodec<>("WorldEnter", EventSettings.CODEC),
                    (cfg, value) -> cfg.worldEnter = value,
                    cfg -> cfg.worldEnter)
            .add()
            .append(new KeyedCodec<>("WorldLeave", EventSettings.CODEC),
                    (cfg, value) -> cfg.worldLeave = value,
                    cfg -> cfg.worldLeave)
            .add()
            .append(new KeyedCodec<>("PlayerDeath", EventSettings.CODEC),
                    (cfg, value) -> cfg.playerDeath = value,
                    cfg -> cfg.playerDeath)
            .add()
            .append(new KeyedCodec<>("PlayerKill", EventSettings.CODEC),
                    (cfg, value) -> cfg.playerKill = value,
                    cfg -> cfg.playerKill)
            .add()
            .build();

    private EventSettings serverStart = new EventSettings(true, true, "#008000", "Server has started");
    private EventSettings serverStop = new EventSettings(true, true, "#800000", "Server has stopped");
    private EventSettings playerJoin = new EventSettings(true, true, "#00FF00", "**%player% joined the game**");
    private EventSettings playerLeave = new EventSettings(true, true, "#FF0000", "**%player% left the game**");
    private EventSettings worldEnter = new EventSettings(false, false, "#5865F2", "%player% entered %world%");
    private EventSettings worldLeave = new EventSettings(false, false, "#5865F2", "%player% left %world%");
    private EventSettings playerDeath = new EventSettings(true, false, "#FFA500", "**%player% died**");
    private EventSettings playerKill = new EventSettings(true, false, "#FF0000", "**%killer% eliminated %victim%**");

    public boolean isServerStart() {
        return serverStart.isEnable();
    }

    public boolean isServerStop() {
        return serverStop.isEnable();
    }

    public boolean isPlayerJoin() {
        return playerJoin.isEnable();
    }

    public boolean isPlayerLeave() {
        return playerLeave.isEnable();
    }

    public boolean isWorldEnter() {
        return worldEnter.isEnable();
    }

    public boolean isWorldLeave() {
        return worldLeave.isEnable();
    }

    public boolean isPlayerDeath() {
        return playerDeath.isEnable();
    }

    public boolean isPlayerKill() {
        return playerKill.isEnable();
    }

    public boolean isServerStartEmbed() {
        return serverStart.getEmbed().isEnable();
    }

    @NotNull
    public String getServerStartEmbedColor() {
        return serverStart.getEmbed().getColor();
    }

    public boolean isServerStopEmbed() {
        return serverStop.getEmbed().isEnable();
    }

    @NotNull
    public String getServerStopEmbedColor() {
        return serverStop.getEmbed().getColor();
    }

    public boolean isPlayerJoinEmbed() {
        return playerJoin.getEmbed().isEnable();
    }

    @NotNull
    public String getPlayerJoinEmbedColor() {
        return playerJoin.getEmbed().getColor();
    }

    public boolean isPlayerLeaveEmbed() {
        return playerLeave.getEmbed().isEnable();
    }

    @NotNull
    public String getPlayerLeaveEmbedColor() {
        return playerLeave.getEmbed().getColor();
    }

    public boolean isWorldEnterEmbed() {
        return worldEnter.getEmbed().isEnable();
    }

    @NotNull
    public String getWorldEnterEmbedColor() {
        return worldEnter.getEmbed().getColor();
    }

    public boolean isWorldLeaveEmbed() {
        return worldLeave.getEmbed().isEnable();
    }

    @NotNull
    public String getWorldLeaveEmbedColor() {
        return worldLeave.getEmbed().getColor();
    }

    public boolean isPlayerDeathEmbed() {
        return playerDeath.getEmbed().isEnable();
    }

    @NotNull
    public String getPlayerDeathEmbedColor() {
        return playerDeath.getEmbed().getColor();
    }

    public boolean isPlayerKillEmbed() {
        return playerKill.getEmbed().isEnable();
    }

    @NotNull
    public String getPlayerKillEmbedColor() {
        return playerKill.getEmbed().getColor();
    }

    @NotNull
    public String getServerStartMessage() {
        return serverStart.getMessage();
    }

    @NotNull
    public String getServerStopMessage() {
        return serverStop.getMessage();
    }

    @NotNull
    public String getPlayerJoinMessage() {
        return playerJoin.getMessage();
    }

    @NotNull
    public String getPlayerLeaveMessage() {
        return playerLeave.getMessage();
    }

    @NotNull
    public String getWorldEnterMessage() {
        return worldEnter.getMessage();
    }

    @NotNull
    public String getWorldLeaveMessage() {
        return worldLeave.getMessage();
    }

    @NotNull
    public String getPlayerDeathMessage() {
        return playerDeath.getMessage();
    }

    @NotNull
    public String getPlayerKillMessage() {
        return playerKill.getMessage();
    }
}