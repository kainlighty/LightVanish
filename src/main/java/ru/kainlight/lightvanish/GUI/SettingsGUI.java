package ru.kainlight.lightvanish.GUI;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightvanish.API.LightVanishAPI;
import ru.kainlight.lightvanish.API.Settings;
import ru.kainlight.lightvanish.COMMON.lightlibrary.BUILDERS.InventoryBuilder;
import ru.kainlight.lightvanish.COMMON.lightlibrary.BUILDERS.ItemBuilder;
import ru.kainlight.lightvanish.COMMON.lightlibrary.UTILS.Parser;
import ru.kainlight.lightvanish.Main;

import java.util.*;

public final class SettingsGUI {

    private final Main plugin;

    public SettingsGUI(Main plugin) {
        this.plugin = plugin;
    }

    public Inventory create(@NotNull Player player) {
        String title = Parser.get().hexString(plugin.getGuiConfig().getConfig().getString("title"));
        InventoryBuilder builder = new InventoryBuilder(plugin, player, 5 * 9, title, true);

        ItemStack enableAllItem = new ItemBuilder(Material.BELL)
                .displayName(getAbilityName("enable-all")).defaultFlags()
                .build();
        ItemStack physicalActionsItem = new ItemBuilder(Material.REDSTONE)
                .displayName(getAbilityName("physical-actions")).defaultFlags()
                .build();
        ItemStack animationsItem = new ItemBuilder(Material.BLAZE_POWDER)
                .displayName(getAbilityName("animations")).defaultFlags()
                .build();
        ItemStack pickupsItem = new ItemBuilder(Material.ARROW)
                .displayName(getAbilityName("pickups")).defaultFlags()
                .build();
        ItemStack silentChestItem = new ItemBuilder(Material.CHEST_MINECART)
                .displayName(getAbilityName("silent-chest")).defaultFlags()
                .build();
        ItemStack resetItem = new ItemBuilder(Material.BARRIER)
                .displayName(getAbilityName("disable-all")).defaultFlags()
                .build();

        LinkedList<Integer> slots = new LinkedList<>(List.of(
                19, // misc border
                13, // enable all
                21, 22, 23, 31, // abilities
                40, // disable all
                25 // misc border
        ));
        ItemStack miscBorder = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).defaultFlags().displayName(" ").build();
        LinkedList<ItemStack> items = new LinkedList<>(List.of(miscBorder, enableAllItem, physicalActionsItem, animationsItem, pickupsItem, silentChestItem, resetItem, miscBorder));
        builder.fillBorder(Material.GRAY_STAINED_GLASS_PANE, true);
        builder.setItems(slots, items);

        registerClickEvent(player.getUniqueId(), builder);

        return builder.build();
    }

    private void registerClickEvent(UUID playerUUID, InventoryBuilder builder) {
        builder.clickEvent(event -> {
            Settings settings = LightVanishAPI.get().getVanishedSettings(playerUUID);
            if(settings == null) {
                event.setCancelled(true);
                return;
            }

            Player whoClicked = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                event.setCancelled(true);
                return;
            }

            int slot = event.getSlot();
            if (slot == 13) {
                settings.enableAll();
                updateItem(whoClicked, false, 21, 22, 23, 31);
            } else if (slot == 21) {
                settings.setPhysicalActions(!settings.isPhysicalActions());
                updateItem(whoClicked, slot, !settings.isPhysicalActions());
            } else if (slot == 2) {
                settings.setAnimation(!settings.isAnimation());
                updateItem(whoClicked, slot, !settings.isAnimation());
            } else if (slot == 23) {
                settings.setPickup(!settings.isPickup());
                updateItem(whoClicked, slot, !settings.isPickup());
            } else if (slot == 31) {
                settings.setSilentChest(!settings.isSilentChest());
                updateItem(whoClicked, slot, !settings.isSilentChest());
            } else if (slot == 40) {
                settings.disableAll();
                updateItem(whoClicked, true, 21, 22, 23, 31);
            }

            event.setCancelled(true);
        });
    }

    private void updateItem(@NotNull Player player, int slot, boolean enabled) {
        Settings settings = LightVanishAPI.get().getVanishedSettings().get(player.getUniqueId());
        if (settings == null) return;
        Inventory inventory = settings.getMenu();

        ItemStack item = inventory.getItem(slot);
        if (item == null || item.getType() == Material.AIR) return;
        ItemMeta itemMeta = item.getItemMeta();
        Enchantment enchantment = Enchantment.ARROW_DAMAGE;

        List<String> lore;
        if (enabled) {
            lore = Parser.get().hexString(plugin.getGuiConfig().getConfig().getStringList("disabled-lore"));
            itemMeta.removeEnchant(enchantment);
        } else {
            lore = Parser.get().hexString(plugin.getGuiConfig().getConfig().getStringList("enabled-lore"));
            itemMeta.addEnchant(enchantment, 1, true);
        }

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        inventory.setItem(slot, item);

        player.updateInventory();
    }

    private void updateItem(Player player, boolean enabled, Integer... slots) {
        Iterator<Integer> iterator = Arrays.stream(slots).iterator();
        while (iterator.hasNext()) {
            updateItem(player, iterator.next(), enabled);
        }
    }

    private @NotNull String getAbilityName(String name) {
        return Parser.get().hexString(plugin.getGuiConfig().getConfig().getString("names." + name));
    }
}
