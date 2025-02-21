package com.github.groundbreakingmc.simplecrates;

import com.github.groundbreakingmc.mylib.actions.Action;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class Crate {

    public static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @Getter
    private final String name;
    private final String displayName;
    private final Set<Prize> prizes;
    private final int totalChanges;

    public Crate(final String name, String displayName, Set<Prize> prizes) {
        this.name = name;
        this.displayName = displayName;
        this.prizes = prizes;
        int totalChanges = 0;
        for (final Prize prize : prizes) {
            totalChanges += prize.chance();
        }
        this.totalChanges = totalChanges;
    }

    public Prize getRandomPrize() {
        final int randomChance = RANDOM.nextInt(this.totalChanges) + 1;

        int currentWeight = 0;
        for (final Prize prize : this.prizes) {
            currentWeight += prize.chance();
            if (randomChance <= currentWeight) {
                return prize;
            }
        }

        throw new IllegalStateException("Chances for crate \"" + this.displayName + "\" configured incorrect!.");
    }

    public record Prize(
            int chance,
            ItemStack display,
            List<Action.ActionExecutor> actions
    ) {
    }
}
