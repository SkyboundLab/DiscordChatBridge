package net.aerh.discordbridge;

import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.ShutdownEvent;
import com.hypixel.hytale.server.core.event.events.player.*;
import com.hypixel.hytale.server.core.plugin.*;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.util.Config;
import net.aerh.discordbridge.config.EventsConfig;
import net.aerh.discordbridge.config.DiscordBridgeConfig;
import net.aerh.discordbridge.config.DiscordConfig;
import net.aerh.discordbridge.config.MessagesConfig;
import net.aerh.discordbridge.discord.DiscordBotConnection;
import net.aerh.discordbridge.discord.MessageSanitizer;
import net.aerh.discordbridge.discord.events.KillFeed;
import net.aerh.discordbridge.discord.model.DiscordMessage;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Level;

public final class DiscordBridgePlugin extends JavaPlugin {

    private static final Pattern INBOUND_PLACEHOLDER = Pattern.compile("%(label|username|message)%");
    private static final String DISCORD_LABEL = "[Discord]";

    private static final Color LABEL_COLOR = Color.decode("#5865F2");
    private static final Color DEFAULT_ROLE_COLOR = Color.decode("#99AAB5");
    private static final Color CONTENT_COLOR = Color.decode("#FFFFFF");

    private final Config<DiscordBridgeConfig> config = withConfig(DiscordBridgeConfig.CODEC);
    private DiscordBotConnection botConnection;
    private boolean serverStartMessageSent;
    private boolean serverStopMessageSent;

    public DiscordBridgePlugin(@NotNull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        Path dataDir = getDataDirectory();

        try {
            ensureConfigExists(dataDir);
        } catch (IOException e) {
            getLogger().at(Level.SEVERE).withCause(e).log("Failed to create default config");
        }

        DiscordBridgeConfig cfg = config.get();
        getLogger().at(Level.INFO).log("Configuration loaded successfully");

        getEventRegistry().registerGlobal(EventPriority.NORMAL, PlayerChatEvent.class, this::onPlayerChat);
        getEventRegistry().registerGlobal(PlayerConnectEvent.class, this::onPlayerConnect);
        getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, this::onPlayerDisconnect);
        getEventRegistry().registerGlobal(ShutdownEvent.class, this::onServerShutdown);
        getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, this::onPlayerEnterWorld);
        getEventRegistry().registerGlobal(DrainPlayerFromWorldEvent.class, this::onPlayerLeaveWorld);
        getEntityStoreRegistry().registerSystem(new KillFeed(
                () -> config.get().getEventsConfig(),
                () -> config.get().getMessagesConfig(),
                this::sendEventMessage
        ));
        getLogger().at(Level.INFO).log("Event listeners registered");

        if (!cfg.canStartBot()) {
            getLogger().at(Level.WARNING).log("Discord bridge disabled - missing bot token or channel id");
            return;
        }

        this.botConnection = new DiscordBotConnection(cfg, getLogger(), this::relayDiscordMessage);
        getLogger().at(Level.INFO).log("Discord bot connection initialized");
    }

    @Override
    protected void start() {
        this.serverStartMessageSent = false;
        this.serverStopMessageSent = false;

        if (botConnection == null) {
            getLogger().at(Level.INFO).log("Skipping Discord bot start - not configured");
            return;
        }

        getLogger().at(Level.INFO).log("Starting Discord bot connection...");
        DiscordBridgeConfig cfg = config.get();
        this.botConnection.start()
                .thenRun(() -> {
                    getLogger().at(Level.INFO).log("Discord bot connected successfully to channel %s", cfg.getDiscordConfig().getChannelId());
                    sendServerStartMessage();
                })
                .exceptionally(throwable -> {
                    getLogger().at(Level.SEVERE)
                            .withCause(throwable)
                            .log("Failed to start Discord bridge bot");
                    return null;
                });
    }

    @Override
    protected void shutdown() {
        getLogger().at(Level.INFO).log("Shutting down Discord bridge...");
        sendServerStopMessage();
        if (botConnection != null) {
            botConnection.shutdown();
            botConnection = null;
            getLogger().at(Level.INFO).log("Discord bot disconnected");
        }
    }

    private void onPlayerChat(@NotNull PlayerChatEvent event) {
        DiscordBridgeConfig cfg = config.get();
        if (event.isCancelled()
                || botConnection == null
                || !cfg.isRelayGameToDiscord()) {
            return;
        }

        String cleaned = MessageSanitizer.sanitizeOutgoing(event.getContent(), cfg.getDiscordConfig().isAllowMentions());
        if (cleaned.isEmpty()) {
            return;
        }

        DiscordConfig discordConfig = cfg.getDiscordConfig();
        String webhookUrl = discordConfig.getWebhookUrl();
        if (webhookUrl.isEmpty()) {
            getLogger().at(Level.WARNING).log("Webhook URL not configured; skipping chat message to Discord.");
            return;
        }
        botConnection.sendWebhookMessage(webhookUrl, event.getSender().getUsername(), cleaned);
    }

    private void onPlayerConnect(@NotNull PlayerConnectEvent event) {
        DiscordBridgeConfig cfg = config.get();
        EventsConfig events = cfg.getEventsConfig();
        MessagesConfig messages = cfg.getMessagesConfig();
        sendEventMessage(events.isPlayerJoin(), events.getPlayerJoinMessage(), events.isPlayerJoinEmbed(), events.getPlayerJoinEmbedColor(), "%player%", event.getPlayerRef().getUsername());
    }

    private void onPlayerDisconnect(@NotNull PlayerDisconnectEvent event) {
        DiscordBridgeConfig cfg = config.get();
        EventsConfig events = cfg.getEventsConfig();
        MessagesConfig messages = cfg.getMessagesConfig();
        sendEventMessage(events.isPlayerLeave(), events.getPlayerLeaveMessage(), events.isPlayerLeaveEmbed(), events.getPlayerLeaveEmbedColor(), "%player%", event.getPlayerRef().getUsername());
    }

    private void onPlayerEnterWorld(@NotNull AddPlayerToWorldEvent event) {
        DiscordBridgeConfig cfg = config.get();
        EventsConfig events = cfg.getEventsConfig();
        if (!events.isWorldEnter()) {
            return;
        }

        PlayerRef playerRef = event.getHolder().getComponent(PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }

        sendEventMessage(true, events.getWorldEnterMessage(), events.isWorldEnterEmbed(), events.getWorldEnterEmbedColor(),
                "%player%", playerRef.getUsername(),
                "%world%", event.getWorld().getName()
        );
    }

    private void onPlayerLeaveWorld(@NotNull DrainPlayerFromWorldEvent event) {
        DiscordBridgeConfig cfg = config.get();
        EventsConfig events = cfg.getEventsConfig();
        if (!events.isWorldLeave()) {
            return;
        }

        PlayerRef playerRef = event.getHolder().getComponent(PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }

        World world = event.getWorld();
        String worldName = world.getName();
        sendEventMessage(true, events.getWorldLeaveMessage(), events.isWorldLeaveEmbed(), events.getWorldLeaveEmbedColor(),
                "%player%", playerRef.getUsername(),
                "%world%", worldName
        );
    }

    private void onServerShutdown(@NotNull ShutdownEvent event) {
        sendServerStopMessage();
    }

    private void relayDiscordMessage(@NotNull DiscordMessage message) {
        DiscordBridgeConfig cfg = config.get();
        if (!cfg.isRelayDiscordToGame()) {
            return;
        }

        Universe universe = Universe.get();
        if (universe == null) {
            return;
        }

        String content = MessageSanitizer.sanitizeIncoming(message);
        if (content.isEmpty()) {
            return;
        }

        Message formatted = buildInboundMessage(message, content);
        universe.sendMessage(formatted);
    }

    @NotNull
    private static Message buildInboundMessage(@NotNull DiscordMessage discordMessage, @NotNull String content) {
        String template = "%label% %username%: %message%";

        Message root = Message.empty();
        Matcher matcher = INBOUND_PLACEHOLDER.matcher(template);
        int last = 0;
        boolean hasMessageToken = false;

        while (matcher.find()) {
            if (matcher.start() > last) {
                appendTextSegment(root, template.substring(last, matcher.start()), CONTENT_COLOR);
            }

            switch (matcher.group(1)) {
                case "label" -> appendLabel(root);
                case "username" -> appendUsername(root, discordMessage);
                case "message" -> {
                    appendMessageContent(root, content);
                    hasMessageToken = true;
                }
                default -> {
                }
            }

            last = matcher.end();
        }

        if (last < template.length()) {
            appendTextSegment(root, template.substring(last), CONTENT_COLOR);
        }

        if (!hasMessageToken) {
            appendMessageContent(root, content);
        }

        return root;
    }

    private static void appendLabel(@NotNull Message root) {
        appendTextSegment(root, DISCORD_LABEL, LABEL_COLOR);
    }

    private static void appendUsername(@NotNull Message root, @NotNull DiscordMessage discordMessage) {
        Color color = discordMessage.displayColor() != null ? discordMessage.displayColor() : DEFAULT_ROLE_COLOR;
        appendTextSegment(root, discordMessage.authorName(), color);
    }

    private static void appendMessageContent(@NotNull Message root, @NotNull String content) {
        appendTextSegment(root, content, CONTENT_COLOR);
    }

    private static void appendTextSegment(@NotNull Message root, @NotNull String text, @NotNull Color color) {
        if (text.isEmpty()) {
            return;
        }

        root.insert(Message.raw(text).color(color));
    }

    private void sendToDiscord(@NotNull String message, @NotNull DiscordBridgeConfig cfg) {
        if (message.isBlank()) {
            return;
        }

        DiscordConfig discordConfig = cfg.getDiscordConfig();
        String finalMessage = discordConfig.isAllowMentions() ? message : MessageSanitizer.preventMentions(message);
        if (botConnection == null || !botConnection.isReady()) {
            return;
        }

        botConnection.sendMessage(finalMessage);
    }

    private void sendServerStartMessage() {
        if (serverStartMessageSent) {
            return;
        }

        serverStartMessageSent = true;
        DiscordBridgeConfig cfg = config.get();
        EventsConfig events = cfg.getEventsConfig();
        MessagesConfig messages = cfg.getMessagesConfig();

        sendEventMessage(events.isServerStart(), events.getServerStartMessage(), events.isServerStartEmbed(), events.getServerStartEmbedColor());
    }

    private void sendServerStopMessage() {
        if (serverStopMessageSent) {
            return;
        }
        serverStopMessageSent = true;
        DiscordBridgeConfig cfg = config.get();
        EventsConfig events = cfg.getEventsConfig();
        MessagesConfig messages = cfg.getMessagesConfig();

        sendEventMessage(events.isServerStop(), events.getServerStopMessage(), events.isServerStopEmbed(), events.getServerStopEmbedColor());
    }

    /**
     * Sends an event message to Discord if enabled.
     *
     * @param enabled         whether this event type is enabled
     * @param template        the message template
     * @param embedEnabled    whether to send as embed
     * @param embedColor      the embed color hex
     * @param replacements    pairs of placeholder and value (e.g., "%player%", "Steve")
     */
    private void sendEventMessage(boolean enabled, @NotNull String template, boolean embedEnabled, @NotNull String embedColor, @NotNull String... replacements) {
        if (!enabled) {
            return;
        }

        String message = template;
        for (int i = 0; i < replacements.length - 1; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }

        if (embedEnabled) {
            if (botConnection == null || !botConnection.isReady()) {
                return;
            }
            botConnection.sendEmbed(message, embedColor);
        } else {
            sendToDiscord(message, config.get());
        }
    }

    private void ensureConfigExists(@NotNull Path dataDir) throws IOException {
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
            getLogger().at(Level.INFO).log("Created data directory: %s", dataDir);
        }

        Path configFile = dataDir.resolve("config.json");
        if (!Files.exists(configFile)) {
            try (InputStream defaultConfig = getClass().getResourceAsStream("/config.json")) {
                if (defaultConfig != null) {
                    Files.copy(defaultConfig, configFile);
                    getLogger().at(Level.INFO).log("Created default config.json");
                } else {
                    getLogger().at(Level.WARNING).log("Default config.json not found in resources");
                }
            }
        }
    }
}
