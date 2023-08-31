package ru.kainlight.lightvanish.HOOKS.PlaceholderAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightvanish.API.LightVanishAPI;
import ru.kainlight.lightvanish.Main;

import java.util.UUID;

@Internal
public final class isVanishedExpansion extends PlaceholderExpansion {

    private final Main plugin;
    public isVanishedExpansion(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return plugin.getDescription().getName().toLowerCase();
    }
    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }
    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
        if (identifier.equalsIgnoreCase("isVanished")) {
            String enabled = getConfigSection().getString("enabled");
            String disabled = getConfigSection().getString("disabled");
            UUID uuid = offlinePlayer.getUniqueId();

            if (offlinePlayer.getPlayer() != null) {
                if (LightVanishAPI.get().getVanished().contains(uuid)) {
                    return enabled;
                } else {
                    return disabled;
                }
            }
        }
        return identifier;
    }

}
