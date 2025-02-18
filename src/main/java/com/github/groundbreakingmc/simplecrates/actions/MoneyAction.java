package com.github.groundbreakingmc.simplecrates.actions;

import com.github.groundbreakingmc.simplecrates.SimpleCrates;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class MoneyAction extends Action.ActionExecutor {

    private final int amount;

    public MoneyAction(SimpleCrates plugin, String action) {
        super(plugin, action);

        this.amount = Integer.parseInt(super.action);
    }

    @Override
    public void execute(@NotNull Player player) {
        if (this.amount < 0) {
            super.economy.withdrawPlayer(player, -this.amount);
        } else {
            super.economy.depositPlayer(player, amount);
        }
    }
}
