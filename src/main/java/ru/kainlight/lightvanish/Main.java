package ru.kainlight.lightvanish;

import lombok.Getter;
import org.jetbrains.annotations.ApiStatus.Internal;
import ru.kainlight.lightvanish.API.LightVanishAPI;
import ru.kainlight.lightvanish.COMMANDS.Vanish;
import ru.kainlight.lightvanish.COMMON.lightlibrary.CONFIGS.BukkitConfig;
import ru.kainlight.lightvanish.COMMON.lightlibrary.LightPlayer;
import ru.kainlight.lightvanish.COMMON.lightlibrary.LightPlugin;
import ru.kainlight.lightvanish.COMMON.lightlibrary.UTILS.Initiators;
import ru.kainlight.lightvanish.GUI.SettingsGUI;
import ru.kainlight.lightvanish.HOOKS.PlaceholderAPI.HPlaceholderAPI;
import ru.kainlight.lightvanish.LISTENERS.PlayerListener;
import ru.kainlight.lightvanish.LISTENERS.silentChest.silentChestComplicatedListener;
import ru.kainlight.lightvanish.LISTENERS.silentChest.silentChestListener;

@Getter
@Internal
public final class Main extends LightPlugin {

    @Getter
    private static Main instance;

    @Getter private BukkitConfig guiConfig;

    @Override
    public void onLoad() {
        this.guiConfig = new BukkitConfig(this, "gui", "config.yml");
        guiConfig.updateConfig();
    }

    @Override
    public void onEnable() {
        instance = this;

        this.getCommand("lightvanish").setExecutor(new Vanish(this));
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new SettingsGUI(this), this);
        registerSilentChest();

        HPlaceholderAPI.get().loadPlaceholderAPIAndSaveSections();

        Initiators.startPluginMessage(this);
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
        LightVanishAPI.get().getAllVanished().clear();
    }

    private void registerSilentChest() {
        if(getConfig().getBoolean("abilities.silent-chest.complicated")) {
            this.getServer().getPluginManager().registerEvents(new silentChestComplicatedListener(this), this);
        } else {
            this.getServer().getPluginManager().registerEvents(new silentChestListener(this), this);
        }
    }

}
