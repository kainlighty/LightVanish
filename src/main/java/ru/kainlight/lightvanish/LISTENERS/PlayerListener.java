package ru.kainlight.lightvanish.LISTENERS;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;
import ru.kainlight.lightvanish.API.LightVanishAPI;
import ru.kainlight.lightvanish.Main;

import java.util.UUID;

public final class PlayerListener extends PacketAdapter implements Listener {

    private final Main plugin;

    public PlayerListener(Main plugin) {
        super(plugin, ListenerPriority.NORMAL,
                PacketType.Play.Server.BLOCK_ACTION,
                PacketType.Play.Server.WORLD_EVENT,
                PacketType.Play.Server.WORLD_PARTICLES, PacketType.Play.Server.ENTITY_EFFECT,
                PacketType.Play.Server.ENTITY_LOOK);
        this.plugin = plugin;
        //onVanishedPlayerPresence();
    }

    @EventHandler
    public void onPlayerVanishedJoin(PlayerJoinEvent event) {
        LightVanishAPI.get().getVanished().forEach(hiderUUID -> {
            Player hider = plugin.getServer().getPlayer(hiderUUID);
            if (hider != null) {
                LightVanishAPI.get().getVanishedPlayer(hider).hide();
                event.joinMessage(null);
            }
        });
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.BLOCK_ACTION ||
                event.getPacketType() == PacketType.Play.Server.WORLD_EVENT || event.getPacketType() == PacketType.Play.Server.WORLD_PARTICLES ||
                event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT || event.getPacketType() == PacketType.Play.Server.ENTITY_LOOK) {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();

            if (isVanished(uuid)) {
                event.setCancelled(true);
            }
        }
    }


    /*private void onVanishedPlayerPresence() {
            ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL,
                    PacketType.Play.Server.BLOCK_ACTION,
                    PacketType.Play.Server.WORLD_EVENT,
                    PacketType.Play.Server.WORLD_PARTICLES, PacketType.Play.Server.ENTITY_EFFECT,
                    PacketType.Play.Server.ENTITY_LOOK) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    if (event.getPacketType() == PacketType.Play.Server.BLOCK_ACTION ||
                            event.getPacketType() == PacketType.Play.Server.WORLD_EVENT || event.getPacketType() == PacketType.Play.Server.WORLD_PARTICLES ||
                            event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT || event.getPacketType() == PacketType.Play.Server.ENTITY_LOOK) {
                        Player player = event.getPlayer();
                        UUID uuid = player.getUniqueId();

                        if (isVanished(uuid)) {
                            event.setCancelled(true);
                        }
                    }
                }
            });

    }*/

    @EventHandler
    public void onPlayerVanishedQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (isVanished(uuid)) {
            event.quitMessage(null);
        }
    }

    @EventHandler
    public void onPlayerVanishedKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (isVanished(uuid)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.resetPlayerWeather();
        }
    }

    @EventHandler
    public void onPlayerVanishedHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        UUID uuid = player.getUniqueId();

        if (isVanished(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVanishedPlayerPickupItems(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        UUID uuid = player.getUniqueId();

        if (isVanished(uuid) && !player.hasPermission("lightvanish.pickup")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Block clickedBlock = event.getClickedBlock();
        boolean physicalAction = event.getAction().equals(Action.PHYSICAL);

        if (isVanished(uuid) && clickedBlock != null && physicalAction) {
            String blockName = clickedBlock.getType().getKey().getKey();

            if (blockName.contains("pressure_plate")) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onVanishedPlayerPickupArrow(PlayerPickupArrowEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (isVanished(uuid) && !player.hasPermission("lightvanish.pickup")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVanishedPlayerBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (isVanished(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVanishedPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (isVanished(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerVanishedPickupExperience(PlayerPickupExperienceEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (isVanished(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerVanishedDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        UUID uuid = player.getUniqueId();

        if (isVanished(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerVanishedEntityTarget(EntityTargetEvent event) {
        if (!(event.getTarget() instanceof Player player)) return;
        UUID uuid = player.getUniqueId();

        if (isVanished(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerVanishedLivingEntityTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player player)) return;
        UUID uuid = player.getUniqueId();

        if (isVanished(uuid)) {
            event.setCancelled(true);
        }
    }

    private boolean isVanished(UUID uuid) {
        return LightVanishAPI.get().getVanished().contains(uuid);
    }

}
