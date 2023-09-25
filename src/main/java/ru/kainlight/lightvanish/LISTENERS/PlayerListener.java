package ru.kainlight.lightvanish.LISTENERS;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;
import ru.kainlight.lightvanish.API.LightVanishAPI;
import ru.kainlight.lightvanish.API.VanishedPlayer;
import ru.kainlight.lightvanish.API.VanishedSettings;
import ru.kainlight.lightvanish.HOLDERS.ConfigHolder;
import ru.kainlight.lightvanish.Main;

@SuppressWarnings("deprecation")
public final class PlayerListener implements Listener {

    private final Main plugin;

    public PlayerListener(Main plugin) {
        this.plugin = plugin;
        new PaperListener(Main.getInstance());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerVanishedReHide(PlayerJoinEvent event) {
        if (!LightVanishAPI.get().getOnlineVanishedPlayers().isEmpty()) {
            LightVanishAPI.get().getOnlineVanishedPlayers().forEach(VanishedPlayer::hide);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerVanishedJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (LightVanishAPI.get().isVanished(player)) {
            if(ConfigHolder.get().isPreventJoinAndQuitMessage()) {
                event.setJoinMessage(null);
            }
        }
    }

    @EventHandler
    public void onPlayerVanishedQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (LightVanishAPI.get().isVanished(player)) {
            VanishedSettings settings = LightVanishAPI.get().getAllSettings().get(player.getUniqueId());
            if(settings.isTemporary()) {
                settings.setTemporary(false);
                settings.getVanishedPlayer().show();
            }
            if(ConfigHolder.get().isPreventJoinAndQuitMessage()) {
                event.setQuitMessage(null);
            }
        }
    }

    @EventHandler
    public void onPlayerVanishedKick(PlayerKickEvent event) {
        Player player = event.getPlayer();

        if (LightVanishAPI.get().isVanished(player)) {
            VanishedSettings settings = LightVanishAPI.get().getAllSettings().get(player.getUniqueId());
            if(settings.isTemporary()) {
                settings.setTemporary(false);
                settings.getVanishedPlayer().show();
            }
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.resetPlayerWeather();
        }
    }

    @EventHandler
    public void onVanishedPlayerAnimation(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        if(isVanished(player)) {
            VanishedSettings settings = LightVanishAPI.get().getAllSettings().get(player.getUniqueId());
            if(settings != null && settings.isAnimation()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVanishedPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!event.getAction().equals(Action.PHYSICAL)) return;
        if(isVanished(player)) {
            VanishedSettings settings = LightVanishAPI.get().getAllSettings().get(player.getUniqueId());
            if(settings != null && settings.isPhysicalActions()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVanishedPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("lightvanish.bypass.worlds")) return;
        String newWorldName = event.getFrom().getName().toLowerCase();

        if (!player.hasPermission("lightvanish.bypass.world." + newWorldName)) {
            if (LightVanishAPI.get().isVanished(player)) {
                if (!ConfigHolder.get().getDisabledWorlds().isEmpty() && ConfigHolder.get().getDisabledWorlds().contains(newWorldName)) {
                    LightVanishAPI.get().getVanishedPlayer(player).show();
                }
            }
        }

    }

    @EventHandler
    public void onVanishedPlayerPickupItems(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if(isVanished(player)) {
            VanishedSettings settings = LightVanishAPI.get().getAllSettings().get(player.getUniqueId());
            if(settings != null && settings.isPickup()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVanishedPlayerPickupArrow(PlayerPickupArrowEvent event) {
        Player player = event.getPlayer();
        if(isVanished(player)) {
            VanishedSettings settings = LightVanishAPI.get().getAllSettings().get(player.getUniqueId());
            if(settings != null && settings.isPickup()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerVanishedHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        isVanishedCancelled(player, event);
    }

    @EventHandler
    public void onVanishedPlayerBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        isVanishedCancelled(player, event);
    }

    @EventHandler
    public void onPlayerVanishedDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        isVanishedCancelled(player, event);
    }

    @EventHandler
    public void onPlayerVanishedDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        isVanishedCancelled(player, event);
    }

    @EventHandler
    public void onPlayerVanishedDamage(EntityDamageByBlockEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        isVanishedCancelled(player, event);
    }

    @EventHandler
    public void onPlayerVanishedEntityTarget(EntityTargetEvent event) {
        if (!(event.getTarget() instanceof Player player)) return;
        isVanishedCancelled(player, event);
    }

    @EventHandler
    public void onPlayerVanishedLivingEntityTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player player)) return;
        isVanishedCancelled(player, event);
    }

    @EventHandler
    public void onPlayerVanishedBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (LightVanishAPI.get().isVanished(player) && !player.hasPermission("lightvanish.bypass.place")) {
            String blockName = event.getBlockPlaced().getType().name().toLowerCase();

            if (!ConfigHolder.get().getBlockedPlace().isEmpty() && ConfigHolder.get().getBlockedPlace().contains(blockName)) {
                event.setCancelled(true);
            }
        }
    }


    private boolean isVanishedCancelled(Player player, Cancellable event) {
        if (LightVanishAPI.get().isVanished(player)) {
            event.setCancelled(true);
        }
        return event.isCancelled();
    }

    private boolean isVanished(Player player) {
        return LightVanishAPI.get().isVanished(player);
    }


}
