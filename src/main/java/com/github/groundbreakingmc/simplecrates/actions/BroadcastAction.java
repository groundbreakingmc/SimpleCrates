package com.github.groundbreakingmc.simplecrates.actions;

import com.github.groundbreakingmc.simplecrates.SimpleCrates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class BroadcastAction extends Action.ActionExecutor {

    public BroadcastAction(SimpleCrates plugin, String action) {
        super(plugin, action);
    }

    @Override
    public void execute(@NotNull Player player) {
        final String prefix = super.colorizer.colorize(super.chat.getPlayerPrefix(player));
        final String suffix = super.colorizer.colorize(super.chat.getPlayerPrefix(player));

        final String replaced = super.action
                .replace("{player}", player.getName())
                .replace("{prefix}", prefix)
                .replace("{suffix}", suffix);

        for (final Player target : Bukkit.getOnlinePlayers()) {
            target.sendMessage(replaced);
        }

        Bukkit.getConsoleSender().sendMessage(replaced);
    }
}
