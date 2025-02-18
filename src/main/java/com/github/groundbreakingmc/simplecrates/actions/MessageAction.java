package com.github.groundbreakingmc.simplecrates.actions;

import com.github.groundbreakingmc.simplecrates.SimpleCrates;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class MessageAction extends Action.ActionExecutor {

    public MessageAction(SimpleCrates plugin, String action) {
        super(plugin, action);
    }

    @Override
    public void execute(@NotNull Player player) {
        player.sendMessage(super.action);
    }
}
