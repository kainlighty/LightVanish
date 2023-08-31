package ru.kainlight.lightvanish.API;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public final class LightVanishEvents {
    public static final class PlayerHideEvent extends PlayerEvent implements Cancellable {

        private static final HandlerList handlers = new HandlerList();
        private boolean isCancelled = false;

        @Nullable
        private static Component message = null;

        public PlayerHideEvent(Player player) {
            super(player);
        }

        public void setMessage(Component message) {
            PlayerHideEvent.message = message;
        }

        public Component getMessage() {
            return message;
        }

        @Deprecated
        public static String getStringMessage() {
            if(message != null) {
                return LegacyComponentSerializer.legacySection().serialize(message);
            } else return null;
        }


        @Override
        public boolean isCancelled() {
            return isCancelled;
        }

        @Override
        public void setCancelled(boolean cancel) {
            isCancelled = cancel;
        }

        public @NotNull Set<UUID> getVanished() {
            return LightVanishAPI.get().getVanished();
        }

        public VanishedPlayer getVanishedPlayer() {
            return LightVanishAPI.get().getVanishedPlayer(player);
        }

        public void showAll() {
            LightVanishAPI.get().showAll();
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return handlers;
        }

        public static @NotNull HandlerList getHandlerList() {
            return handlers;
        }
    }

    public static final class PlayerShowEvent extends PlayerEvent implements Cancellable {

        private static final HandlerList handlers = new HandlerList();
        private boolean isCancelled = false;

        @Nullable
        private static Component message = null;

        public PlayerShowEvent(Player player) {
            super(player);
        }

        public void setMessage(Component message) {
            PlayerShowEvent.message = message;
        }

        public Component getMessage() {
            return message;
        }

        @Deprecated
        public static String getStringMessage() {
            if(message != null) {
                return LegacyComponentSerializer.legacySection().serialize(message);
            } else return null;
        }

        @Override
        public boolean isCancelled() {
            return isCancelled;
        }

        @Override
        public void setCancelled(boolean cancel) {
            isCancelled = cancel;
        }

        public @NotNull Set<UUID> getVanished() {
            return LightVanishAPI.get().getVanished();
        }

        public VanishedPlayer getVanishedPlayer() {
            return LightVanishAPI.get().getVanishedPlayer(player);
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return handlers;
        }

        public static @NotNull HandlerList getHandlerList() {
            return handlers;
        }
    }
}

