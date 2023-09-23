package ru.kainlight.lightvanish.LISTENERS;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.kainlight.lightvanish.API.LightVanishAPI;
import ru.kainlight.lightvanish.API.Settings;
import ru.kainlight.lightvanish.Main;

import java.util.stream.Collectors;

final class PaperListener implements Listener {

    private final Main plugin;

    PaperListener(Main plugin) {
        this.plugin = plugin;

        try {
            Class.forName("com.destroystokyo.paper.event.server.PaperServerListPingEvent");
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        } catch (ClassNotFoundException e) {
            Main.getInstance().getLogger()
                    .warning("Hiding players in invisibility among the online servers in the list was not enabled due to the fact that you do not have a Paper Core");
        }

        try {
            Class.forName("com.destroystokyo.paper.event.server.PlayerPickupExperienceEvent");
        } catch (ClassNotFoundException ignored) {}
    }

    @EventHandler
    public void onServerPing(PaperServerListPingEvent event) {
        var vanishedPlayers = LightVanishAPI.get().getAllVanished().keySet().stream()
                .filter(uuid -> plugin.getServer().getPlayer(uuid) != null)
                .collect(Collectors.toSet());

        int vanishedCount = vanishedPlayers.size(), onlineCount = Bukkit.getOnlinePlayers().size();
        var playerSample = event.getPlayerSample();

        event.setNumPlayers(onlineCount - vanishedCount);
        playerSample.removeIf(profile -> vanishedPlayers.contains(profile.getId()));
    }

    @EventHandler
    public void onPlayerVanishedPickupExperience(PlayerPickupExperienceEvent event) {
        Player player = event.getPlayer();

        Settings settings = LightVanishAPI.get().getVanishedSettings().get(player.getUniqueId());
        if(settings == null) return;
        if (LightVanishAPI.get().isVanished(player) && settings.isPickup()) {
            event.setCancelled(true);
        }
    }

}
