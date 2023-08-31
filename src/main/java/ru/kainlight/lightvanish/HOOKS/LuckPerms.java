package ru.kainlight.lightvanish.HOOKS;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import ru.kainlight.lightvanish.Main;

import java.util.Optional;
import java.util.UUID;

public final class LuckPerms {
    LuckPerms() {}

    private static final LuckPerms luckPerms = new LuckPerms();
    public static LuckPerms get() {
        return luckPerms;
    }

    public static net.luckperms.api.LuckPerms getAPI() {
        return LuckPermsProvider.get();
    }

    public String getPlayerPrefixSync(UUID uuid) {
        Optional<User> user = Optional.ofNullable(getAPI().getUserManager().getUser(uuid));
        return user.map(value -> value.getCachedData().getMetaData().getPrefix()).orElse("");
    }

    public int getGroupWeightSync(String groupName) {
        Optional<Group> group = Optional.ofNullable(getAPI().getGroupManager().getGroup(groupName));
        return group.map(value -> value.getWeight().orElse(-1)).orElse(-1);
    }

    private final boolean byGroupWeight = Main.getInstance().getConfig().getBoolean("abilities.by-group-weight", true);
    public boolean checkGroupWeight(UUID sender, UUID target) {
        if(!byGroupWeight) return false;

        String senderGroup = getAPI().getUserManager().getUser(sender).getPrimaryGroup();
        String targetGroup = getAPI().getUserManager().getUser(target).getPrimaryGroup();
        int senderGroupWeight = LuckPermsProvider.get().getGroupManager().getGroup(senderGroup).getWeight().orElse(-1);
        int targetGroupWeight = LuckPermsProvider.get().getGroupManager().getGroup(targetGroup).getWeight().orElse(-1);
        return senderGroupWeight < targetGroupWeight;
    }

}
