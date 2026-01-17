package net.aerh.discordbridge.discord.events;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.event.KillFeedEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.aerh.discordbridge.config.EventsConfig;
import net.aerh.discordbridge.config.MessagesConfig;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public final class KillFeed extends EntityEventSystem<EntityStore, KillFeedEvent.Display> {

    private final ComponentType<EntityStore, PlayerRef> playerRefComponent = PlayerRef.getComponentType();
    private final Supplier<EventsConfig> eventsSupplier;
    private final Supplier<MessagesConfig> messagesSupplier;
    private final MessageSender messageSender;

    public KillFeed(
            @NotNull Supplier<EventsConfig> eventsSupplier,
            @NotNull Supplier<MessagesConfig> messagesSupplier,
            @NotNull MessageSender messageSender
    ) {
        super(KillFeedEvent.Display.class);
        this.eventsSupplier = eventsSupplier;
        this.messagesSupplier = messagesSupplier;
        this.messageSender = messageSender;
    }

    @NotNull
    @Override
    public Query<EntityStore> getQuery() {
        return this.playerRefComponent;
    }

    @Override
    public void handle(
            int index,
            @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl KillFeedEvent.Display event
    ) {
        PlayerRef victim = archetypeChunk.getComponent(index, this.playerRefComponent);
        if (victim == null) {
            return;
        }

        handleKillFeedEvent(event.getDamage(), victim, store);
    }

    private void handleKillFeedEvent(@NotNull Damage damage, @NotNull PlayerRef victim, @NotNull Store<EntityStore> store) {
        EventsConfig events = eventsSupplier.get();
        MessagesConfig messages = messagesSupplier.get();
        PlayerRef killer = resolveKiller(damage, store);

        if (killer != null) {
            messageSender.send(events.isPlayerKill(), messages.getPlayerKill(), events.isPlayerKillEmbed(), events.getPlayerKillEmbedColor(), events.getPlayerKillEmbedContentType(),
                    "%killer%", killer.getUsername(),
                    "%victim%", victim.getUsername()
            );
        } else {
            messageSender.send(events.isPlayerDeath(), messages.getPlayerDeath(), events.isPlayerDeathEmbed(), events.getPlayerDeathEmbedColor(), events.getPlayerDeathEmbedContentType(), "%player%", victim.getUsername());
        }
    }

    @Nullable
    private PlayerRef resolveKiller(@NotNull Damage damage, @NotNull Store<EntityStore> store) {
        if (damage.getSource() instanceof Damage.EntitySource entitySource) {
            if (entitySource.getRef().isValid()) {
                return store.getComponent(entitySource.getRef(), playerRefComponent);
            }
        }

        return null;
    }

    @FunctionalInterface
    public interface MessageSender {
        void send(boolean enabled, @NotNull String template, boolean embedEnabled, @NotNull String embedColor, @NotNull String embedContentType, @NotNull String... replacements);
    }
}