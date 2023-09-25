package ru.kainlight.lightvanish.LISTENERS.silentChest;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.kainlight.lightvanish.API.LightVanishAPI;
import ru.kainlight.lightvanish.API.VanishedSettings;
import ru.kainlight.lightvanish.HOLDERS.ConfigHolder;
import ru.kainlight.lightvanish.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class silentChestComplicatedListener implements Listener {

    private final Main plugin;

    public silentChestComplicatedListener(Main plugin) {
        this.plugin = plugin;
        this.updateInterval = ConfigHolder.get().getSilentChestUpdateInterval() + 1L;
    }

    private final long updateInterval;
    private final HashMap<UUID, Long> sleep = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (event.isCancelled() || SilentChest.fakeChest.isEmpty() || !isChest(event.getBlock().getType())) return;

        Map<Player, SilentChest> copySilentPlayers = new HashMap<>(SilentChest.fakeChest);
        copySilentPlayers.forEach((player, chest) -> {
            Location blockLoc = event.getBlock().getLocation();
            Location chestLoc = chest.getLocation();

            if (chestLoc.equals(blockLoc)) {
                chest.getPlayer().closeInventory();
                SilentChest.fakeChest.remove(player);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled() || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!LightVanishAPI.get().isVanished(player)) return;
        UUID playerUUID = player.getUniqueId();

        VanishedSettings settings = LightVanishAPI.get().getAllSettings().get(playerUUID);
        if(settings == null) return;
        if (!settings.isSilentChest()) return;

        Block clickedBlock = event.getClickedBlock();
        Material clickedType = clickedBlock.getType();

        if (clickedType == Material.ENDER_CHEST) {
            event.setCancelled(true);
            player.openInventory(player.getEnderChest());
            return;
        }

        if (!isChest(clickedType)) return;

        Inventory inventory = getChestInventory(clickedBlock);
        if (inventory == null || inventory.getSize() % 9 != 0) return;

        String chestName = null;
        if (inventory instanceof Chest chest) {
            if (chest.getLootTable() != null) {
                event.setCancelled(true);
                return;
            }

            try {
                chestName = chest.getCustomName();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        String finalChestName = chestName;
        Location location = inventory.getLocation();
        Inventory inventory2 = SilentChest.fakeChest.values().stream()
                .filter(chest -> chest.getLocation().equals(location))
                .findFirst()
                .map(SilentChest::getInventory)
                .orElseGet(() -> {
                    int i = (int) (Math.ceil(inventory.getSize() / 9.0D) * 9.0D);
                    Inventory newInventory = finalChestName != null ?
                            plugin.getServer().createInventory(player, i, finalChestName) :
                            plugin.getServer().createInventory(player, i);
                    newInventory.setContents(inventory.getContents());
                    return newInventory;
                });

        event.setCancelled(true);
        player.openInventory(inventory2);
        sleep.put(playerUUID, System.currentTimeMillis() + 100L);

        final boolean isEditing = player.hasPermission("lightvanish.chest.editing");
        SilentChest silentChest = new SilentChest(player, inventory2, location, true, isEditing);
        SilentChest.fakeChest.put(player, silentChest);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSilentInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player) || SilentChest.fakeChest.isEmpty() || !SilentChest.fakeChest.containsKey(player)) return;
        SilentChest.fakeChest.remove(player);
    }

    @EventHandler
    public void onSilentInventoryClickWatcher(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) || SilentChest.fakeChest.isEmpty() || !isChest(event.getInventory().getType())) return;

        UUID playerUUID = player.getUniqueId();
        if (sleep.containsKey(playerUUID) && sleep.get(playerUUID) > System.currentTimeMillis()) {
            event.setCancelled(true);
            return;
        }

        SilentChest.fakeChest.values().stream()
                .filter(chest -> !chest.getPlayer().getUniqueId().equals(playerUUID) && chest.getLocation().equals(event.getInventory().getLocation()))
                .forEach(silentChest -> {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        ItemStack[] contents = event.getInventory().getContents();
                        silentChest.getInventory().setContents(contents);
                    }, updateInterval);
                });
    }

    @EventHandler
    public void onSilentInventoryClickVanished(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) || SilentChest.fakeChest.isEmpty() || !SilentChest.fakeChest.containsKey(player))
            return;

        final SilentChest silentChest = SilentChest.fakeChest.get(player);
        if (!isChest(silentChest.getInventory().getType())) return;
        if (!silentChest.isEditing()) {
            event.setCancelled(true);
            return;
        }

        UUID playerUUID = player.getUniqueId();
        if (sleep.containsKey(playerUUID) && sleep.get(playerUUID) > System.currentTimeMillis()) {
            event.setCancelled(true);
            return;
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Inventory inventory = getChestInventory(silentChest.getLocation().getBlock());
            if (inventory == null) return;

            ItemStack[] contents = event.getInventory().getContents();
            silentChest.getInventory().setContents(contents);
            inventory.setContents(contents);

            updateInventory(inventory, silentChest.getInventory());
        }, updateInterval);
    }

    @EventHandler
    public void onSilentInventoryDragVanished(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) || SilentChest.fakeChest.isEmpty() || !SilentChest.fakeChest.containsKey(player)) return;

        final SilentChest silentChest = SilentChest.fakeChest.get(player);
        if (!isChest(silentChest.getInventory().getType())) return;
        if(!silentChest.isEditing()) event.setCancelled(true);
    }

    private void updateInventory(Inventory inventory, Inventory silentChest) {
        inventory.getViewers().forEach(humanEntity -> {
            if (humanEntity instanceof Player human) {
                human.updateInventory();
            }
        });

        silentChest.getViewers().forEach(humanEntity -> {
            if (humanEntity instanceof Player human) {
                human.updateInventory();
            }

        });
    }

    private Inventory getChestInventory(Block block) {
        if (block.getState() instanceof Chest chest) {
            return chest.getInventory();
        } else if (block.getState() instanceof DoubleChest doubleChest) {
            return doubleChest.getInventory();
        } else if (block.getState() instanceof Barrel barrel) {
            return barrel.getInventory();
        } else if (block.getState() instanceof ShulkerBox shulkerBox) {
            return shulkerBox.getInventory();
        } else if (block.getState() instanceof InventoryHolder inventoryHolder) {
            return inventoryHolder.getInventory();
        } else return null;
    }

    private boolean isChest(Material material) {
        return material == Material.SHULKER_BOX ||
                material == Material.TRAPPED_CHEST || material == Material.CHEST ||
                material == Material.BARREL;
    }

    private boolean isChest(InventoryType inventoryType) {
        return inventoryType == InventoryType.SHULKER_BOX ||
                inventoryType == InventoryType.CHEST ||
                inventoryType == InventoryType.BARREL;
    }
}

