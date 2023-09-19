package ru.kainlight.lightvanish.UTILS;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightvanish.API.LightVanishAPI;
import ru.kainlight.lightvanish.API.VanishedPlayer;
import ru.kainlight.lightvanish.COMMON.lightlibrary.LightPlayer;
import ru.kainlight.lightvanish.HOLDERS.ConfigHolder;
import ru.kainlight.lightvanish.Main;

import java.util.HashMap;
import java.util.Map;

@Internal
public final class Runnables {

    @Getter
    private static final Runnables methods = new Runnables();

    private final Map<Player, Integer> actionbarTask = new HashMap<>();
    @Getter
    private final Map<Player, Integer> tempTimerTask = new HashMap<>();

    public void startActionbar(VanishedPlayer vanishedPlayer) {
        Player player = vanishedPlayer.player();
        if(player == null || actionbarTask.get(player) != null) return;

        int id = Bukkit.getServer().getScheduler().runTaskTimer(Main.getInstance(), () -> {
            if(player != null) {
                long vanishedTime = vanishedPlayer.getVanishedTime();
                vanishedTime = vanishedTime + 1;
                boolean isTemporary = vanishedPlayer.isTemporary();

                vanishedPlayer.setVanishedTime(vanishedTime);
                LightPlayer.of(player).sendActionbar(ConfigHolder.get().vanishEnabledActionbarMessage(isTemporary));
            }
        }, 20L, 20L).getTaskId();

        actionbarTask.put(player, id);
    }

    public void startVanishedTemp(VanishedPlayer vanishedPlayer, long seconds) {
        Player player = vanishedPlayer.player();
        if(player == null || tempTimerTask.get(player) != null) return;

        int id = Bukkit.getServer().getScheduler().runTaskLater(Main.getInstance(), () -> {
            vanishedPlayer.show();
            LightPlayer.of(player).sendMessage(ConfigHolder.get().vanishDisableSelfMessage());
        }, seconds * 20L).getTaskId();
        tempTimerTask.put(vanishedPlayer.player(), id);
    }

    public void stopActionbar(@NotNull Player player) {
        Integer id = actionbarTask.remove(player);
        if(id != null) Bukkit.getServer().getScheduler().cancelTask(id);
    }

    public void stopTimer(@NotNull Player player) {
        Integer id = tempTimerTask.remove(player);
        if(id != null) {
            Bukkit.getServer().getScheduler().cancelTask(id);
            LightVanishAPI.get().getVanishedPlayer(player).show();
        }
    }

    public void stopAll() {
        actionbarTask.clear();
        tempTimerTask.clear();
    }
}
