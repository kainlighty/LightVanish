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
import ru.kainlight.lightvanish.API.VanishedSettings;
import ru.kainlight.lightvanish.COMMON.lightlibrary.BUILDERS.InventoryBuilder;
import ru.kainlight.lightvanish.COMMON.lightlibrary.BUILDERS.ItemBuilder;
import ru.kainlight.lightvanish.COMMON.lightlibrary.UTILS.Parser;
import ru.kainlight.lightvanish.Main;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class SettingsGUI implements Listener {

    private final Main plugin;

    public SettingsGUI(Main plugin) {
        this.plugin = plugin;
    }

    public Inventory create(@NotNull Player player) {
        String title = Parser.get().hexString(plugin.getGuiConfig().getConfig().getString("title"));
        InventoryBuilder builder = new InventoryBuilder(plugin, player, 5 * 9, title, false);

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
        ItemStack disableAllItem = new ItemBuilder(Material.BARRIER)
                .displayName(getAbilityName("disable-all")).defaultFlags()
                .build();

        LinkedList<Integer> slots = new LinkedList<>(List.of(
                19, // misc border
                13, // enable all
                21, 22, 23, 31, // abilities
                40, // disable all
                25// misc border
        ));
        ItemStack miscBorder = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).defaultFlags().displayName(" ").build();
        LinkedList<ItemStack> items = new LinkedList<>(List.of(miscBorder, enableAllItem, physicalActionsItem, animationsItem, pickupsItem, silentChestItem, disableAllItem, miscBorder));
        builder.fillBorder(Material.GRAY_STAINED_GLASS_PANE, true);
        builder.setItems(slots, items);

        return builder.build();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        VanishedSettings settings = LightVanishAPI.get().getSettings(player.getUniqueId());
        if(settings == null) return;
        if(!event.getInventory().equals(settings.getMenu())) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        int slot = event.getSlot();
        boolean status = false;
        if (slot == 13) {
            settings.enableAll();
            updateItem(player, false, 21, 22, 23, 31);
        } else if (slot == 21) {
            status = settings.isPhysicalActions();
            settings.setPhysicalActions(!status);
        } else if (slot == 22) {
            status = settings.isAnimation();
            settings.setAnimation(!status);
        } else if (slot == 23) {
            status = settings.isPickup();
            settings.setPickup(!status);
        } else if (slot == 31) {
            status = settings.isSilentChest();
            settings.setSilentChest(!status);
        } else if (slot == 40) {
            settings.disableAll();
            updateItem(player, false, 21, 22, 23, 31);
        } else {
            event.setCancelled(true);
            return;
        }

        updateItem(player, slot, status);
        event.setCancelled(true);
    }

    private void updateItem(@NotNull Player player, int slot, boolean enabled) {
        VanishedSettings settings = LightVanishAPI.get().getAllSettings().get(player.getUniqueId());
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
