package ru.kainlight.lightvanish.UTILS;

import ru.kainlight.lightvanish.HOOKS.PlaceholderAPI.PlaceholderAPI;
import ru.kainlight.lightvanish.HOOKS.PlaceholderAPI.isVanishedExpansion;
import ru.kainlight.lightvanish.API.LightVanishAPI;
import ru.kainlight.lightvanish.Main;

public final class Initiators {
    Initiators() {}
    private static final Initiators initiators = new Initiators();
    public static Initiators get() {
        return initiators;
    }

    private final String name = Main.getInstance().getDescription().getName();

    public void startPluginMessage() {
        final String version = Main.getInstance().getDescription().getVersion();
        loadPlaceholderAPIAndSaveSections();

        Main.getInstance().getParser().logger("");
        Main.getInstance().getParser().logger("&c » &f" + name + " enabled");
        new GitHubUpdater(Main.getInstance()).start();
        Main.getInstance().getParser().logger("&c » &fVersion: " + version);
        Main.getInstance().getParser().logger("");
    }

    public void stopPluginMessage() {
        LightVanishAPI.get().removePacketListeners();
        LightVanishAPI.get().getVanished().clear();
        LightVanishAPI.unregister();

        unloadPlaceholderAPI();

        Main.getInstance().getParser().logger("");
        Main.getInstance().getParser().logger("&c » &f" + name + " disabled");
        Main.getInstance().getParser().logger("");
    }

    private void loadPlaceholderAPIAndSaveSections() {
        if (PlaceholderAPI.get().getPlugin() != null) {

            if (Main.getInstance().getConfig().getBoolean("placeholderAPIExpansions")) {
                PlaceholderAPI.get().createLightVanishSection();
                new isVanishedExpansion(Main.getInstance()).register();
            } else {
                Main.getInstance().getSLF4JLogger().warn("You have the PlaceholderAPI enabled, but the expansions are not included");
            }

        }
    }

    private void unloadPlaceholderAPI() {
        if (PlaceholderAPI.get().getPlugin() != null) {
            new isVanishedExpansion(Main.getInstance()).unregister();
        }
    }

}
