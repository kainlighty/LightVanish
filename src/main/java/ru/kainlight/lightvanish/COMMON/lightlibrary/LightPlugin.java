package ru.kainlight.lightvanish.COMMON.lightlibrary;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kainlight.lightvanish.COMMON.lightlibrary.CONFIGS.BukkitConfig;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Getter
public class LightPlugin extends JavaPlugin {

    private final double CONFIG_VERSION = 1.0;
    public static final boolean paper = isPaper();
    public BukkitConfig messageConfig;

    @Override
    public void onLoad() {
        this.saveDefaultConfig();
        BukkitConfig.saveLanguages(this, "language");
        updateConfig();
        messageConfig.updateConfig();
    }

    @SuppressWarnings("all")
    public void updateConfig() {
        Double version = getConfig().getDouble("config-version");
        if(version != null && version == CONFIG_VERSION) return;
        // Загрузка текущей конфигурации
        FileConfiguration userConfig = getConfig();

        // Чтение конфигурации по умолчанию из JAR-файла
        InputStream defaultConfigStream = getResource("config.yml");
        InputStreamReader inputConfigReader = new InputStreamReader(defaultConfigStream, StandardCharsets.UTF_8);
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(inputConfigReader);

        // Добавление отсутствующих значений из конфигурации по умолчанию и удаление удаленных ключей
        defaultConfig.getKeys(true).forEach(key -> {
            // Если у пользователя нет такого ключа или его значение является значением по умолчанию, добавляем его
            if (!userConfig.contains(key) || userConfig.get(key).equals(defaultConfig.get(key))) {
                userConfig.set(key, defaultConfig.get(key));
            }
        });

        userConfig.getKeys(true).forEach(key -> {
            if (!defaultConfig.contains(key)) {
                userConfig.set(key, null);
            }
        });

        getLogger().warning("config.yml updated");
        getConfig().set("config-version", CONFIG_VERSION);
        saveConfig();
    }

    private static boolean isPaper() {
        try {
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
