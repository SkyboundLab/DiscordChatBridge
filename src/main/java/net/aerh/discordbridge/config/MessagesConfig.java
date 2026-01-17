package net.aerh.discordbridge.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import org.jetbrains.annotations.NotNull;

/**
 * Configuration for customizable message templates.
 */
public final class MessagesConfig {

    public static final BuilderCodec<MessagesConfig> CODEC = BuilderCodec
            .builder(MessagesConfig.class, MessagesConfig::new)
            .append(new KeyedCodec<>("ServerStart", Codec.STRING),
                    (cfg, value) -> cfg.serverStart = value,
                    cfg -> cfg.serverStart)
            .add()
            .append(new KeyedCodec<>("ServerStop", Codec.STRING),
                    (cfg, value) -> cfg.serverStop = value,
                    cfg -> cfg.serverStop)
            .add()
            .append(new KeyedCodec<>("PlayerJoin", Codec.STRING),
                    (cfg, value) -> cfg.playerJoin = value,
                    cfg -> cfg.playerJoin)
            .add()
            .append(new KeyedCodec<>("PlayerLeave", Codec.STRING),
                    (cfg, value) -> cfg.playerLeave = value,
                    cfg -> cfg.playerLeave)
            .add()
            .append(new KeyedCodec<>("WorldEnter", Codec.STRING),
                    (cfg, value) -> cfg.worldEnter = value,
                    cfg -> cfg.worldEnter)
            .add()
            .append(new KeyedCodec<>("WorldLeave", Codec.STRING),
                    (cfg, value) -> cfg.worldLeave = value,
                    cfg -> cfg.worldLeave)
            .add()
            .append(new KeyedCodec<>("PlayerDeath", Codec.STRING),
                    (cfg, value) -> cfg.playerDeath = value,
                    cfg -> cfg.playerDeath)
            .add()
             .append(new KeyedCodec<>("PlayerKill", Codec.STRING),
                     (cfg, value) -> cfg.playerKill = value,
                     cfg -> cfg.playerKill)
             .add()
             .append(new KeyedCodec<>("GameToDiscord", Codec.STRING),
                     (cfg, value) -> cfg.gameToDiscord = value,
                     cfg -> cfg.gameToDiscord)
             .add()
             .build();

    private static final String DEFAULT_SERVER_START = ":white_check_mark: Server is now online!";
    private static final String DEFAULT_SERVER_STOP = ":octagonal_sign: Server is shutting down.";
    private static final String DEFAULT_PLAYER_JOIN = ":inbox_tray: %player% joined the server.";
    private static final String DEFAULT_PLAYER_LEAVE = ":outbox_tray: %player% left the server.";
    private static final String DEFAULT_WORLD_ENTER = ":compass: %player% entered %world%.";
    private static final String DEFAULT_WORLD_LEAVE = ":door: %player% left %world%.";
    private static final String DEFAULT_PLAYER_DEATH = ":skull: %player% died.";
    private static final String DEFAULT_PLAYER_KILL = ":crossed_swords: %killer% eliminated %victim%.";
    private static final String DEFAULT_GAME_TO_DISCORD = "**%player%**: %message%";

    private String serverStart = DEFAULT_SERVER_START;
    private String serverStop = DEFAULT_SERVER_STOP;
    private String playerJoin = DEFAULT_PLAYER_JOIN;
    private String playerLeave = DEFAULT_PLAYER_LEAVE;
    private String worldEnter = DEFAULT_WORLD_ENTER;
    private String worldLeave = DEFAULT_WORLD_LEAVE;
    private String playerDeath = DEFAULT_PLAYER_DEATH;
    private String playerKill = DEFAULT_PLAYER_KILL;
    private String gameToDiscord = DEFAULT_GAME_TO_DISCORD;

    @NotNull
    public String getServerStart() {
        return serverStart == null ? DEFAULT_SERVER_START : serverStart;
    }

    @NotNull
    public String getServerStop() {
        return serverStop == null ? DEFAULT_SERVER_STOP : serverStop;
    }

    @NotNull
    public String getPlayerJoin() {
        return playerJoin == null ? DEFAULT_PLAYER_JOIN : playerJoin;
    }

    @NotNull
    public String getPlayerLeave() {
        return playerLeave == null ? DEFAULT_PLAYER_LEAVE : playerLeave;
    }

    @NotNull
    public String getWorldEnter() {
        return worldEnter == null ? DEFAULT_WORLD_ENTER : worldEnter;
    }

    @NotNull
    public String getWorldLeave() {
        return worldLeave == null ? DEFAULT_WORLD_LEAVE : worldLeave;
    }

    @NotNull
    public String getPlayerDeath() {
        return playerDeath == null ? DEFAULT_PLAYER_DEATH : playerDeath;
    }

    @NotNull
    public String getPlayerKill() {
        return playerKill == null ? DEFAULT_PLAYER_KILL : playerKill;
    }

    @NotNull
    public String getGameToDiscord() {
        return gameToDiscord == null ? DEFAULT_GAME_TO_DISCORD : gameToDiscord;
    }
}