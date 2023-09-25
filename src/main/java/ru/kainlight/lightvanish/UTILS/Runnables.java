package ru.kainlight.lightvanish.UTILS;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightvanish.API.VanishedPlayer;
import ru.kainlight.lightvanish.API.VanishedSettings;
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

    public void startActionbar(VanishedPlayer vanishedPlayer) {
        Player player = vanishedPlayer.player();
        if(player == null || !vanishedPlayer.isVanished()) return;

        int id = Bukkit.getServer().getScheduler().runTaskTimer(Main.getInstance(), () -> {
            if(player != null) {
                VanishedSettings settings = vanishedPlayer.getSettings();

                long vanishedTime = settings.getTemporaryTime();
                vanishedTime = vanishedTime + 1;
                settings.setTemporaryTime(vanishedTime);

                boolean isTemporary = settings.isTemporary();
                LightPlayer.of(player).sendActionbar(ConfigHolder.get().vanishEnabledActionbarMessage(isTemporary));
            }
        }, 20L, 20L).getTaskId();

        actionbarTask.put(player, id);
    }

    public void startTemporaryTimer(VanishedPlayer vanishedPlayer, long seconds) {
        Player player = vanishedPlayer.player();
        if(player == null || vanishedPlayer.getSettings().isTemporary()) return;
        vanishedPlayer.getSettings().setTemporaryTime(seconds);

        Bukkit.getServer().getScheduler().runTaskLater(Main.getInstance(), () -> {
            VanishedSettings settings = vanishedPlayer.getSettings();
            if(!settings.isTemporary()) return;
            vanishedPlayer.show();
            settings.setTemporary(false);
            LightPlayer.of(player).sendMessage(ConfigHolder.get().vanishDisableSelfMessage());
        }, seconds * 20L);
    }

    public void stopActionbar(@NotNull Player player) {
        Integer id = actionbarTask.remove(player);
        if(id != null) Bukkit.getServer().getScheduler().cancelTask(id);
    }
}
