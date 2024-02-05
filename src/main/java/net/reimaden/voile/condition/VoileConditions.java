/*
 * This file is part of Voile, a library mod for Minecraft.
 * Copyright (C) 2023-2024  Maxmani
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
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
                    (!data.isPresent("block_condition") || (((ConditionFactory<CachedBlockPosition>.Instance) data.get("block_condition")).test(
                            new CachedBlockPosition(entity.getWorld(), BlockPos.ofFloored(entity.getX(), entity.getBoundingBox().minY - 0.0000001D, entity.getZ()), true))))));
    @SuppressWarnings("unchecked")
    public static final ConditionFactory<Entity> NEARBY_ENTITIES = registerEntityCondition(new ConditionFactory<>(Voile.id("nearby_entities"), new SerializableData()
            .add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null)
            .add("distance", SerializableDataTypes.FLOAT)
            .add("count", SerializableDataTypes.INT, 1),
            (data, entity) -> {
                if (data.getInt("count") <= 0) return false;

                if (!data.isPresent("entity_condition")) {
                    return entity.getWorld().getEntitiesByClass(Entity.class, entity.getBoundingBox().expand(data.getFloat("distance")), predicate -> entity != predicate)
                            .size() >= data.getInt("count");
                }
                int count = (int) entity.getWorld().getEntitiesByClass(Entity.class, entity.getBoundingBox().expand(data.getFloat("distance")), predicate -> entity != predicate)
                        .stream().filter(target -> ((ConditionFactory<Entity>.Instance) data.get("entity_condition")).test(target)).count();
                return count >= data.getInt("count");
            }));

    // Bi-Entity Conditions
    public static final ConditionFactory<Pair<Entity, Entity>> SCOREBOARD = registerBiEntityCondition(new ConditionFactory<>(Voile.id("scoreboard"), new SerializableData()
            .add("actor_objective", SerializableDataTypes.STRING)
            .add("target_objective", SerializableDataTypes.STRING)
            .add("comparison", ApoliDataTypes.COMPARISON),
            (data, pair) -> {
                Entity actor = pair.getLeft();
                Entity target = pair.getRight();

                if (actor == null || target == null) return false;

                String actorName = actor instanceof PlayerEntity playerEntity ? playerEntity.getName().getString() : actor.getUuidAsString();
                String targetName = target instanceof PlayerEntity playerEntity ? playerEntity.getName().getString() : target.getUuidAsString();

                Scoreboard scoreboard = actor.getWorld().getScoreboard();
                ScoreboardObjective actorObjective = scoreboard.getNullableObjective(data.getString("actor_objective"));
                ScoreboardObjective targetObjective = scoreboard.getNullableObjective(data.getString("target_objective"));

                if (scoreboard.playerHasObjective(actorName, actorObjective) && scoreboard.playerHasObjective(targetName, targetObjective)) {
                    Comparison comparison = data.get("comparison");
                    return comparison.compare(scoreboard.getPlayerScore(actorName, actorObjective).getScore(), scoreboard.getPlayerScore(targetName, targetObjective).getScore());
                }

                return false;
            }));

    // Item Conditions
    public static final ConditionFactory<Pair<World, ItemStack>> ENCHANTABILITY = registerItemCondition(new ConditionFactory<>(Voile.id("enchantability"), new SerializableData()
            .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
            .add("compare_to", SerializableDataTypes.INT),
            (data, pair) -> {
                ItemStack stack = pair.getRight();
                int enchantability = stack.getItem().getEnchantability();

                return ((Comparison) data.get("comparison")).compare(enchantability, data.getInt("compare_to"));
            }));
    public static final ConditionFactory<Pair<World, ItemStack>> CRAFTABLE = registerItemCondition(new ConditionFactory<>(Voile.id("craftable"), new SerializableData(),
            (data, pair) -> {
                World world = pair.getLeft();
                ItemStack stack = pair.getRight();

                return world.getRecipeManager().listAllOfType(RecipeType.CRAFTING).stream()
                        .anyMatch(recipe -> recipe.value().getResult(world.getRegistryManager()).isOf(stack.getItem()));
            }));

    private static ConditionFactory<Entity> registerEntityCondition(ConditionFactory<Entity> factory) {
        return Registry.register(ApoliRegistries.ENTITY_CONDITION, factory.getSerializerId(), factory);
    }

    private static ConditionFactory<Pair<Entity, Entity>> registerBiEntityCondition(ConditionFactory<Pair<Entity, Entity>> factory) {
        return Registry.register(ApoliRegistries.BIENTITY_CONDITION, factory.getSerializerId(), factory);
    }

    private static ConditionFactory<Pair<World, ItemStack>> registerItemCondition(ConditionFactory<Pair<World, ItemStack>> factory) {
        return Registry.register(ApoliRegistries.ITEM_CONDITION, factory.getSerializerId(), factory);
    }

    public static void register() {
        Voile.LOGGER.debug("Registering conditions for " + Voile.MOD_ID);
    }
}
