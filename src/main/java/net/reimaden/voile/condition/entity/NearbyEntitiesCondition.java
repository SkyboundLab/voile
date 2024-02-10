/*
 * This file is part of Voile, a library mod for Minecraft.
 * Copyright (C) 2024  Maxmani
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

package net.reimaden.voile.condition.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.util.Pair;
import net.reimaden.voile.Voile;

import java.util.function.Predicate;

public class NearbyEntitiesCondition {

    public static boolean condition(SerializableData.Instance data, Entity entity) {
        int count = data.getInt("count");
        if (count < 0) return false;

        Predicate<Pair<Entity, Entity>> biEntityCondition = data.get("bientity_condition");
        float distance = data.getFloat("distance");

        if (biEntityCondition == null) {
            return entity.getWorld().getEntitiesByClass(Entity.class, entity.getBoundingBox().expand(distance), predicate -> entity != predicate)
                    .size() >= count;
        }
        int entities = (int) entity.getWorld().getEntitiesByClass(Entity.class, entity.getBoundingBox().expand(distance), predicate -> entity != predicate)
                .stream().filter(target -> biEntityCondition.test(new Pair<>(entity, target))).count();
        return entities >= count;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(
                Voile.id("nearby_entities"),
                new SerializableData()
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("distance", SerializableDataTypes.FLOAT)
                        .add("count", SerializableDataTypes.INT, 1),
                NearbyEntitiesCondition::condition
        );
    }
}
