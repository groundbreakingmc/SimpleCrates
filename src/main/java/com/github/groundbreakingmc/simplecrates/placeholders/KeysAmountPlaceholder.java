package com.github.groundbreakingmc.simplecrates.placeholders;

import com.github.groundbreakingmc.simplecrates.SimpleCrates;
import com.github.groundbreakingmc.simplecrates.listeners.DataManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public final class KeysAmountPlaceholder extends PlaceholderExpansion {

    private final DataManager dataManager;

    public KeysAmountPlaceholder(final SimpleCrates plugin) {
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "simplecrates";
    }

    @Override
    public @NotNull String getAuthor() {
        return "GroundbreakingMC";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @NotNull String onRequest(final OfflinePlayer player, @NotNull String params) {
        if (player == null || !player.isOnline()) {
            return "Online players only!";
        }

        return Integer.toString(this.dataManager.getPlayerKeys(player.getPlayer(), params));
    }
}
