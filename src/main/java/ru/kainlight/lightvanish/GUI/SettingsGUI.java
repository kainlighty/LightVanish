package ru.kainlight.lightvanish.GUI;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightvanish.API.LightVanishAPI;
import ru.kainlight.lightvanish.API.Settings;
import ru.kainlight.lightvanish.COMMON.lightlibrary.UTILS.ItemBuilder;
import ru.kainlight.lightvanish.COMMON.lightlibrary.UTILS.Parser;
import ru.kainlight.lightvanish.Main;

import java.util.Arrays;
import java.util.List;

public class SettingsGUI implements Listener {

    private final Main plugin;

    public SettingsGUI(Main plugin) {
        this.plugin = plugin;
    }

    public Inventory create(@NotNull Player player) {
        String title = Parser.get().hexString(plugin.getGuiConfig().getConfig().getString("title"));
        Inventory inventory = plugin.getServer().createInventory(player, 5 * 9, title);

        ItemStack enableAllItem = new ItemBuilder(Material.BELL).displayName(getAbilityName("enable-all")).defaultFlags().build();
        ItemStack physicalActionsItem = new ItemBuilder(Material.REDSTONE).displayName(getAbilityName("physical-actions")).defaultFlags().build();
        ItemStack animationsItem = new ItemBuilder(Material.BLAZE_POWDER).displayName(getAbilityName("animations")).defaultFlags().build();
        ItemStack pickupsItem = new ItemBuilder(Material.ARROW).displayName(getAbilityName("pickups")).defaultFlags().build();
        ItemStack silentChestItem = new ItemBuilder(Material.CHEST_MINECART).displayName(getAbilityName("silent-chest")).defaultFlags().build();
        ItemStack resetItem = new ItemBuilder(Material.BARRIER).displayName(getAbilityName("disable-all")).defaultFlags().build();

        fillBorder(inventory);

        inventory.setItem(13, enableAllItem);
        inventory.setItem(21, physicalActionsItem);
        inventory.setItem(22, animationsItem);
        inventory.setItem(23, pickupsItem);
        inventory.setItem(31, silentChestItem);
        inventory.setItem(40, resetItem);

        return inventory;
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void handleInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Settings settings = LightVanishAPI.get().getVanishedSettings().get(player.getUniqueId());
        if (settings == null) return;
        if (!Arrays.equals(event.getInventory().getContents(), settings.getMenu().getContents())) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        ItemMeta itemMeta = clickedItem.getItemMeta();
        if (itemMeta == null) return;
        String itemName = itemMeta.getDisplayName();

        if (itemName.equalsIgnoreCase(getAbilityName("enable-all"))) {
            settings.enableAll();
            updateItem(player, false, 21, 22, 23, 31);
        } else if (itemMeta.getDisplayName().equals(getAbilityName("physical-actions"))) {
            settings.setPhysicalActions(!settings.isPhysicalActions());
            updateItem(player, 21, !settings.isPhysicalActions());
        } else if (itemMeta.getDisplayName().equals(getAbilityName("animations"))) {
            settings.setAnimation(!settings.isAnimation());
            updateItem(player, 22, !settings.isAnimation());
        } else if (itemMeta.getDisplayName().equals(getAbilityName("pickups"))) {
            settings.setPickup(!settings.isPickup());
            updateItem(player, 23, !settings.isPickup());
        } else if (itemMeta.getDisplayName().equals(getAbilityName("silent-chest"))) {
            settings.setSilentChest(!settings.isSilentChest());
            updateItem(player, 31, !settings.isSilentChest());
        } else if (itemMeta.getDisplayName().equals(getAbilityName("disable-all"))) {
            settings.disableAll();

            updateItem(player, true, 21, 22, 23, 31);
        }

        event.setCancelled(true);
    }

    private void updateItem(@NotNull Player player, int slot, boolean enabled) {
        Settings settings = LightVanishAPI.get().getVanishedSettings().get(player.getUniqueId());
        if (settings == null) return;
        Inventory inventory = settings.getMenu();

        ItemStack item = inventory.getItem(slot);
        if (item == null || item.getType() == Material.AIR) return;
        ItemMeta itemMeta = item.getItemMeta();
        Enchantment enchantment = Enchantment.ARROW_DAMAGE;

        if (enabled) {
            List<String> lore = Parser.get().hexString(plugin.getGuiConfig().getConfig().getStringList("disabled-lore"));

            itemMeta.setLore(lore);
            itemMeta.removeEnchant(enchantment);
        } else {
            List<String> lore = Parser.get().hexString(plugin.getGuiConfig().getConfig().getStringList("enabled-lore"));

            itemMeta.setLore(lore);
            itemMeta.addEnchant(enchantment, 1, true);
        }

        item.setItemMeta(itemMeta);
        inventory.setItem(slot, item);
        player.updateInventory();

        settings.setMenu(inventory);
    }

    private void updateItem(Player player, boolean enabled, Integer... slots) {
        Arrays.stream(slots).forEach(slot -> {
            updateItem(player, slot, enabled);
        });
    }

    private void fillBorder(Inventory chestInventory) {
        if (chestInventory == null) return;

        int rows = chestInventory.getSize() / 9;
        ItemStack glassPane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).defaultFlags().displayName("").build();

        // Заполнение верхнего и нижнего ряда стеклянными панелями
        for (int i = 0; i < 9; i++) {
            chestInventory.setItem(i, glassPane); // Верхний ряд
            chestInventory.setItem((rows - 1) * 9 + i, glassPane); // Нижний ряд
        }

        // Заполнение левого и правого столбца стеклянными панелями
        for (int i = 1; i < rows - 1; i++) {
            chestInventory.setItem(i * 9, glassPane); // Левый столбец
            chestInventory.setItem(i * 9 + 8, glassPane); // Правый столбец
        }

        ItemStack itemStack = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).defaultFlags().displayName("").build();
        chestInventory.setItem(19, itemStack);
        chestInventory.setItem(25, itemStack);
    }

    private @NotNull String getAbilityName(String ability) {
        return Parser.get().hexString(plugin.getGuiConfig().getConfig().getString("names." + ability));
    }
}
