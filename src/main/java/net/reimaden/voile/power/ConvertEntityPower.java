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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Pair;

import java.util.function.Predicate;

public class ConvertEntityPower extends Power {

    private final Predicate<MobEntity> entityCondition;
    private final Predicate<Pair<Entity, MobEntity>> biEntityCondition;
    private final EntityType<MobEntity> newEntity;
    private final boolean ignoreDifficulty;

    public ConvertEntityPower(PowerType<?> type, LivingEntity entity, Predicate<MobEntity> entityCondition,
                              Predicate<Pair<Entity, MobEntity>> biEntityCondition, EntityType<MobEntity> newEntity, boolean ignoreDifficulty) {
        super(type, entity);
        this.entityCondition = entityCondition;
        this.biEntityCondition = biEntityCondition;
        this.newEntity = newEntity;
        this.ignoreDifficulty = ignoreDifficulty;
    }

    public boolean checkEntity(MobEntity mobEntity) {
        return (this.entityCondition == null || this.entityCondition.test(mobEntity)) && (this.biEntityCondition == null || this.biEntityCondition.test(new Pair<>(entity, mobEntity)));
    }

    public EntityType<? extends MobEntity> getNewEntity() {
        return this.newEntity;
    }

    public boolean ignoreDifficulty() {
        return this.ignoreDifficulty;
    }
}
