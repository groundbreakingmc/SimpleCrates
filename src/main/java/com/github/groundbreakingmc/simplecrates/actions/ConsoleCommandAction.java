package com.github.groundbreakingmc.simplecrates.actions;

import com.github.groundbreakingmc.simplecrates.SimpleCrates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ConsoleCommandAction extends Action.ActionExecutor {

    public ConsoleCommandAction(SimpleCrates plugin, String action) {
        super(plugin, action);
    }

    @Override
    public void execute(@NotNull Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), super.action.replace("{player}", player.getName()));
    }
}
