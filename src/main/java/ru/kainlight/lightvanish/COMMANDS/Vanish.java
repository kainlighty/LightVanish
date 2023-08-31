package ru.kainlight.lightvanish.COMMANDS;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightvanish.API.LightVanishEvents;
import ru.kainlight.lightvanish.API.VanishedPlayer;
import ru.kainlight.lightvanish.CONFIGS.ConfigHolder;
import ru.kainlight.lightvanish.API.LightVanishAPI;
import ru.kainlight.lightvanish.Main;

public final class Vanish implements CommandExecutor {

    private final Main plugin;

    public Vanish(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("lightvanish").setExecutor(new Manage(plugin));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String label, String[] args) {
        if (!(sender instanceof Player hider)) return true;
        if (!hider.hasPermission("lightvanish.use")) return true;

        if (args.length == 0) {
            VanishedPlayer vanishedPlayer = LightVanishAPI.get().getVanishedPlayer(hider);
            if (vanishedPlayer.toggle()) {
                String message = LightVanishEvents.PlayerHideEvent.getStringMessage();
                if(message != null) {
                    plugin.getMessenger().sendMessage(hider, message);
                }
                return true;
            } else {
                String message = LightVanishEvents.PlayerShowEvent.getStringMessage();
                if(message != null) {
                    plugin.getMessenger().sendMessage(hider, message);
                }
            }

            return true;
        } else if (args.length == 1 && hider.hasPermission("lightvanish.use.other")) {
            String username = args[0];
            Player target = plugin.getServer().getPlayer(username);
            if (target == null) {
                plugin.getMessenger().sendMessage(sender, ConfigHolder.get().playerNotFoundMessage().replace("<username>", username));
                return true;
            }

            if(sender.equals(target)) {
                plugin.getMessenger().sendMessage(sender, ConfigHolder.get().playerNotFoundMessage().replace("<username>", username));
                return true;
            }

            if (LightVanishAPI.get().getVanishedPlayer(target).toggle()) {
                plugin.getMessenger().sendMessage(hider, ConfigHolder.get().vanishEnableForOtherSenderMessage().replace("<username>", target.getName()));
                plugin.getMessenger().sendMessage(target, ConfigHolder.get().vanishEnableForOtherPlayerMessage().replace("<username>", target.getName()));
            } else {
                plugin.getMessenger().sendMessage(hider, ConfigHolder.get().vanishDisableForOtherSenderMessage().replace("<username>", target.getName()));
                plugin.getMessenger().sendMessage(target, ConfigHolder.get().vanishDisableOtherForPlayerMessage().replace("<username>", target.getName()));
            }

            return true;
        }

        return true;
    }
}
