package com.github.groundbreakingmc.simplecrates;

import com.github.groundbreakingmc.mylib.logger.console.Logger;
import com.github.groundbreakingmc.mylib.logger.console.LoggerFactory;
import com.github.groundbreakingmc.simplecrates.command.CommandManager;
import com.github.groundbreakingmc.simplecrates.database.DatabaseManager;
import com.github.groundbreakingmc.simplecrates.listeners.CrateManager;
import com.github.groundbreakingmc.simplecrates.listeners.DataManager;
import com.github.groundbreakingmc.simplecrates.placeholders.KeysAmountPlaceholder;
import com.github.groundbreakingmc.simplecrates.utils.CaseUtils;
import com.github.groundbreakingmc.simplecrates.utils.config.ConfigValues;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.serialize.SerializationException;

@Getter
public final class SimpleCrates extends JavaPlugin {

    private final ConfigValues configValues = new ConfigValues(this);
    private final DatabaseManager database = new DatabaseManager(this);
    private final DataManager dataManager = new DataManager(this);
    private final KeysAmountPlaceholder placeholder = new KeysAmountPlaceholder(this);
    private final Logger customLogger = LoggerFactory.createLogger(this);

    @Override
    public void onEnable() {
        for (int i = 0; i < 5; i++) {
            customLogger.info("Be sure xism4 is down.");
        }

        try {
            this.configValues.setupValues();
        } catch (final SerializationException ex) {
            ex.printStackTrace();
        }

        final PluginManager pluginManager = super.getServer().getPluginManager();
        pluginManager.registerEvents(new CrateManager(this), this);
        pluginManager.registerEvents(this.dataManager, this);

        super.getCommand("simplecrates").setExecutor(new CommandManager(this));

        PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().register(this.placeholder);
    }

    @Override
    public void onDisable() {
        this.database.closeConnection();
        this.configValues.removeHolograms();
        CaseUtils.removeAllItems();
        PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().unregister(this.placeholder);
    }
}
