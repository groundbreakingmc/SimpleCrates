package com.github.groundbreakingmc.simplecrates.utils;

import com.github.groundbreakingmc.mylib.collections.cases.Triplet;
import com.github.groundbreakingmc.simplecrates.Crate;
import com.github.groundbreakingmc.simplecrates.SimpleCrates;
import com.github.groundbreakingmc.simplecrates.actions.Action;
import com.google.common.collect.ImmutableList;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public final class CaseUtils {

    private static final List<FireworkEffect> EFFECTS;
    private static final Vector VELOCITY;
    private static final Set<Item> ITEMS = new HashSet<>();

    public static void open(final SimpleCrates plugin,
                            final Player player,
                            final Triplet<Boolean, Crate, Hologram> crateParams,
                            final Location location,
                            final boolean fireworksAnimation) {
        crateParams.setLeft(true);
        crateParams.getRight().disable();

        if (fireworksAnimation) {
            open(plugin, player, crateParams, location);
            return;
        }

        plugin.getDataManager().removePlayerKeys(player, crateParams.getMiddle().getName(), 1);

        final Crate.Prize prize = crateParams.getMiddle().getRandomPrize();
        final Item item = location.getWorld().dropItem(location, prize.display());
        ITEMS.add(item);
        item.customName(prize.display().displayName());
        item.setVelocity(VELOCITY);
        item.setCustomNameVisible(true);
        item.setCanMobPickup(false);
        item.setCanPlayerPickup(false);

        for (final Action.ActionExecutor action : prize.actions()) {
            action.execute(player);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            item.remove();
            crateParams.getRight().enable();
            ITEMS.remove(item);
            crateParams.setLeft(false);
        }, 50L);
    }

    private static void open(final SimpleCrates plugin,
                             final Player player,
                             final Triplet<Boolean, Crate, Hologram> crateParams,
                             final Location location) {
        new BukkitRunnable() {
            private final Location clonedLocation = location.clone().add(0.5, 14, 0.5);
            private int i = 0;

            @Override
            public void run() {
                this.clonedLocation.subtract(0, 1, 0);
                if (++this.i == EFFECTS.size()) {
                    cancel();
                    open(plugin, player, crateParams, this.clonedLocation, false);
                } else {
                    spawnFirework(this.clonedLocation, EFFECTS.get(i));
                }
            }
        }.runTaskTimer(plugin, 0L, 3L);
    }

    private static void spawnFirework(final Location location, final FireworkEffect effect) {
        final Firework firework = location.getWorld().spawn(location, Firework.class);
        final FireworkMeta meta = firework.getFireworkMeta();

        meta.addEffect(effect);
        firework.setFireworkMeta(meta);

        firework.detonate();
    }

    public static void removeAllItems() {
        for (final Item item : ITEMS) {
            item.remove();
        }
    }

    static {
        final List<FireworkEffect> effects = new ArrayList<>();
        for (final Color color : getColors()) {
            effects.add(
                    FireworkEffect.builder()
                            .withColor(color)
                            .with(FireworkEffect.Type.BALL)
                            .build()
            );
        }

        EFFECTS = ImmutableList.copyOf(effects);
        VELOCITY = new Vector(0, 0.2, 0);
    }

    private static List<Color> getColors() {
        return ImmutableList.of(
                Color.fromRGB(255, 0, 0),
                Color.fromRGB(255, 50, 0),
                Color.fromRGB(255, 100, 0),
                Color.fromRGB(255, 150, 0),
                Color.fromRGB(255, 200, 0),
                Color.fromRGB(255, 255, 0),
                Color.fromRGB(200, 255, 0),
                Color.fromRGB(0, 255, 0),
                Color.fromRGB(0, 255, 255),
                Color.fromRGB(0, 0, 255),
                Color.fromRGB(75, 0, 130),
                Color.fromRGB(148, 0, 211),
                Color.fromRGB(238, 130, 238)
        );
    }
}
