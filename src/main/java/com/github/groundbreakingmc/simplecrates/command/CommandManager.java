package com.github.groundbreakingmc.simplecrates.command;

import com.github.groundbreakingmc.simplecrates.SimpleCrates;
import com.github.groundbreakingmc.simplecrates.listeners.DataManager;
import com.github.groundbreakingmc.simplecrates.utils.config.ConfigValues;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;

public final class CommandManager implements TabExecutor {

    private final SimpleCrates plugin;
    private final ConfigValues configValues;
    private final DataManager dataManager;

    public CommandManager(final SimpleCrates plugin) {
        this.plugin = plugin;
        this.configValues = plugin.getConfigValues();
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage("""
                    Usage:
                    - /simplecrates reload - Reload plugin
                    - /simplecrates load - Load data from DB
                    - /simplecrates give <player> <crate> (<amount>) - Give keys""");
            return true;
        }

        switch (args[0]) {
            case "reload" -> {
                try {
                    this.configValues.removeHolograms();
                    this.configValues.setupValues();
                    sender.sendMessage(this.configValues.getReloadMessage());
                } catch (final SerializationException ex) {
                    ex.printStackTrace();
                    sender.sendMessage("Nasral govnom! (check console)");
                }
            }
            case "load" -> Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                for (final Player target : Bukkit.getOnlinePlayers()) {
                    this.dataManager.load(target);
                }
                sender.sendMessage("Successfully loaded data from db!");
            });
            case "give" -> {
                if (args.length < 3) {
                    sender.sendMessage("Usage: /simplecrates give <player> <crate> (<amount>)");
                    return true;
                }

                final Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage("Player not found!");
                    return true;
                }

                if (!this.configValues.getCrateNames().contains(args[2])) {
                    sender.sendMessage("Crate not found!");
                    return true;
                }

                final int amount = args.length > 3 ? Integer.parseInt(args[3]) : 1;
                this.dataManager.addPlayerKeys(target, args[2], amount);
                sender.sendMessage("Successfully given x" + amount + " " + args[1] + " cases to " + target.getName() + "!");
            }
            default -> sender.sendMessage("""
                    Usage:
                    - /simplecrates reload - Reload plugin
                    - /simplecrates load - Load data from DB
                    - /simplecrates give <player> <crate> (<amount>) - Give keys""");
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("simplecrates.admin") && args.length > 0) {
            if (args.length == 1) {
                final List<String> completions = new ArrayList<>();
                final String input = args[0];
                for (final String completion : List.of("reload", "load", "give")) {
                    if (StringUtil.startsWithIgnoreCase(completion, input)) {
                        completions.add(completion);
                    }
                }
                return completions;
            }

            if (args[0].equalsIgnoreCase("give")) {
                if (args.length == 2) {
                    final List<String> completions = new ArrayList<>();
                    final String input = args[1];
                    for (final Player player : Bukkit.getOnlinePlayers()) {
                        if (StringUtil.startsWithIgnoreCase(player.getName(), input)) {
                            completions.add(player.getName());
                        }
                    }
                    return completions;
                }

                if (args.length == 3) {
                    return List.copyOf(this.configValues.getCrateNames());
                }

                if (args.length == 4) {
                    return List.of("<amount>");
                }
            }
        }

        return List.of();
    }
}
