package ru.kainlight.lightvanish.API;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public abstract class LightVanishAPI {
    LightVanishAPI() {}

    private static LightVanishAPI instance = null;
    public static void register() {
        if (instance == null) {
            instance = new VisibilityManager();
        }
    }

    public static void unregister() {
        if(instance != null) {
            instance.showAll();
            instance = null;
        }
    }

    public static @NonNull LightVanishAPI get() {
        if (instance == null) {
            throw new UnsupportedOperationException("LightVanish is not available or has not been loaded yet");
        }
        return instance;
    }

    public abstract Set<UUID> getVanished();
    public abstract VanishedPlayer getVanishedPlayer(Player hider);
    public abstract void showAll();

    public abstract void removePacketListeners();
}




