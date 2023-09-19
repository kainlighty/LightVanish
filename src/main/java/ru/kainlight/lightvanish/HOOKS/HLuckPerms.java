package ru.kainlight.lightvanish.HOOKS;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import ru.kainlight.lightvanish.HOLDERS.ConfigHolder;

import java.util.Optional;
import java.util.UUID;

public final class HLuckPerms {
    private HLuckPerms() {}

    private static final HLuckPerms H_LUCK_PERMS = new HLuckPerms();
    public static HLuckPerms get() {
        return H_LUCK_PERMS;
    }

    public static net.luckperms.api.LuckPerms getAPI() {
        return LuckPermsProvider.get();
    }

    public User getUser(UUID uuid) {
        return getAPI().getUserManager().getUser(uuid);
    }

    public String getPlayerPrefix(UUID uuid) {
        Optional<User> user = Optional.ofNullable(getAPI().getUserManager().getUser(uuid));
        return user.map(value -> value.getCachedData().getMetaData().getPrefix()).orElse("");
    }

    public int getGroupWeight(String groupName) {
        Optional<Group> group = Optional.ofNullable(getAPI().getGroupManager().getGroup(groupName));
        return group.map(value -> value.getWeight().orElse(0)).orElse(0);
    }

    public boolean checkGroupWeight(UUID sender, UUID target) {
        if(!ConfigHolder.get().isByGroupWeight()) return false;
        User senderUser = getUser(sender);
        User targetUser = getUser(target);

        String senderGroup = senderUser.getPrimaryGroup();
        String targetGroup = targetUser.getPrimaryGroup();
        int senderGroupWeight = getGroupWeight(senderGroup);
        int targetGroupWeight = getGroupWeight(targetGroup);
        return senderGroupWeight < targetGroupWeight;
    }

}
