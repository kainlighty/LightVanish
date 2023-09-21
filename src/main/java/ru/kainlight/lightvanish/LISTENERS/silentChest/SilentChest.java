package ru.kainlight.lightvanish.LISTENERS.silentChest;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

@Getter
public final class SilentChest {
    static final HashMap<Player, SilentChest> fakeChest = new HashMap<>();

    private final Player player;
    private final Inventory inventory;
    private final Location location;
    @Setter private boolean opened;
    private final boolean editing;

    public SilentChest(Player player, Inventory inventory, Location location, boolean opened, boolean editing) {
        this.player = player;
        this.inventory = inventory;
        this.location = location;
        this.opened = opened;
        this.editing = editing;
    }

}
