package ru.kainlight.lightvanish.LISTENERS;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightvanish.API.LightVanishAPI;
import ru.kainlight.lightvanish.Main;

import java.util.List;

public class ServerListener implements Listener {

    private final Main plugin;

    public ServerListener(Main plugin) {
        this.plugin = plugin;
        new ServerPingEvent(plugin);
    }

    /*private void onVanishedPlayerPresenceCount() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Status.Server.SERVER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacketType() == PacketType.Status.Server.SERVER_INFO) {
                    Player player = event.getPlayer();;
                    Set<UUID> vanishedPlayers = LightVanishAPI.get().getVanished();
                    if (vanishedPlayers.contains(player.getUniqueId())) {
                        WrappedServerPing serverPing = event.getPacket().getServerPings().read(0);
                        int vanishedCount = LightVanishAPI.get().getVanished().size();
                        int onlineCount = plugin.getServer().getOnlinePlayers().size();

                        serverPing.setPlayersOnline(onlineCount - vanishedCount);
                        List<WrappedGameProfile> wrappedGameProfiles = new ArrayList<>(serverPing.getPlayers());
                        wrappedGameProfiles.removeIf(hiders -> vanishedPlayers.contains(player.getUniqueId()));

                        serverPing.setPlayers(wrappedGameProfiles);
                        event.getPacket().getServerPings().write(0, serverPing);
                    }
                }

            }
        });
    }*/

    static class ServerPingEvent implements Listener {
        final boolean includeOnlineCount = Main.getInstance().getConfig().getBoolean("abilities.include-online-count", false);

        ServerPingEvent(Main plugin) {
            if (includeOnlineCount) return;
            try {
                Class.forName("com.destroystokyo.paper.event.server.PaperServerListPingEvent");
                plugin.getServer().getPluginManager().registerEvents(this, plugin);
            } catch (ClassNotFoundException e) {
                plugin.getSLF4JLogger()
                        .warn("Hiding players in invisibility among the online servers in the list was not enabled due to the fact that you do not have a Paper Core", e.getCause());
            }
        }

        @EventHandler
        public void onServerPing(PaperServerListPingEvent event) {
            @NotNull var vanishedPlayers = LightVanishAPI.get().getVanished();
            int vanishedCount = vanishedPlayers.size();
            int nonVanishedCount = Bukkit.getOnlinePlayers().size();
            event.setNumPlayers(nonVanishedCount - vanishedCount);
            List<PlayerProfile> playerSample = event.getPlayerSample();

            playerSample.removeIf(profile -> vanishedPlayers.contains(profile.getId()));
        }
    }
}
