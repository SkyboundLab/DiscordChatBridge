package io.github.skyboundlab.discordbridge.config;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

/**
 * Configuration for event broadcast settings.
 */
public final class EventsConfig {

    public static final BuilderCodec<EventsConfig> CODEC = BuilderCodec
            .builder(EventsConfig.class, EventsConfig::new)
            .append(new KeyedCodec<>("ServerStart", EventMessageConfig.CODEC),
                    (cfg, value) -> cfg.serverStart = value,
                    cfg -> cfg.serverStart)
            .add()
            .append(new KeyedCodec<>("ServerStop", EventMessageConfig.CODEC),
                    (cfg, value) -> cfg.serverStop = value,
                    cfg -> cfg.serverStop)
            .add()
            .append(new KeyedCodec<>("PlayerJoin", EventMessageConfig.CODEC),
                    (cfg, value) -> cfg.playerJoin = value,
                    cfg -> cfg.playerJoin)
            .add()
            .append(new KeyedCodec<>("PlayerLeave", EventMessageConfig.CODEC),
                    (cfg, value) -> cfg.playerLeave = value,
                    cfg -> cfg.playerLeave)
            .add()
            .append(new KeyedCodec<>("WorldEnter", EventMessageConfig.CODEC),
                    (cfg, value) -> cfg.worldEnter = value,
                    cfg -> cfg.worldEnter)
            .add()
            .append(new KeyedCodec<>("WorldLeave", EventMessageConfig.CODEC),
                    (cfg, value) -> cfg.worldLeave = value,
                    cfg -> cfg.worldLeave)
            .add()
            .append(new KeyedCodec<>("WorldChange", EventMessageConfig.CODEC),
                    (cfg, value) -> cfg.worldChange = value,
                    cfg -> cfg.worldChange)
            .add()
            .append(new KeyedCodec<>("PlayerDeath", EventMessageConfig.CODEC),
                    (cfg, value) -> cfg.playerDeath = value,
                    cfg -> cfg.playerDeath)
            .add()
            .append(new KeyedCodec<>("PlayerKill", PlayerKillConfig.CODEC),
                    (cfg, value) -> cfg.playerKill = value,
                    cfg -> cfg.playerKill)
            .add()
            .append(new KeyedCodec<>("ZoneDiscovery", EventMessageConfig.CODEC),
                    (cfg, value) -> cfg.zoneDiscovery = value,
                    cfg -> cfg.zoneDiscovery)
            .add()
            .build();

    private EventMessageConfig serverStart = new EventMessageConfig(true, "**Server has started**", "#008000");
    private EventMessageConfig serverStop = new EventMessageConfig(true, "**Server has stopped**", "#800000");
    private EventMessageConfig playerJoin = new EventMessageConfig(true, "**%player% joined the game**", "#00FF00");
    private EventMessageConfig playerLeave = new EventMessageConfig(true, "**%player% left the game**", "#FF0000");
    private EventMessageConfig worldEnter = new EventMessageConfig(false, ":compass: %player% entered %world%.", null);
    private EventMessageConfig worldLeave = new EventMessageConfig(false, ":door: %player% left %world%.", null);
    private EventMessageConfig worldChange = new EventMessageConfig(false, ":repeat: %player% moved from %from% to %to%.", null);
    private EventMessageConfig playerDeath = new EventMessageConfig(true, "**%player% died to %cause%**", "#FFA500");
    private PlayerKillConfig playerKill = new PlayerKillConfig(
            true,
            "**%killer% killed %victim%**",
            "**%killer% killed %victim% using %item%**",
            "**%killer% shot %victim% with %projectile%**",
            "**%killer% shot %victim%**"
    );
    private EventMessageConfig zoneDiscovery = new EventMessageConfig(false, ":map: %player% discovered %zone% (%region%).", null);

    public EventMessageConfig getServerStart() {
        return serverStart;
    }

    public EventMessageConfig getServerStop() {
        return serverStop;
    }

    public EventMessageConfig getPlayerJoin() {
        return playerJoin;
    }

    public EventMessageConfig getPlayerLeave() {
        return playerLeave;
    }

    public EventMessageConfig getWorldEnter() {
        return worldEnter;
    }

    public EventMessageConfig getWorldLeave() {
        return worldLeave;
    }

    public EventMessageConfig getWorldChange() {
        return worldChange;
    }

    public EventMessageConfig getPlayerDeath() {
        return playerDeath;
    }

    public PlayerKillConfig getPlayerKill() {
        return playerKill;
    }

    public EventMessageConfig getZoneDiscovery() {
        return zoneDiscovery;
    }
}