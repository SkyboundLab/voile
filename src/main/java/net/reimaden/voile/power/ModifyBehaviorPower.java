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

package net.reimaden.voile.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;

import java.util.function.Predicate;

public class ModifyBehaviorPower extends Power {

    private final Predicate<Entity> entityCondition;
    private final Predicate<Pair<Entity, Entity>> biEntityCondition;
    private final EntityBehavior desiredBehavior;

    public ModifyBehaviorPower(PowerType<?> type, LivingEntity player, EntityBehavior desiredBehavior,
                               Predicate<Entity> entityCondition, Predicate<Pair<Entity, Entity>> biEntityCondition) {
        super(type, player);
        this.entityCondition = entityCondition;
        this.biEntityCondition = biEntityCondition;
        this.desiredBehavior = desiredBehavior;
    }

    public boolean checkEntity(Entity mob) {
        return (this.entityCondition == null || this.entityCondition.test(mob)) && (this.biEntityCondition == null || this.biEntityCondition.test(new Pair<>(entity, mob)));
    }

    public EntityBehavior getDesiredBehavior() {
        return this.desiredBehavior;
    }

    public enum EntityBehavior {
        HOSTILE,
        NEUTRAL,
        PASSIVE
    }
}
