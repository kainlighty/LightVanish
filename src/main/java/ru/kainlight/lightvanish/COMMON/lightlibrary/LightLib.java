package ru.kainlight.lightvanish.COMMON.lightlibrary;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightvanish.COMMON.lightlibrary.UTILS.Parser;
import ru.kainlight.lightvanish.Main;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class LightLib {
    private LightLib() {}

    public static LightLib get() {
        return new LightLib();
    }

    @SuppressWarnings("all")
    public void updateConfig(Main plugin) {
        // Загрузка текущей конфигурации
        FileConfiguration userConfig = plugin.getConfig();

        // Чтение конфигурации по умолчанию из JAR-файла
        InputStream defaultConfigStream = plugin.getResource("config.yml");
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

        plugin.saveConfig();
    }

    public LightLib logger(@NotNull String message) {
        String messageComponent = Parser.get().hexString(message);
        Bukkit.getServer().getConsoleSender().sendMessage(messageComponent);
        return this;
    }

}