package com.github.groundbreakingmc.simplecrates.actions;

import com.github.groundbreakingmc.mylib.utils.luckperms.LuckPermsUtils;
import com.github.groundbreakingmc.simplecrates.SimpleCrates;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public final class LuckPermsAction extends Action.ActionExecutor {

    private final String groupName;
    private final Duration duration;

    public LuckPermsAction(SimpleCrates plugin, String action) {
        super(plugin, action);

        final String[] params = action.split(";");
        this.groupName = params[0];
        this.duration = params.length > 1 && !params[1].equals("infinity")
                ? Duration.ofDays(Long.parseLong(params[1]))
                : null;
    }

    @Override
    public void execute(@NotNull Player player) {
        LuckPermsUtils.setPlayerGroup(player.getUniqueId(), this.groupName, this.duration);
    }
}
