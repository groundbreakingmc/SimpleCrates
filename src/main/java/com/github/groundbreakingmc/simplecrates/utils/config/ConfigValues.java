package com.github.groundbreakingmc.simplecrates.utils.config;

import com.github.groundbreakingmc.mylib.actions.Action;
import com.github.groundbreakingmc.mylib.collections.cases.Triplet;
import com.github.groundbreakingmc.mylib.colorizer.Colorizer;
import com.github.groundbreakingmc.mylib.colorizer.ColorizerFactory;
import com.github.groundbreakingmc.mylib.config.ConfigurateLoader;
import com.github.groundbreakingmc.mylib.utils.bukkit.BukkitSerializeUtils;
import com.github.groundbreakingmc.simplecrates.Crate;
import com.github.groundbreakingmc.simplecrates.SimpleCrates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;

@Getter
public final class ConfigValues {

    @Getter(AccessLevel.NONE)
    private final SimpleCrates plugin;

    private Colorizer colorizer;
    private World world;

    @Getter(AccessLevel.NONE)
    private Map<Location, Triplet<Boolean, Crate, Hologram>> crates;
    private Set<String> crateNames;

    private String reloadMessage;
    private String noCasesMessage;

    public ConfigValues(final SimpleCrates plugin) {
        this.plugin = plugin;
    }

    public void setupValues() throws SerializationException {
        final ConfigurationNode config = this.getConfig("config.yml", 1.0, "settings.config-version");

        this.setupSettings(config);
        this.setupCases(config);
        this.setupMessages(config);
    }

    public void setupSettings(final ConfigurationNode config) {
        final ConfigurationNode settings = config.node("settings");
        this.colorizer = ColorizerFactory.createColorizer(settings.node("colorizer-serializer").getString());
        this.world = Bukkit.getWorld(settings.node("world").getString());
    }

    public void setupCases(final ConfigurationNode config) throws SerializationException {
        final Map<Location, Triplet<Boolean, Crate, Hologram>> cratesTemp = new HashMap<>();
        final Set<String> crateNamesTemp = new HashSet<>();

        // Тут у нас енрисет с ключём Object (ключ из конфига)
        // и згачением ? extends ConfigurationNode (значение ключа из конфига)
        for (final var crateNode : config.node("crates").childrenMap().entrySet()) {
            final ConfigurationNode caseConfig = this.getConfig(crateNode.getValue().getString());
            final Crate crate = this.createCrate(crateNode.getKey().toString(), caseConfig);
            for (final String locationString : caseConfig.node("locations").getList(String.class)) {
                final Location location = BukkitSerializeUtils.locationFromString(locationString);
                final Hologram hologram = this.createHologram(caseConfig, location);
                cratesTemp.put(location, new Triplet<>(false, crate, hologram));
            }
            crateNamesTemp.add(crateNode.getKey().toString());
        }

        this.crates = ImmutableMap.copyOf(cratesTemp);
        this.crateNames = ImmutableSet.copyOf(crateNamesTemp);
    }

    private Crate createCrate(final String name, final ConfigurationNode node) throws SerializationException {
        return new Crate(
                name,
                this.colorizer.colorize(node.node("name").getString()),
                this.getPrizes(node)
        );
    }

    private Hologram createHologram(final ConfigurationNode node, final Location location) throws SerializationException {
        final ConfigurationNode hologramNode = node.node("hologram");
        final double offset = hologramNode.node("offset").getDouble();
        final List<String> lines = hologramNode.node("lines").getList(String.class);
        final Hologram hologram = DHAPI.createHologram(
                UUID.randomUUID().toString(),
                location.clone().add(0.5, offset, 0.5),
                false,
                lines
        );
        hologram.setDisplayRange(20);
        hologram.setUpdateInterval(60);
        return hologram;
    }

    private Set<Crate.Prize> getPrizes(final ConfigurationNode node) throws SerializationException {
        final Set<Crate.Prize> prizes = new HashSet<>();
        for (final var prizesNode : node.node("prizes").childrenMap().entrySet()) {
            prizes.add(new Crate.Prize(
                    prizesNode.getValue().node("chance").getInt(),
                    this.getDisplayItem(prizesNode.getValue()),
                    this.getActions(prizesNode.getValue())
            ));
        }

        return ImmutableSet.copyOf(prizes);
    }

    private ItemStack getDisplayItem(final ConfigurationNode node) {
        final ItemStack display = new ItemStack(
                Material.valueOf(node.node("material").getString().toUpperCase()),
                1
        );

        final ItemMeta meta = display.getItemMeta();
        meta.setDisplayName(this.colorizer.colorize(node.node("name").getString()));
        display.setItemMeta(meta);

        return display;
    }

    private List<Action.ActionExecutor> getActions(final ConfigurationNode node) throws SerializationException {
        return node.node("actions").getList(String.class).stream()
                .map(string -> Action.fromString(string).createAction(
                        this.plugin,
                        this.plugin.getCustomLogger(),
                        this.colorizer,
                        string
                ))
                .collect(ImmutableList.toImmutableList());
    }

    public void setupMessages(final ConfigurationNode config) {
        final ConfigurationNode messages = config.node("messages");
        this.reloadMessage = this.colorizer.colorize(messages.node("reload").getString());
        this.noCasesMessage = this.colorizer.colorize(messages.node("no-cases").getString());
    }

    public Triplet<Boolean, Crate, Hologram> getCrate(final Location location) {
        return this.crates.get(location);
    }

    public void removeHolograms() {
        if (!this.crates.isEmpty()) {
            for (final Map.Entry<Location, Triplet<Boolean, Crate, Hologram>> entry : this.crates.entrySet()) {
                entry.getValue().getRight().delete();
            }
        }
    }

    private ConfigurationNode getConfig(final String fileName) {
        return this.getConfig(fileName, 0, null);
    }

    private ConfigurationNode getConfig(final String fileName, final double fileVersion, final String versionPath) {
        return ConfigurateLoader.loader(this.plugin, this.plugin.getCustomLogger())
                .fileName(fileName)
                .fileVersion(fileVersion)
                .fileVersionPath(versionPath)
                .load();
    }
}
