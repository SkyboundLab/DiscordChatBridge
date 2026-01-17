package net.aerh.discordbridge.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import org.jetbrains.annotations.NotNull;

/**
 * Configuration for event broadcast toggles.
 */
public final class EventsConfig {

    public static final BuilderCodec<EventsConfig> CODEC = BuilderCodec
            .builder(EventsConfig.class, EventsConfig::new)
            .append(new KeyedCodec<>("ServerStart", Codec.BOOLEAN),
                    (cfg, value) -> cfg.serverStart = value,
                    cfg -> cfg.serverStart)
            .add()
            .append(new KeyedCodec<>("ServerStop", Codec.BOOLEAN),
                    (cfg, value) -> cfg.serverStop = value,
                    cfg -> cfg.serverStop)
            .add()
            .append(new KeyedCodec<>("PlayerJoin", Codec.BOOLEAN),
                    (cfg, value) -> cfg.playerJoin = value,
                    cfg -> cfg.playerJoin)
            .add()
            .append(new KeyedCodec<>("PlayerLeave", Codec.BOOLEAN),
                    (cfg, value) -> cfg.playerLeave = value,
                    cfg -> cfg.playerLeave)
            .add()
            .append(new KeyedCodec<>("WorldEnter", Codec.BOOLEAN),
                    (cfg, value) -> cfg.worldEnter = value,
                    cfg -> cfg.worldEnter)
            .add()
            .append(new KeyedCodec<>("WorldLeave", Codec.BOOLEAN),
                    (cfg, value) -> cfg.worldLeave = value,
                    cfg -> cfg.worldLeave)
            .add()
            .append(new KeyedCodec<>("PlayerDeath", Codec.BOOLEAN),
                    (cfg, value) -> cfg.playerDeath = value,
                    cfg -> cfg.playerDeath)
            .add()
             .append(new KeyedCodec<>("PlayerKill", Codec.BOOLEAN),
                     (cfg, value) -> cfg.playerKill = value,
                     cfg -> cfg.playerKill)
             .add()
             .append(new KeyedCodec<>("ServerStartEmbed", Codec.BOOLEAN),
                     (cfg, value) -> cfg.serverStartEmbed = value,
                     cfg -> cfg.serverStartEmbed)
             .add()
             .append(new KeyedCodec<>("ServerStartEmbedColor", Codec.STRING),
                     (cfg, value) -> cfg.serverStartEmbedColor = value,
                     cfg -> cfg.serverStartEmbedColor)
             .add()
             .append(new KeyedCodec<>("ServerStopEmbed", Codec.BOOLEAN),
                     (cfg, value) -> cfg.serverStopEmbed = value,
                     cfg -> cfg.serverStopEmbed)
             .add()
             .append(new KeyedCodec<>("ServerStopEmbedColor", Codec.STRING),
                     (cfg, value) -> cfg.serverStopEmbedColor = value,
                     cfg -> cfg.serverStopEmbedColor)
             .add()
             .append(new KeyedCodec<>("PlayerJoinEmbed", Codec.BOOLEAN),
                     (cfg, value) -> cfg.playerJoinEmbed = value,
                     cfg -> cfg.playerJoinEmbed)
             .add()
             .append(new KeyedCodec<>("PlayerJoinEmbedColor", Codec.STRING),
                     (cfg, value) -> cfg.playerJoinEmbedColor = value,
                     cfg -> cfg.playerJoinEmbedColor)
             .add()
             .append(new KeyedCodec<>("PlayerLeaveEmbed", Codec.BOOLEAN),
                     (cfg, value) -> cfg.playerLeaveEmbed = value,
                     cfg -> cfg.playerLeaveEmbed)
             .add()
             .append(new KeyedCodec<>("PlayerLeaveEmbedColor", Codec.STRING),
                     (cfg, value) -> cfg.playerLeaveEmbedColor = value,
                     cfg -> cfg.playerLeaveEmbedColor)
             .add()
             .append(new KeyedCodec<>("WorldEnterEmbed", Codec.BOOLEAN),
                     (cfg, value) -> cfg.worldEnterEmbed = value,
                     cfg -> cfg.worldEnterEmbed)
             .add()
             .append(new KeyedCodec<>("WorldEnterEmbedColor", Codec.STRING),
                     (cfg, value) -> cfg.worldEnterEmbedColor = value,
                     cfg -> cfg.worldEnterEmbedColor)
             .add()
             .append(new KeyedCodec<>("WorldLeaveEmbed", Codec.BOOLEAN),
                     (cfg, value) -> cfg.worldLeaveEmbed = value,
                     cfg -> cfg.worldLeaveEmbed)
             .add()
             .append(new KeyedCodec<>("WorldLeaveEmbedColor", Codec.STRING),
                     (cfg, value) -> cfg.worldLeaveEmbedColor = value,
                     cfg -> cfg.worldLeaveEmbedColor)
             .add()
             .append(new KeyedCodec<>("PlayerDeathEmbed", Codec.BOOLEAN),
                     (cfg, value) -> cfg.playerDeathEmbed = value,
                     cfg -> cfg.playerDeathEmbed)
             .add()
             .append(new KeyedCodec<>("PlayerDeathEmbedColor", Codec.STRING),
                     (cfg, value) -> cfg.playerDeathEmbedColor = value,
                     cfg -> cfg.playerDeathEmbedColor)
             .add()
             .append(new KeyedCodec<>("PlayerKillEmbed", Codec.BOOLEAN),
                     (cfg, value) -> cfg.playerKillEmbed = value,
                     cfg -> cfg.playerKillEmbed)
             .add()
             .append(new KeyedCodec<>("PlayerKillEmbedColor", Codec.STRING),
                     (cfg, value) -> cfg.playerKillEmbedColor = value,
                     cfg -> cfg.playerKillEmbedColor)
             .add()
             .append(new KeyedCodec<>("ServerStartEmbedContentType", Codec.STRING),
                     (cfg, value) -> cfg.serverStartEmbedContentType = value,
                     cfg -> cfg.serverStartEmbedContentType)
             .add()
             .append(new KeyedCodec<>("ServerStopEmbedContentType", Codec.STRING),
                     (cfg, value) -> cfg.serverStopEmbedContentType = value,
                     cfg -> cfg.serverStopEmbedContentType)
             .add()
             .append(new KeyedCodec<>("PlayerJoinEmbedContentType", Codec.STRING),
                     (cfg, value) -> cfg.playerJoinEmbedContentType = value,
                     cfg -> cfg.playerJoinEmbedContentType)
             .add()
             .append(new KeyedCodec<>("PlayerLeaveEmbedContentType", Codec.STRING),
                     (cfg, value) -> cfg.playerLeaveEmbedContentType = value,
                     cfg -> cfg.playerLeaveEmbedContentType)
             .add()
             .append(new KeyedCodec<>("WorldEnterEmbedContentType", Codec.STRING),
                     (cfg, value) -> cfg.worldEnterEmbedContentType = value,
                     cfg -> cfg.worldEnterEmbedContentType)
             .add()
             .append(new KeyedCodec<>("WorldLeaveEmbedContentType", Codec.STRING),
                     (cfg, value) -> cfg.worldLeaveEmbedContentType = value,
                     cfg -> cfg.worldLeaveEmbedContentType)
             .add()
             .append(new KeyedCodec<>("PlayerDeathEmbedContentType", Codec.STRING),
                     (cfg, value) -> cfg.playerDeathEmbedContentType = value,
                     cfg -> cfg.playerDeathEmbedContentType)
             .add()
             .append(new KeyedCodec<>("PlayerKillEmbedContentType", Codec.STRING),
                     (cfg, value) -> cfg.playerKillEmbedContentType = value,
                     cfg -> cfg.playerKillEmbedContentType)
             .add()
             .build();

    private boolean serverStart = true;
    private boolean serverStop = true;
    private boolean playerJoin = true;
    private boolean playerLeave = true;
    private boolean worldEnter = true;
    private boolean worldLeave = true;
    private boolean playerDeath = true;
    private boolean playerKill = true;

    private boolean serverStartEmbed = false;
    private String serverStartEmbedColor = "#00FF00";
    private boolean serverStopEmbed = false;
    private String serverStopEmbedColor = "#FF0000";
    private boolean playerJoinEmbed = false;
    private String playerJoinEmbedColor = "#5865F2";
    private boolean playerLeaveEmbed = false;
    private String playerLeaveEmbedColor = "#5865F2";
    private boolean worldEnterEmbed = false;
    private String worldEnterEmbedColor = "#5865F2";
    private boolean worldLeaveEmbed = false;
    private String worldLeaveEmbedColor = "#5865F2";
    private boolean playerDeathEmbed = false;
    private String playerDeathEmbedColor = "#FFA500";
    private boolean playerKillEmbed = false;
    private String playerKillEmbedColor = "#FF0000";

    private String serverStartEmbedContentType = "description";
    private String serverStopEmbedContentType = "description";
    private String playerJoinEmbedContentType = "description";
    private String playerLeaveEmbedContentType = "description";
    private String worldEnterEmbedContentType = "description";
    private String worldLeaveEmbedContentType = "description";
    private String playerDeathEmbedContentType = "description";
    private String playerKillEmbedContentType = "description";

    public boolean isServerStart() {
        return serverStart;
    }

    public boolean isServerStop() {
        return serverStop;
    }

    public boolean isPlayerJoin() {
        return playerJoin;
    }

    public boolean isPlayerLeave() {
        return playerLeave;
    }

    public boolean isWorldEnter() {
        return worldEnter;
    }

    public boolean isWorldLeave() {
        return worldLeave;
    }

    public boolean isPlayerDeath() {
        return playerDeath;
    }

    public boolean isPlayerKill() {
        return playerKill;
    }

    public boolean isServerStartEmbed() {
        return serverStartEmbed;
    }

    @NotNull
    public String getServerStartEmbedColor() {
        return serverStartEmbedColor != null ? serverStartEmbedColor : "#00FF00";
    }

    public boolean isServerStopEmbed() {
        return serverStopEmbed;
    }

    @NotNull
    public String getServerStopEmbedColor() {
        return serverStopEmbedColor != null ? serverStopEmbedColor : "#FF0000";
    }

    public boolean isPlayerJoinEmbed() {
        return playerJoinEmbed;
    }

    @NotNull
    public String getPlayerJoinEmbedColor() {
        return playerJoinEmbedColor != null ? playerJoinEmbedColor : "#5865F2";
    }

    public boolean isPlayerLeaveEmbed() {
        return playerLeaveEmbed;
    }

    @NotNull
    public String getPlayerLeaveEmbedColor() {
        return playerLeaveEmbedColor != null ? playerLeaveEmbedColor : "#5865F2";
    }

    public boolean isWorldEnterEmbed() {
        return worldEnterEmbed;
    }

    @NotNull
    public String getWorldEnterEmbedColor() {
        return worldEnterEmbedColor != null ? worldEnterEmbedColor : "#5865F2";
    }

    public boolean isWorldLeaveEmbed() {
        return worldLeaveEmbed;
    }

    @NotNull
    public String getWorldLeaveEmbedColor() {
        return worldLeaveEmbedColor != null ? worldLeaveEmbedColor : "#5865F2";
    }

    public boolean isPlayerDeathEmbed() {
        return playerDeathEmbed;
    }

    @NotNull
    public String getPlayerDeathEmbedColor() {
        return playerDeathEmbedColor != null ? playerDeathEmbedColor : "#FFA500";
    }

    public boolean isPlayerKillEmbed() {
        return playerKillEmbed;
    }

    @NotNull
    public String getPlayerKillEmbedColor() {
        return playerKillEmbedColor != null ? playerKillEmbedColor : "#FF0000";
    }

    @NotNull
    public String getServerStartEmbedContentType() {
        return serverStartEmbedContentType != null ? serverStartEmbedContentType : "description";
    }

    @NotNull
    public String getServerStopEmbedContentType() {
        return serverStopEmbedContentType != null ? serverStopEmbedContentType : "description";
    }

    @NotNull
    public String getPlayerJoinEmbedContentType() {
        return playerJoinEmbedContentType != null ? playerJoinEmbedContentType : "description";
    }

    @NotNull
    public String getPlayerLeaveEmbedContentType() {
        return playerLeaveEmbedContentType != null ? playerLeaveEmbedContentType : "description";
    }

    @NotNull
    public String getWorldEnterEmbedContentType() {
        return worldEnterEmbedContentType != null ? worldEnterEmbedContentType : "description";
    }

    @NotNull
    public String getWorldLeaveEmbedContentType() {
        return worldLeaveEmbedContentType != null ? worldLeaveEmbedContentType : "description";
    }

    @NotNull
    public String getPlayerDeathEmbedContentType() {
        return playerDeathEmbedContentType != null ? playerDeathEmbedContentType : "description";
    }

    @NotNull
    public String getPlayerKillEmbedContentType() {
        return playerKillEmbedContentType != null ? playerKillEmbedContentType : "description";
    }
}