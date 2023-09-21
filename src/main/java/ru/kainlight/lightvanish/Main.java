package ru.kainlight.lightvanish;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus.Internal;
import ru.kainlight.lightvanish.API.LightVanishAPI;
import ru.kainlight.lightvanish.COMMANDS.Vanish;
import ru.kainlight.lightvanish.COMMON.lightlibrary.CONFIGS.BukkitConfig;
import ru.kainlight.lightvanish.COMMON.lightlibrary.UTILS.Initiators;
import ru.kainlight.lightvanish.HOOKS.PlaceholderAPI.HPlaceholderAPI;
import ru.kainlight.lightvanish.LISTENERS.PlayerListener;
import ru.kainlight.lightvanish.LISTENERS.silentChest.silentChestComplicatedListener;
import ru.kainlight.lightvanish.LISTENERS.silentChest.silentChestListener;
import ru.kainlight.lightvanish.UTILS.Runnables;

@Getter
@Internal
public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    public BukkitConfig messageConfig;

    @Override
    public void onLoad() {
        this.saveDefaultConfig();
        BukkitConfig.saveLanguages(this, "language");
    }

    @Override
    public void onEnable() {
        instance = this;

        this.getCommand("lightvanish").setExecutor(new Vanish(this));
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        registerSilentChest();

        HPlaceholderAPI.get().loadPlaceholderAPIAndSaveSections();

        Initiators.startPluginMessage(this);
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
        LightVanishAPI.get().getAllVanished().clear();
        Runnables.getMethods().stopAll();
    }

    private void registerSilentChest() {
        if(getConfig().getBoolean("abilities.silent-chest.complicated")) {
            this.getServer().getPluginManager().registerEvents(new silentChestComplicatedListener(this), this);
        } else {
            this.getServer().getPluginManager().registerEvents(new silentChestListener(this), this);
        }
    }

}
