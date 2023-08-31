package ru.kainlight.lightvanish.COMMANDS;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightvanish.HOOKS.LuckPerms;
import ru.kainlight.lightvanish.CONFIGS.ConfigHolder;
import ru.kainlight.lightvanish.API.LightVanishAPI;
import ru.kainlight.lightvanish.Main;

import java.util.UUID;

final class Manage implements CommandExecutor {

    private final Main plugin;
    Manage(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String s, @NotNull String[] args) {
        if(!sender.hasPermission("lightvanish.use.other")) return true;

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "list" -> {
                if(!sender.hasPermission("lightvanish.list")) return true;

                var vanisheds = LightVanishAPI.get().getVanished();
                if(vanisheds.isEmpty()) {
                    plugin.getMessenger().sendMessage(sender, ConfigHolder.get().listMessages("empty"));
                    return true;
                }

                String header = plugin.getMessageConfig().getConfig().getString("list.header");
                final String body = plugin.getMessageConfig().getConfig().getString("list.body");
                String footer = plugin.getMessageConfig().getConfig().getString("list.footer");
                if(body == null) return true;

                if(header != null && !header.equals("")) plugin.getMessenger().sendMessage(sender, header);

                LightVanishAPI.get().getVanished().forEach(uuids -> {
                    Player vanishedPlayer = plugin.getServer().getPlayer(uuids);
                    if (vanishedPlayer != null) {
                        String finalBody = body;

                        String username = vanishedPlayer.getName();
                        finalBody = finalBody.replace("<username>", username);

                        if(body.contains("<prefix>")) {
                            UUID uuid = vanishedPlayer.getUniqueId();
                            String prefix = LuckPerms.get().getPlayerPrefixSync(uuid);
                            finalBody = finalBody.replace("<prefix>", prefix);
                        }

                        plugin.getMessenger().sendMessage(sender, finalBody);
                    }
                });

                if(footer != null && !footer.equals("")) plugin.getMessenger().sendMessage(sender, footer);
            }
            case "showall" -> {
                if(!sender.hasPermission("lightvanish.showall")) return true;

                LightVanishAPI.get().showAll();
                plugin.getMessenger().sendMessage(sender, ConfigHolder.get().showAllMessage());
            }
            case "reload" -> {
                if(!sender.hasPermission("lightvanish.reload")) return true;

                plugin.saveDefaultConfig();
                plugin.getMessageConfig().saveDefaultConfig();

                plugin.reloadConfig();
                plugin.getMessageConfig().reloadConfig();
                plugin.getMessageConfig().reloadLanguages();

                plugin.getMessenger().sendMessage(sender, ConfigHolder.get().reloadMessage());
            }
            case "reconfig" -> {
                if(sender instanceof Player) return true;

                plugin.saveDefaultConfig();
                plugin.getMessageConfig().saveDefaultConfig();

                plugin.updateConfig();
                plugin.getMessageConfig().updateConfig();

                plugin.getMessenger().sendMessage(sender, ConfigHolder.get().reloadMessage());
            }
        }

        return true;
    }
}
