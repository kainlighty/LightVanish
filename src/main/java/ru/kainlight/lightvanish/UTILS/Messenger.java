package ru.kainlight.lightvanish.UTILS;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.kainlight.lightvanish.Main;

public final class Messenger {

    private final Main plugin;
    public Messenger(Main plugin) {
        this.plugin = plugin;
    }

    public void sendTitle(Player player, Component title, Component subTitle, int tick1, int tick2, int tick3) {
        Title.Times times = Title.Times.times(Ticks.duration(tick1), Ticks.duration(tick2), Ticks.duration(tick3));
        Title resultTitle = Title.title(title, subTitle, times);
        player.showTitle(resultTitle);
    }

    public void sendTitle(Player player, Component title, Component subTitle) {
        Title resultTitle = Title.title(title, subTitle);
        player.showTitle(resultTitle);
    }

    public void sendClickableMessage(Player player, String message, String hover, String command) {
        Component messageComponent = plugin.getParser().hex(message)
                .clickEvent(ClickEvent.runCommand(command))
                .hoverEvent(HoverEvent.showText(Component.text(hover)));
        player.sendMessage(messageComponent);
    }

    public void sendMessage(CommandSender sender, String message) {
        Component messageComponent = plugin.getParser().hex(message);
        sender.sendMessage(messageComponent);
    }

    public void sendMessage(CommandSender sender, String... message) {
        for (String msg : message) {
            Component messageComponent = plugin.getParser().hex(msg);
            sender.sendMessage(messageComponent);
        }
    }

    public void sendActionbar(Player player, String message) {
        Component messageComponent = plugin.getParser().hex(message);
        player.sendMessage(messageComponent);
    }

    public void sendActionBarLib(Player player, String message) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        plugin.getParser().hexString(message);

        // Создаем пакет типа PacketPlayOutTitle с подтипом ACTIONBAR
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.CHAT);
        packet.getTitleActions().write(0, EnumWrappers.TitleAction.ACTIONBAR);

        // Нам нужно установить сообщение в пакет с помощью IChatBaseComponent
        // Для этого используем метод fromLegacyText класса CraftChatMessage, который преобразует обычный текст в IChatBaseComponent
        packet.getChatComponents().write(0, WrappedChatComponent.fromHandle(WrappedChatComponent.fromLegacyText(message)));

        protocolManager.sendServerPacket(player, packet);
    }

    public void sendHoverMessage(Player player, String message, String hover) {
        Component mainComponent = plugin.getParser().hex(message);
        Component hoverComponent = plugin.getParser().hex(hover);

        mainComponent = mainComponent.hoverEvent(HoverEvent.showText(hoverComponent));
        player.sendMessage(mainComponent);
    }

    public void sendMessageForAll(String message) {
        for (Player onlinePlayers : plugin.getServer().getOnlinePlayers()) {
            sendMessage(onlinePlayers, message);
        }
    }

    public void sendMessageForAll(String... message) {
        for (Player onlinePlayers : plugin.getServer().getOnlinePlayers()) {
            sendMessage(onlinePlayers, message);
        }
    }
}
