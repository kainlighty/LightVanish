package ru.kainlight.lightvanish.HOOKS.PlaceholderAPI;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

public final class PlaceholderAPI {
    PlaceholderAPI() {}

    private static final PlaceholderAPI placeholderAPI = new PlaceholderAPI();

    public static PlaceholderAPI get() {
        return placeholderAPI;
    }

    public PlaceholderAPIPlugin getPlugin() {
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPIPlugin.getInstance();
        }

        return null;
    }

    public void createLightVanishSection() {
        var config = getPlugin().getConfig();
        ConfigurationSection section = config.getConfigurationSection("expansions.LightVanish");

        if (section == null) {
            section = config.createSection("expansions.LightVanish");
            getPlugin().saveConfig();
        }

        if (section.getString("enabled") == null || section.getString("disabled") == null) {
            section.set("enabled", "&7Включен");
            section.set("disabled", "&cВыключен");
            getPlugin().saveConfig();
        }

    }

    public ConfigurationSection getLightVanishSection() {
        return getPlugin().getConfig().getConfigurationSection("expansions.LightVanish");
    }

}
