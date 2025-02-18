package com.github.groundbreakingmc.simplecrates.actions;

import com.github.groundbreakingmc.mylib.utils.player.PlayerUtils;
import com.github.groundbreakingmc.mylib.utils.player.settings.TitleSettings;
import com.github.groundbreakingmc.simplecrates.SimpleCrates;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class TitleAction extends Action.ActionExecutor {

    private final TitleSettings settings;

    public TitleAction(SimpleCrates plugin, String action) {
        super(plugin, action);

        this.settings = TitleSettings.get(super.action);
    }

    @Override
    public void execute(@NotNull Player player) {
        PlayerUtils.showTitle(player, this.settings);
    }
}
