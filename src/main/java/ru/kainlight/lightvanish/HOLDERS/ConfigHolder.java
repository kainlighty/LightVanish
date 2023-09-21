package ru.kainlight.lightvanish.HOLDERS;

import lombok.Getter;
import ru.kainlight.lightvanish.Main;

import java.util.List;

@SuppressWarnings("all")
@Getter
public final class ConfigHolder {
    private ConfigHolder() {}
    private static final ConfigHolder configHolder = new ConfigHolder();
    public static ConfigHolder get() { return configHolder; }

    private boolean preventJoinAndQuitMessage = Main.getInstance().getConfig().getBoolean("abilities.prevent-join-quit-message");
    private boolean includeInOnlineCount = Main.getInstance().getConfig().getBoolean("abilities.include-in-online-count");
    private boolean byGroupWeight = Main.getInstance().getConfig().getBoolean("abilities.by-group-weight");
    private List<String> disabledWorlds = Main.getInstance().getConfig().getStringList("abilities.disabled-worlds");
    private final boolean animationsEnabled = Main.getInstance().getConfig().getBoolean("abilities.animations");

    private final int silentChestMode = Main.getInstance().getConfig().getInt("abilities.silent-chest.mode");
    private final int silentChestUpdateInterval = Main.getInstance().getConfig().getInt("abilities.silent-chest.interval");

    public List<String> getBlockedPlace() {
        List<String> blockedPlace = Main.getInstance().getConfig().getStringList("abilities.blocked-place");
        return blockedPlace;
    }

    public String vanishEnableForOtherSenderMessage() {
        String message = Main.getInstance().getMessageConfig().getConfig().getString("enable.other.sender");
        return message;
    }
    public String vanishEnableForOtherPlayerMessage() {
        String message = Main.getInstance().getMessageConfig().getConfig().getString("enable.other.player");
        return message;
    }
    public String vanishDisableOtherForSenderMessage() {
        String message = Main.getInstance().getMessageConfig().getConfig().getString("disable.other.sender");
        return message;
    }
    public String vanishDisableOtherForPlayerMessage() {
        String message = Main.getInstance().getMessageConfig().getConfig().getString("disable.other.player");
        return message;
    }


    public String vanishEnabledActionbarMessage(boolean isTemporary) {
        String message = Main.getInstance().getMessageConfig().getConfig().getString("enable.actionbar");
        return isTemporary ? message+= "&7 ⧗" : message;
    }
    public String vanishEnableSelfMessage() {
        String message = Main.getInstance().getMessageConfig().getConfig().getString("enable.self");
        return message;
    }
    public String vanishDisableSelfMessage() {
        String message = Main.getInstance().getMessageConfig().getConfig().getString("disable.self");
        return message;
    }

    public String playerProtectedMessage() {
        String message = Main.getInstance().getMessageConfig().getConfig().getString("player-protected");
        return message;
    }

    public String playerOfflineMessage() {
        String message = Main.getInstance().getMessageConfig().getConfig().getString("player-offline");
        return message;
    }

    public String playerNotFoundMessage() {
        String message = Main.getInstance().getMessageConfig().getConfig().getString("player-not-found");
        return message;
    }

    public String showAllMessage() {
        String message = Main.getInstance().getMessageConfig().getConfig().getString("disable.all");
        return message;
    }
}
