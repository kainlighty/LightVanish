package ru.kainlight.lightvanish.COMMANDS;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightvanish.API.LightVanishAPI;
import ru.kainlight.lightvanish.API.VanishedPlayer;
import ru.kainlight.lightvanish.API.VanishedSettings;
import ru.kainlight.lightvanish.COMMON.lightlibrary.LightLib;
import ru.kainlight.lightvanish.COMMON.lightlibrary.LightPlayer;
import ru.kainlight.lightvanish.HOLDERS.ConfigHolder;
import ru.kainlight.lightvanish.HOOKS.HLuckPerms;
import ru.kainlight.lightvanish.Main;
import ru.kainlight.lightvanish.UTILS.Runnables;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class Vanish implements CommandExecutor {

    private final Main plugin;

    public Vanish(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("lightvanish").setTabCompleter(new Completer());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String label, String[] args) {
        if (!sender.hasPermission("lightvanish.use")) return true;

        if (args.length == 0 && sender.hasPermission("lightvanish.use") && sender instanceof Player hider) {
            VanishedPlayer vanishedPlayer = LightVanishAPI.get().getVanishedPlayer(hider);

            if (vanishedPlayer.toggle()) {
                LightPlayer.of(vanishedPlayer.player()).sendMessage(ConfigHolder.get().vanishEnableSelfMessage());
                return true;
            } else {
                LightPlayer.of(vanishedPlayer.player()).sendMessage(ConfigHolder.get().vanishDisableSelfMessage());
            }

            return true;
        }

        switch (args[0].toLowerCase()) {
            case "test" -> {
                boolean a = Boolean.parseBoolean(args[1]);
                Player player = (Player) sender;
                player.setCanPickupItems(a);
            }
            case "list" -> {
                if (!sender.hasPermission("lightvanish.list")) return true;

                Set<VanishedPlayer> vanisheds = LightVanishAPI.get().getOnlineVanishedPlayers();
                if (vanisheds.isEmpty()) {
                    String message = plugin.getMessageConfig().getConfig().getString("list.empty");
                    LightPlayer.of(sender).sendMessage(message);
                    return true;
                }

                this.listShow(vanisheds, sender);

                return true;
            }

            case "settings" -> {
                if (!(sender instanceof Player player) || !player.hasPermission("lightvanish.settings")) return true;
                VanishedSettings settings = LightVanishAPI.get().getAllSettings().get(player.getUniqueId());
                if (settings != null) {
                    player.openInventory(settings.getMenu());
                    return true;
                } else {
                    String error = plugin.getMessageConfig().getConfig().getString("has-vanished-before");
                    LightPlayer.of(player).sendMessage(error);
                    return true;
                }
            }

            case "show-all", "showall" -> {
                if (!sender.hasPermission("lightvanish.show-all")) return true;

                LightVanishAPI.get().showAll();
                LightPlayer.of(sender).sendMessage(ConfigHolder.get().showAllMessage());
                return true;
            }

            case "reload" -> {
                if (sender instanceof Player) return true;
                plugin.saveDefaultConfig();
                plugin.getMessageConfig().saveDefaultConfig();

                plugin.reloadConfig();
                plugin.getMessageConfig().reloadConfig();

                plugin.getLogger().info("Configurations reloaded");
                return true;
            }

            default -> {
                if (args.length == 1 || args.length == 2 && sender.hasPermission("lightvanish.use.other") && checkCmd(args[0])) { // ? Other vanish
                    String playerName = args[0];
                    if (sender.getName().equals(playerName)) return true;

                    OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(playerName);
                    if (!offlinePlayer.hasPlayedBefore()) {
                        LightPlayer.of(sender).sendMessage(ConfigHolder.get().playerNotFoundMessage().replace("<username>", playerName));
                        return true;
                    }

                    Player player = offlinePlayer.getPlayer();
                    if (player == null) {
                        LightPlayer.of(sender).sendMessage(ConfigHolder.get().playerOfflineMessage().replace("<username>", playerName));
                        return true;
                    }

                    if (sender instanceof Player hider) {
                        if (HLuckPerms.get().checkGroupWeight(hider.getUniqueId(), player.getUniqueId())) {
                            LightPlayer.of(sender).sendMessage(ConfigHolder.get().playerProtectedMessage().replace("<username>", playerName));
                            return true;
                        }
                    }

                    VanishedPlayer vanishedPlayer = LightVanishAPI.get().getVanishedPlayer(player);
                    if (vanishedPlayer.toggle()) {

                        if (args.length == 2) {
                            long seconds = Long.parseLong(args[1]);
                            if (seconds == 0) return true;
                            Runnables.getMethods().startTemporaryTimer(vanishedPlayer, seconds);
                        }

                        LightPlayer.of(player).sendMessage(ConfigHolder.get().vanishEnableForOtherPlayerMessage().replace("<username>", sender.getName()));
                        LightPlayer.of(sender).sendMessage(ConfigHolder.get().vanishEnableForOtherSenderMessage().replace("<username>", playerName));
                        return true;
                    } else {
                        LightPlayer.of(player).sendMessage(ConfigHolder.get().vanishDisableOtherForPlayerMessage().replace("<username>", sender.getName()));
                        LightPlayer.of(sender).sendMessage(ConfigHolder.get().vanishDisableOtherForSenderMessage().replace("<username>", playerName));
                        return true;
                    }
                }
            }
        }

        return true;
    }

    @Deprecated
    private boolean checkCmd(String subCommand) {
        return !subCommand.equalsIgnoreCase("list") && !subCommand.equalsIgnoreCase("show-all");
    }

    private void listShow(Set<VanishedPlayer> vanisheds, CommandSender sender) {
        CompletableFuture.runAsync(() -> {

            String body = plugin.getMessageConfig().getConfig().getString("list.body");
            if (body == null) return;

            String header = plugin.getMessageConfig().getConfig().getString("list.header");
            if (header != null) LightPlayer.of(sender).sendMessage(header);

            vanisheds.forEach(vanishedPlayer -> {
                String finalBody = body;
                VanishedSettings settings = vanishedPlayer.getSettings();

                Player player = vanishedPlayer.player();
                finalBody = finalBody.replace("<username>", player.getName());
                Long vanishedMinutes = TimeUnit.SECONDS.toMinutes(settings.getTemporaryTime());
                Long vanishedSeconds = TimeUnit.SECONDS.toSeconds(settings.getTemporaryTime());

                if (finalBody.contains("<prefix>")) {
                    UUID uuid = player.getUniqueId();
                    String prefix = HLuckPerms.get().getPlayerPrefix(uuid);
                    finalBody = finalBody.replace("<prefix>", prefix);
                }

                String timeInfo = plugin.getMessageConfig().getConfig().getString("list.time")
                        .replace("<minutes>", vanishedMinutes.toString())
                        .replace("<seconds>", vanishedSeconds.toString());

                if (settings.isTemporary()) {
                    finalBody += "&7 ⧗";
                }

                if (sender instanceof Player) {
                    LightPlayer.of(sender).sendHoverMessage(finalBody, timeInfo);
                } else {
                    LightPlayer.of(sender).sendMessage(finalBody);
                }
            });

            String footer = plugin.getMessageConfig().getConfig().getString("list.footer");
            if (footer != null) {
                Integer vanishedSize = vanisheds.size();
                LightPlayer.of(sender).sendMessage(footer.replace("<count>", vanishedSize.toString()));
            }

        });
    }

}


final class Completer implements TabCompleter {

    Completer() {}

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("lightvanish.*")) return null;

        if (args.length == 1) {
            List<String> players = Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).toList();
            List<String> completion = new ArrayList<>(List.of("list", "settings", "show-all"));
            if (!(sender instanceof Player)) {
                completion.add("reload");
            }
            completion.addAll(players);

            return completion;
        }
        return null;
    }
}
