package ru.kainlight.lightvanish.API;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightvanish.HOLDERS.ConfigHolder;
import ru.kainlight.lightvanish.HOOKS.HLuckPerms;
import ru.kainlight.lightvanish.Main;
import ru.kainlight.lightvanish.UTILS.Runnables;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class VanishedPlayer {

    private final Main plugin;

    @NotNull
    private final Player hider;

    @Getter @Setter
    private long vanishedTime = 0L;

    VanishedPlayer(Main plugin, @NotNull Player hider) {
        this.plugin = plugin;
        this.hider = hider;
    }

    public Player player() {
        return hider;
    }

    public boolean toggle() {
        if (LightVanishAPI.get().isVanished(hider)) {
            show();
            return false;
        } else {
            hide();
            return true;
        }
    }

    public void hide() {
        if (callHideEvent()) return;
        if(ConfigHolder.get().getDisabledWorlds().contains(hider.getWorld().getName())) return;

        UUID hiderUUID = hider.getUniqueId();
        enableEffects();

        LightVanishAPI.get().getAllVanished().putIfAbsent(hiderUUID, this);
        if (!hider.hasMetadata("vanished")) {
            hider.setMetadata("vanished", new FixedMetadataValue(plugin, true));
            Runnables.getMethods().startActionbar(this);
        }

        getViewers().forEach(online -> {
            UUID onlineUUID = online.getUniqueId();

            if (!hiderUUID.equals(onlineUUID)) {
                if (HLuckPerms.get().checkGroupWeight(onlineUUID, hiderUUID)) {
                    if (!online.equals(hider)) {
                        online.hidePlayer(plugin, hider);
                    }
                }
            }
        });
    }

    public void show() {
        if (!LightVanishAPI.get().isVanished(hider)) return;
        if (callShowEvent()) return;

        disableEffects();
        LightVanishAPI.get().getAllVanished().remove(hider.getUniqueId());
        hider.removeMetadata("vanished", plugin);
        Runnables.getMethods().stopActionbar(hider);
        setVanishedTime(0L);

        plugin.getServer().getOnlinePlayers().forEach(online -> online.showPlayer(plugin, hider));
    }

    public List<? extends Player> getViewers() {
        return plugin.getServer().getOnlinePlayers().stream().filter(player -> player.canSee(hider)).toList();
    }

    public boolean isVanished() {
        return LightVanishAPI.get().isVanished(player());
    }

    public boolean isTemporary() {
        return Runnables.getMethods().getTempTimerTask().containsKey(hider);
    }




    @Internal
    private void enableEffects() {
        hider.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 1, false, false));
        hider.setPlayerWeather(WeatherType.CLEAR);
        hider.setInvulnerable(true);
    }

    @Internal
    private void disableEffects() {
        hider.removePotionEffect(PotionEffectType.NIGHT_VISION);
        hider.resetPlayerWeather();
        hider.setInvulnerable(false);
    }

    @Internal
    private boolean callHideEvent() {
        var event = new LightVanishAPI.PlayerHideEvent(hider);
        plugin.getServer().getPluginManager().callEvent(event);
        return event.isCancelled();
    }

    @Internal
    private boolean callShowEvent() {
        var event = new LightVanishAPI.PlayerShowEvent(hider);
        plugin.getServer().getPluginManager().callEvent(event);
        return event.isCancelled();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VanishedPlayer that = (VanishedPlayer) o;
        return vanishedTime == that.vanishedTime && Objects.equals(hider, that.hider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hider, vanishedTime);
    }

    @Override
    public String toString() {
        return "VanishedPlayer{" +
                "player=" + hider +
                " isVanished=" + isVanished() +
                " isTemporary=" + isTemporary() +
                ", vanishedTime=" + vanishedTime +
                '}';
    }
}
