package com.github.groundbreakingmc.simplecrates.listeners;

import com.github.groundbreakingmc.mylib.utils.event.EventCheckUtils;
import com.github.groundbreakingmc.simplecrates.SimpleCrates;
import com.github.groundbreakingmc.simplecrates.utils.CaseUtils;
import com.github.groundbreakingmc.simplecrates.utils.config.ConfigValues;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public final class CrateManager implements Listener {

    private final SimpleCrates plugin;
    private final ConfigValues configValues;
    private final DataManager dataManager;

    public CrateManager(final SimpleCrates plugin) {
        this.plugin = plugin;
        this.configValues = plugin.getConfigValues();
        this.dataManager = plugin.getDataManager();
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (player.getWorld() != this.configValues.getWorld()
                || !EventCheckUtils.clickedOnBlock(event)
                || event.getInteractionPoint() == null) {
            return;
        }

        final Location caseLocation = event.getInteractionPoint().getBlock().getLocation();
        final var crateParams = this.configValues.getCrate(caseLocation);
        if (crateParams == null) {
            return;
        }

        if (!crateParams.getLeft().booleanValue()) {
            if (!this.dataManager.playerHasKeys(player, crateParams.getMiddle().getName(), 1)) {
                player.sendMessage(this.configValues.getNoCasesMessage());
            } else {
                CaseUtils.open(this.plugin, player, crateParams, caseLocation, true);
            }
        }

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (this.configValues.getWorld() != player.getWorld()) {
            return;
        }

        final var crateParams = this.configValues.getCrate(event.getBlock().getLocation());
        if (crateParams != null) {
            event.setCancelled(true);
        }
    }
}
