package ru.kainlight.lightvanish.API;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightvanish.Main;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Getter
public final class LightVanishAPI {

    private LightVanishAPI() {}
    private static final LightVanishAPI lightVanishAPI = new LightVanishAPI();
    public static LightVanishAPI get() {
        return lightVanishAPI;
    }

    private final Map<UUID, VanishedPlayer> allVanished = new HashMap<>();
    private final Map<UUID, Settings> vanishedSettings = new HashMap<>();

    @SuppressWarnings("ConstantConditions")
    public Set<VanishedPlayer> getOnlineVanishedPlayers() {
        return allVanished.keySet().stream()
                .filter(uuid -> Bukkit.getServer().getPlayer(uuid) != null)
                .map(uuid -> getVanishedPlayer(Bukkit.getServer().getPlayer(uuid)))
                .collect(Collectors.toSet());
    }

    public VanishedPlayer getVanishedPlayer(@NotNull Player hider) {
        if(allVanished.containsKey(hider.getUniqueId())) {
            return allVanished.get(hider.getUniqueId());
        } else {
            return new VanishedPlayer(Main.getInstance(), hider);
        }
    }

    public Settings getVanishedSettings(UUID uuid) {
        return vanishedSettings.get(uuid);
    }

    public boolean isVanished(Player player) {
        if(player == null) return false;
        return LightVanishAPI.get().getOnlineVanishedPlayers().contains(getVanishedPlayer(player));
    }

    public boolean isVanished(UUID uuid) {
        return LightVanishAPI.get().getAllVanished().containsKey(uuid);
    }

    public void showAll() {
        getOnlineVanishedPlayers().forEach(VanishedPlayer::show);
    }






    @Getter
    public static final class PlayerHideEvent extends PlayerEvent implements Cancellable {

        private static final HandlerList handlers = new HandlerList();
        @Setter
        private boolean isCancelled = false;

        public PlayerHideEvent(Player player) {
            super(player);
        }

        public VanishedPlayer getVanishedPlayer() {
            return LightVanishAPI.get().getVanishedPlayer(player);
        }

        public Set<VanishedPlayer> getVanishedPlayers() {
            return LightVanishAPI.get().getOnlineVanishedPlayers();
        }

        public List<? extends Player> getViewers() {
            return getVanishedPlayer().getViewers();
        }

        public boolean isVanished() {
            return LightVanishAPI.get().isVanished(player);
        }

        public Settings getSettings() {
            return getVanishedPlayer().getSettings();
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

    @Getter
    public static final class PlayerShowEvent extends PlayerEvent implements Cancellable {

        private static final HandlerList handlers = new HandlerList();
        @Setter
        private boolean isCancelled = false;

        public PlayerShowEvent(Player player) {
            super(player);
        }

        public VanishedPlayer getVanishedPlayer() {
            return LightVanishAPI.get().getVanishedPlayer(player);
        }

        public Set<VanishedPlayer> getVanishedPlayers() {
            return LightVanishAPI.get().getOnlineVanishedPlayers();
        }

        public boolean isVanished() {
            return LightVanishAPI.get().isVanished(player);
        }

        public Settings getSettings() {
            return getVanishedPlayer().getSettings();
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


