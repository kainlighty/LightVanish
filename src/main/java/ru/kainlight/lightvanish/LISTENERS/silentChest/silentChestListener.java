package ru.kainlight.lightvanish.LISTENERS.silentChest;

import net.kyori.adventure.sound.SoundStop;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import ru.kainlight.lightvanish.API.LightVanishAPI;
import ru.kainlight.lightvanish.COMMON.lightlibrary.LightPlayer;
import ru.kainlight.lightvanish.HOLDERS.ConfigHolder;
import ru.kainlight.lightvanish.Main;

public final class silentChestListener implements Listener {

    private final Main plugin;

    public silentChestListener(Main plugin) {
        this.plugin = plugin;
        this.updateInterval = ConfigHolder.get().getSilentChestUpdateInterval() + 4L;
    }

    private final long updateInterval;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVanishedPlayerOpenableAction(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!LightVanishAPI.get().isVanished(player) || player.isSneaking() || !event.hasBlock() || !(event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

        Block clickedBlock = event.getClickedBlock();
        Material clickedType = clickedBlock.getType();

        if (clickedType == Material.ENDER_CHEST) {
            event.setCancelled(true);
            player.openInventory(player.getEnderChest());
            return;
        }

        if (!isChest(clickedType)) return;

        Inventory inventory = getChestInventory(event.getClickedBlock());
        if (inventory == null) return;
        boolean isEditing = player.hasPermission("lightvanish.silent.chest.editing");
        SilentChest silentChest = new SilentChest(player, inventory, clickedBlock.getLocation(), false, isEditing);

        if (!silentChest.isOpened()) {
            GameMode gameMode = player.getGameMode();
            player.setGameMode(GameMode.SPECTATOR);

            chestActionScheduler(player, gameMode);
        }

        SilentChest.fakeChest.put(player, silentChest);
    }

    @EventHandler
    public void onVanishedPlayerInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) || SilentChest.fakeChest.isEmpty()) return;
        if (!LightVanishAPI.get().isVanished(player)) return;

        InventoryType type = event.getInventory().getType();
        if (!isChest(type)) return;

        SilentChest silentChest = SilentChest.fakeChest.get(player);
        if (!silentChest.isEditing()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onVanishedPlayerInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) || !LightVanishAPI.get().isVanished(player) || SilentChest.fakeChest.isEmpty())
            return;

        InventoryType type = event.getInventory().getType();
        if (!isChest(type)) return;

        SilentChest silentChest = SilentChest.fakeChest.get(player);
        if (!silentChest.isEditing()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVanishedPlayerInventoryClose(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player player) || SilentChest.fakeChest.isEmpty() || !SilentChest.fakeChest.containsKey(player)) return;
        SilentChest.fakeChest.remove(player);
    }

    private void chestActionScheduler(Player player, GameMode previous) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            player.setGameMode(previous);

            SilentChest silentChest = SilentChest.fakeChest.get(player);
            if(silentChest != null) {
                silentChest.setOpened(true);
            }
        }, updateInterval);
    }

    private boolean isChest(InventoryType type) {
        return type == InventoryType.CHEST || type == InventoryType.SHULKER_BOX || type == InventoryType.BARREL;
    }

    public boolean isChest(Material material) {
        return material == Material.CHEST || material == Material.TRAPPED_CHEST || material == Material.SHULKER_BOX || material == Material.BARREL;
    }

    private Inventory getChestInventory(Block block) {
        if (block.getState() instanceof Chest chest) {
            return chest.getInventory();
        } else if (block.getState() instanceof DoubleChest doubleChest) {
            return doubleChest.getInventory();
        } else if (block.getState() instanceof ShulkerBox shulkerBox) {
            return shulkerBox.getInventory();
        } else if (block.getState() instanceof Barrel barrel) {
            return barrel.getInventory();
        } else return null;
    }

}
