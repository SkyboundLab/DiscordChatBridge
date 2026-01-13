package net.aerh.discordbridge.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

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
            .build();

    private boolean serverStart = true;
    private boolean serverStop = true;
    private boolean playerJoin = true;
    private boolean playerLeave = true;
    private boolean worldEnter = true;
    private boolean worldLeave = true;
    private boolean playerDeath = true;
    private boolean playerKill = true;

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
}