package ru.kainlight.lightvanish.HOOKS.PlaceholderAPI;

import org.bukkit.configuration.ConfigurationSection;
import ru.kainlight.lightvanish.COMMON.lightlibrary.LightLib;
import ru.kainlight.lightvanish.Main;

public final class HPlaceholderAPI {
    private HPlaceholderAPI() {}

    private static final HPlaceholderAPI hPlaceholderAPI = new HPlaceholderAPI();

    public static HPlaceholderAPI get() {
        return hPlaceholderAPI;
    }

    public static me.clip.placeholderapi.PlaceholderAPIPlugin getPlugin() {
        if (Main.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return me.clip.placeholderapi.PlaceholderAPIPlugin.getInstance();
        }

        return null;
    }

    public void loadPlaceholderAPIAndSaveSections() {
        if (getPlugin() != null) {
            createLightVanishSection();
            new isVanishedExpansion(Main.getInstance()).register();
        }
    }

    public void unloadPlaceholderAPI() {
        isVanishedExpansion isVanishedExpansion = new isVanishedExpansion(Main.getInstance());
        if (isVanishedExpansion.isRegistered() && getPlugin() != null) {
            new isVanishedExpansion(Main.getInstance()).unregister();
        }
    }

    private ConfigurationSection getLightVanishSection() {
        if (getPlugin() == null) return null;
        return getPlugin().getConfig().getConfigurationSection("expansions.LightVanish");
    }

    private void createLightVanishSection() {
        var config = getPlugin().getConfig();
        ConfigurationSection section = config.getConfigurationSection("expansions.LightVanish");

        if (section == null) {
            section = config.createSection("expansions.LightVanish");
            getPlugin().saveConfig();

            LightLib.get().logger("&7[PlaceholderAPI] Section LightVanish created");
        }

        if (section.getString("enabled") == null || section.getString("disabled") == null) {
            section.set("enabled", "Vanish: &aEnabled");
            section.set("disabled", "Vanished: &cDisabled");
            getPlugin().saveConfig();

            LightLib.get().logger("&7[PlaceholderAPI] LightVanish values [enabled, disabled] created");
        }

    }

}
