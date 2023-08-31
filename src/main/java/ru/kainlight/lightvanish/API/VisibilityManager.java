package ru.kainlight.lightvanish.API;

import com.comphenix.protocol.ProtocolLibrary;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus.Internal;
import ru.kainlight.lightvanish.Main;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Internal
final class VisibilityManager extends LightVanishAPI {

    private final Main plugin;
    private final Set<UUID> vanished = new HashSet<>();

    VisibilityManager() {
        this.plugin = Main.getInstance();
    }

    @Override
    public Set<UUID> getVanished() {
        return vanished;
    }

    @Override
    public VanishedPlayer getVanishedPlayer(Player hider) {
        return new VanishedPlayer(plugin, hider);
    }

    @Override
    public void showAll() {
        plugin.getServer().getOnlinePlayers().forEach(online -> {
            VanishedPlayer vanishedPlayer = LightVanishAPI.get().getVanishedPlayer(online);
            if(vanishedPlayer.isVanished()) {
                vanishedPlayer.show();
            }
        });
    }

    @Override
    public void removePacketListeners() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(plugin);
        PaperServerListPingEvent.getHandlerList().unregister(plugin);
    }
}


