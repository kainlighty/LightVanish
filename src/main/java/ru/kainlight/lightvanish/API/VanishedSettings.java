package ru.kainlight.lightvanish.API;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

@Getter @Setter
public final class VanishedSettings {

    private final VanishedPlayer vanishedPlayer;
    private final Inventory menu;
    private boolean temporary, animation, pickup, silentChest, physicalActions = false;
    private long temporaryTime = 0L;

    VanishedSettings(VanishedPlayer vanishedPlayer, Inventory menu) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VanishedSettings settings = (VanishedSettings) o;
        return temporary == settings.temporary && animation == settings.animation && pickup == settings.pickup && silentChest == settings.silentChest && physicalActions == settings.physicalActions && temporaryTime == settings.temporaryTime && Objects.equals(vanishedPlayer, settings.vanishedPlayer) && Objects.equals(menu, settings.menu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vanishedPlayer, menu, temporary, animation, pickup, silentChest, physicalActions, temporaryTime);
    }

    @Override
    public String toString() {
        return "Settings{" +
                "vanishedPlayer=" + vanishedPlayer +
                ", menu=" + menu +
                ", isTemporary=" + temporary +
                ", animations=" + animation +
                ", canPickup=" + pickup +
                ", isSilentChest=" + silentChest +
                ", canPhysicalActions=" + physicalActions +
                ", temporaryTime=" + temporaryTime +
                '}';
    }
}
