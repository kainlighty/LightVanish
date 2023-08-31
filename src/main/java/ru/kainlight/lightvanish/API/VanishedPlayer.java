package ru.kainlight.lightvanish.API;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.ApiStatus.Internal;
import ru.kainlight.lightvanish.HOOKS.LuckPerms;
import ru.kainlight.lightvanish.CONFIGS.ConfigHolder;
import ru.kainlight.lightvanish.Main;

import java.util.UUID;

public final class VanishedPlayer {

    private final Main plugin;
    private final Player hider;

    VanishedPlayer(Main plugin, Player hider) {
        this.plugin = plugin;
        this.hider = hider;
    }

    public Player getPlayer() {
        return hider;
    }

    public boolean toggle() {
        if (isVanished()) {
            show();
            return false;
        } else {
            hide();
            return true;
        }
    }

    public void hide() {
        callHideEvent();

        UUID hiderUUID = hider.getUniqueId();
        enableEffects();

        LightVanishAPI.get().getVanished().add(hiderUUID);
        hider.setMetadata("vanished", new FixedMetadataValue(plugin, true));
        hider.setInvulnerable(true);

        Bukkit.getServer().getOnlinePlayers().forEach(online -> {
            UUID onlineUUID = online.getUniqueId();

            if (!hiderUUID.equals(onlineUUID)) {
                if (LuckPerms.get().checkGroupWeight(onlineUUID, hiderUUID)) {
                    if (!online.equals(hider)) {
                        online.hidePlayer(plugin, hider);
                    }
                }
            }
        });
    }

    public void show() {
        callShowEvent();

        UUID hiderUUID = hider.getUniqueId();
        disableEffects();

        LightVanishAPI.get().getVanished().remove(hiderUUID);
        hider.removeMetadata("vanished", plugin);
        hider.setInvulnerable(false);

        plugin.getServer().getOnlinePlayers().forEach(online -> online.showPlayer(plugin, hider));
    }

    public boolean isVanished() {
        return LightVanishAPI.get().getVanished().contains(hider.getUniqueId()) && hider.hasMetadata("vanished");
    }


    @Internal
    private void enableEffects() {
        PotionEffect nightVision = new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false);
        hider.addPotionEffect(nightVision);
        hider.setPlayerWeather(WeatherType.CLEAR);
    }

    @Internal
    private void disableEffects() {
        hider.removePotionEffect(PotionEffectType.NIGHT_VISION);
        hider.resetPlayerWeather();
    }

    @Internal
    private void callHideEvent() {
        var event = new LightVanishEvents.PlayerHideEvent(hider);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        if(event.getMessage() == null) {
            var message = Component.text(ConfigHolder.get().vanishEnableSelfMessage());
            event.setMessage(message);
        }
    }

    @Internal
    private void callShowEvent() {
        var event = new LightVanishEvents.PlayerShowEvent(hider);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        if(event.getMessage() == null) {
            var message = Component.text(ConfigHolder.get().vanishDisableSelfMessage());
            event.setMessage(message);
        }
    }
}
