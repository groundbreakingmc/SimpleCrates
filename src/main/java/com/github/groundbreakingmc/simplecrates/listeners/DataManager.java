package com.github.groundbreakingmc.simplecrates.listeners;

import com.github.groundbreakingmc.simplecrates.SimpleCrates;
import com.github.groundbreakingmc.simplecrates.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DataManager implements Listener {

    private final SimpleCrates plugin;
    private final DatabaseManager database;
    private final Map<UUID, Map<String, Integer>> keys;

    public DataManager(final SimpleCrates plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
        this.keys = new HashMap<>();
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> this.load(event.getPlayer()));
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        this.clear(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onKick(final PlayerKickEvent event) {
        this.clear(event);
    }

    public void load(final Player player) {
        final UUID playerUUID = player.getUniqueId();
        final Map<String, Integer> playerKeys = this.database.getPlayerKeys(playerUUID);
        if (!playerKeys.isEmpty()) {
            this.keys.put(playerUUID, playerKeys);
        }
    }

    private void clear(final PlayerEvent event) {
        final UUID playerUUID = event.getPlayer().getUniqueId();
        this.keys.remove(playerUUID);
    }

    public void addPlayerKeys(final Player player, final String crateName, final int amount) {
        this.keys.compute(player.getUniqueId(), (uuid, playerKeys) -> {
            if (playerKeys == null) {
                playerKeys = new HashMap<>();
            }
            playerKeys.merge(crateName, amount, Integer::sum);
            return playerKeys;
        });

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () ->
                this.database.updatePlayerKeys(DatabaseManager.ADD_PLAYER_KEYS, player.getUniqueId(), crateName, amount)
        );
    }

    public boolean playerHasKeys(final Player player, final String crateName, final int amount) {
        if (this.keys.isEmpty()) {
            return false;
        }

        final Map<String, Integer> playerKeys = this.keys.get(player.getUniqueId());
        if (playerKeys == null || playerKeys.isEmpty()) {
            return false;
        }

        final Integer has = playerKeys.get(crateName);
        return has != null && has >= amount;
    }

    public void removePlayerKeys(final Player player, final String crateName, final int amount) {
        final Map<String, Integer> playerKeys = this.keys.get(player.getUniqueId());
        final int has = playerKeys.get(crateName);
        playerKeys.put(crateName, has - amount);
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () ->
                this.database.updatePlayerKeys(DatabaseManager.REMOVE_PLAYER_KEYS, player.getUniqueId(), crateName, amount)
        );
    }

    public int getPlayerKeys(final Player player, final String crateName) {
        if (this.keys.isEmpty()) {
            return 0;
        }

        final Map<String, Integer> playerKeys = this.keys.get(player.getUniqueId());
        return playerKeys != null && !playerKeys.isEmpty()
                ? playerKeys.getOrDefault(crateName, 0)
                : 0;
    }
}
