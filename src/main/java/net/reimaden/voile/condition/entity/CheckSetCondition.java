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

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.EntitySetPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.util.Pair;
import net.reimaden.voile.Voile;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class CheckSetCondition {

    // Code largely taken from Apoli's ActionOnSetAction

    public static boolean condition(SerializableData.Instance data, Entity entity) {
        PowerHolderComponent component = PowerHolderComponent.KEY.maybeGet(entity).orElse(null);
        PowerType<?> powerType = data.get("set");

        if (component == null || powerType == null || !(component.getPower(powerType) instanceof EntitySetPower entitySetPower)) {
            return false;
        }

        Predicate<Pair<Entity, Entity>> biEntityCondition = data.get("bientity_condition");

        List<UUID> uuids = new LinkedList<>(entitySetPower.getIterationSet());
        if (uuids.isEmpty()) return false;
        if (data.getBoolean("reverse")) {
            Collections.reverse(uuids);
        }

        int limit = data.get("limit");
        int processedUuids = 0;

        if (limit <= 0) {
            limit = uuids.size();
        }

        for (UUID uuid : uuids) {
            Entity entityFromSet = entitySetPower.getEntity(uuid);
            Pair<Entity, Entity> entityPair = new Pair<>(entity, entityFromSet);

            if (biEntityCondition.test(entityPair)) {
                processedUuids++;
            }
        }

        return processedUuids >= limit;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(
                Voile.id("check_set"),
                new SerializableData()
                        .add("set", ApoliDataTypes.POWER_TYPE)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION)
                        .add("limit", SerializableDataTypes.INT, 0)
                        .add("reverse", SerializableDataTypes.BOOLEAN, false),
                CheckSetCondition::condition
        );
    }
}
