/*
 * This file is part of Voile, a library mod for Minecraft.
 * Copyright (C) 2023  Maxmani
 *
 * Voile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Voile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Voile.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.reimaden.voile.condition;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.reimaden.voile.Voile;

@SuppressWarnings("unused")
public class VoileConditions {

    // Entity Conditions
    public static final ConditionFactory<Entity> MOON_PHASE = registerEntityCondition(new ConditionFactory<>(Voile.id("moon_phase"), new SerializableData()
            .add("comparison", ApoliDataTypes.COMPARISON)
            .add("compare_to", SerializableDataTypes.INT),
            (data, entity) -> ((Comparison) data.get("comparison")).compare(entity.getWorld().getMoonPhase(), data.getInt("compare_to"))));
    @SuppressWarnings("unchecked") // Copy of Apoli's On Block condition with more precision
    public static final ConditionFactory<Entity> PRECISE_ON_BLOCK = registerEntityCondition(new ConditionFactory<>(Voile.id("precise_on_block"), new SerializableData()
            .add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null),
            (data, entity) -> entity.isOnGround() &&
                    (!data.isPresent("block_condition") || (((ConditionFactory<CachedBlockPosition>.Instance)data.get("block_condition")).test(
                            new CachedBlockPosition(entity.getWorld(), BlockPos.ofFloored(entity.getX(), entity.getBoundingBox().minY - 0.0000001D, entity.getZ()), true))))));

    // Item Conditions
    public static final ConditionFactory<Pair<World, ItemStack>> ENCHANTABILITY = registerItemCondition(new ConditionFactory<>(Voile.id("enchantability"), new SerializableData()
            .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
            .add("compare_to", SerializableDataTypes.INT),
            (data, pair) -> {
                ItemStack stack = pair.getRight();
                int enchantability = stack.getItem().getEnchantability();

                return ((Comparison) data.get("comparison")).compare(enchantability, data.getInt("compare_to"));
            }));

    private static ConditionFactory<Entity> registerEntityCondition(ConditionFactory<Entity> factory) {
        return Registry.register(ApoliRegistries.ENTITY_CONDITION, factory.getSerializerId(), factory);
    }

    private static ConditionFactory<Pair<World, ItemStack>> registerItemCondition(ConditionFactory<Pair<World, ItemStack>> factory) {
        return Registry.register(ApoliRegistries.ITEM_CONDITION, factory.getSerializerId(), factory);
    }

    public static void register() {
        Voile.LOGGER.debug("Registering conditions for " + Voile.MOD_ID);
    }
}
