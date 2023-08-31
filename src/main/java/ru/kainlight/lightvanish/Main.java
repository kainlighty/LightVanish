package ru.kainlight.lightvanish;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus.Internal;
import ru.kainlight.lightvanish.API.LightVanishAPI;
import ru.kainlight.lightvanish.COMMANDS.Vanish;
import ru.kainlight.lightvanish.CONFIGS.CustomConfig;
import ru.kainlight.lightvanish.LISTENERS.PlayerListener;
import ru.kainlight.lightvanish.LISTENERS.ServerListener;
import ru.kainlight.lightvanish.UTILS.Initiators;
import ru.kainlight.lightvanish.UTILS.Messenger;
import ru.kainlight.lightvanish.UTILS.Parser;
import ru.kainlight.lightvanish.UTILS.Runnables;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Getter @Internal
public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    public CustomConfig messageConfig;
    private Messenger messenger;
    private final Parser parser = new Parser();
    private Runnables runnables;

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        instance = this;

        messenger = new Messenger(this);
        runnables = new Runnables(this);
        CustomConfig.saveLanguages();

        LightVanishAPI.register();

        getCommand("vanish").setExecutor(new Vanish(this));
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ServerListener(this), this);

        Initiators.get().startPluginMessage();
    }

    @Override
    public void onDisable() {
        Initiators.get().stopPluginMessage();
    }

    public void updateConfig() {
        FileConfiguration configuration = this.getConfig();

        // Загрузка текущей конфигурации
        InputStream defaultConfigStream = this.getResource("config.yml");
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream, StandardCharsets.UTF_8));

        // Добавление отсутствующих значений из конфигурации по умолчанию и удаление удаленных ключей
        for (String key : defaultConfig.getKeys(true)) {
            // Если у пользователя нет такого ключа или его значение является значением по умолчанию, добавляем его
            if (!configuration.contains(key) || configuration.get(key).equals(defaultConfig.get(key))) {
                configuration.set(key, defaultConfig.get(key));
            }
        }
        // Удаление удалённых значений из конфига
        for (String key : configuration.getKeys(true)) {
            if (!defaultConfig.contains(key)) {
                configuration.set(key, null);
            }
        }

        saveConfig();
    }

}
