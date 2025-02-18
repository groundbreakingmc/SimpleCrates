package com.github.groundbreakingmc.simplecrates.actions;

import com.github.groundbreakingmc.mylib.utils.player.PlayerUtils;
import com.github.groundbreakingmc.mylib.utils.player.settings.SoundSettings;
import com.github.groundbreakingmc.simplecrates.SimpleCrates;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class SoundAction extends Action.ActionExecutor {

    private final SoundSettings settings;

    public SoundAction(SimpleCrates plugin, String action) {
        super(plugin, action);

        this.settings = SoundSettings.get(action);
    }

    @Override
    public void execute(@NotNull Player player) {
        PlayerUtils.playSound(player, this.settings);
    }
}
