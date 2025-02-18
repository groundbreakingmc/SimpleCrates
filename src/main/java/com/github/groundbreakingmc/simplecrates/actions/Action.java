package com.github.groundbreakingmc.simplecrates.actions;

import com.github.groundbreakingmc.simplecrates.SimpleCrates;
import com.github.groundbreakingmc.mylib.colorizer.Colorizer;
import com.github.groundbreakingmc.mylib.utils.vault.VaultUtils;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public enum Action {
    BROADCAST("[BROADCAST]", (plugin, string, key) -> createAction(plugin, string, key, BroadcastAction::new, "[BROADCAST]")),
    CONSOLE("[CONSOLE]", (plugin, string, key) -> createAction(plugin, string, key, ConsoleCommandAction::new, "[CONSOLE]")),
    PLAY_SOUND("[SOUND]", (plugin, string, key) -> createAction(plugin, string, key, SoundAction::new, "[SOUND]")),
    LUCKPERMS("[LUCKPERMS]", (plugin, string, key) -> createAction(plugin, string, key, LuckPermsAction::new, "[LUCKPERMS]")),
    MONEY("[MONEY]", (plugin, string, key) -> createAction(plugin, string, key, MoneyAction::new, "[MONEY]")),
    MESSAGE("[MESSAGE]", (plugin, string, key) -> createAction(plugin, string, key, MessageAction::new, "[MESSAGE]")),
    TITLE("[TITLE]", (plugin, string, key) -> createAction(plugin, string, key, TitleAction::new, "[TITLE]"));

    public final String prefix;
    private final ActionFactory actionFactory;

    Action(final String prefix, final ActionFactory actionFactory) {
        this.prefix = prefix;
        this.actionFactory = actionFactory;
    }


    public static Action fromString(final String string) {
        for (final Action actionType : values()) {
            if (string.startsWith(actionType.prefix)) {
                return actionType;
            }
        }

        return null;
    }

    @FunctionalInterface
    interface ActionFactory {
        ActionExecutor create(final SimpleCrates plugin, final String string, final String prefix);
    }

    private static ActionExecutor createAction(final SimpleCrates plugin, final String string, final String key, final ActionCreator actionCreator, final String prefix) {
        if (string.isEmpty()) {
            logMissingArguments(plugin, key, prefix);
            return null;
        }

        return actionCreator.create(plugin, string);
    }

    public ActionExecutor createAction(final SimpleCrates plugin, final String string) {
        return this.actionFactory.create(plugin, string.substring(this.prefix.length()).trim(), this.prefix);
    }

    private static void logMissingArguments(final SimpleCrates plugin, final String key, final String prefix) {
        plugin.getLogger().warning("Missing arguments for " + prefix + " action. Check your config file.");
        plugin.getLogger().warning("Path to: lucky-blocks." + key + ".actions");
    }

    @FunctionalInterface
    interface ActionCreator {
        ActionExecutor create(final SimpleCrates plugin, final String string);
    }


    public abstract static class ActionExecutor {

        protected final SimpleCrates plugin;
        protected final Chat chat;
        protected final Economy economy;
        protected final Colorizer colorizer;
        protected final String action;

        protected ActionExecutor(final SimpleCrates plugin, final String action) {
            this.plugin = plugin;
            this.chat = VaultUtils.getChatProvider(plugin.getServer().getServicesManager());
            this.economy = VaultUtils.getEconomyProvider(plugin.getServer().getServicesManager());
            this.colorizer = plugin.getConfigValues().getColorizer();
            this.action = this.colorizer.colorize(action);
        }

        public abstract void execute(@NotNull Player player);
    }
}
