package ru.kainlight.lightvanish.LISTENERS;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import ru.kainlight.lightvanish.API.LightVanishAPI;
import ru.kainlight.lightvanish.Main;

public final class silentChestListener implements Listener {

    private final Main plugin;

    public silentChestListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onVanishedPlayerOpenableAction(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (LightVanishAPI.get().isVanished(player) && !player.isSneaking()
                && event.getHand() == EquipmentSlot.HAND &&
                event.hasBlock() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!player.hasPermission("lightvanish.silent.chest")) return;

            Material clickedType = event.getClickedBlock().getType();
            String blockName = clickedType.name().toLowerCase();

            if (clickedType == Material.ENDER_CHEST) {
                event.setCancelled(true);
                player.openInventory(player.getEnderChest());
                return;
            }

            if (blockName.contains("chest") || blockName.contains("shulker") || blockName.contains("barrel")) {
                if (!player.hasMetadata("lv_chest")) {
                    GameMode gameMode = player.getGameMode();
                    player.setGameMode(GameMode.SPECTATOR);

                    player.setMetadata("lv_chest", new FixedMetadataValue(plugin, true));
                    chestActionScheduler(player, gameMode);
                }
            }

        }
    }

    @EventHandler
    public void onVanishedPlayerInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player player)) return;
        if(player.hasPermission("lightvanish.silent.chest.editing")) return;

        InventoryType type = event.getInventory().getType();
        if(LightVanishAPI.get().isVanished(player)) {
            if(isChest(type)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVanishedPlayerInventoryDrag(InventoryDragEvent event) {
        if(!(event.getWhoClicked() instanceof Player player)) return;
        InventoryType type = event.getInventory().getType();
        if(player.hasPermission("lightvanish.silent.chest.editing")) return;

        if(LightVanishAPI.get().isVanished(player)) {
            if(isChest(type)) {
                event.setCancelled(true);
            }
        }
    }

    private void chestActionScheduler(Player player, GameMode previous) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            player.setGameMode(previous);
            player.removeMetadata("lv_chest", plugin);
        }, 5L);
    }

    private boolean isChest(InventoryType type) {
        return type == InventoryType.CHEST || type == InventoryType.SHULKER_BOX || type == InventoryType.BARREL;
    }

}
