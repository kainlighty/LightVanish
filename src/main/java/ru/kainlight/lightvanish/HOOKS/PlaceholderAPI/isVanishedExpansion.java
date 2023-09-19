package ru.kainlight.lightvanish.HOOKS.PlaceholderAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightvanish.Main;

@SuppressWarnings("deprecation") @Internal
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

            if (offlinePlayer.getPlayer() != null) {
                if (offlinePlayer.getPlayer().hasMetadata("vanished")) {
                    return enabled;
                } else {
                    return disabled;
                }
            }
        }
        return identifier;
    }

}
