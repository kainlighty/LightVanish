package ru.kainlight.lightvanish.API;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;

@Getter @Setter
public final class Settings {


    private final VanishedPlayer vanishedPlayer;
    private Inventory menu;
    private boolean temporary, animation, pickup, silentChest, physicalActions = false;
    private long temporaryTime = 0L;

    Settings(VanishedPlayer vanishedPlayer, Inventory menu) {
        this.vanishedPlayer = vanishedPlayer;
        this.menu = menu;
    }

    public void enableAll() {
        animation = true;
        pickup = true;
        silentChest = true;
        physicalActions = true;
    }

    public void disableAll() {
        animation = false;
        pickup = false;
        silentChest = false;
        physicalActions = false;
    }
}
