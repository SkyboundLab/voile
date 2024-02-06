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

package net.reimaden.voile.event;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.TypeFilter;
import net.reimaden.voile.power.ActionOnEntityDeathPower;

import java.util.stream.StreamSupport;

public class ListenToEntityDeathEvent implements ServerLivingEntityEvents.AfterDeath {

    @Override
    public void afterDeath(LivingEntity entity, DamageSource source) {
        MinecraftServer server = entity.getServer();

        if (server != null) {
            StreamSupport.stream(server.getWorlds().spliterator(), false)
                    .flatMap(world -> world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class),
                            livingEntity -> hasPower(livingEntity, entity, source)).stream())
                    .flatMap(livingEntity -> PowerHolderComponent.getPowers(livingEntity, ActionOnEntityDeathPower.class).stream())
                    .filter(power -> power.doesApply(entity, source, 0.0f))
                    .forEach(power -> power.onDeath(entity));
        }
    }

    private static boolean hasPower(LivingEntity actor, LivingEntity target, DamageSource source) {
        return PowerHolderComponent.getPowers(actor, ActionOnEntityDeathPower.class).stream()
                .anyMatch(power -> power.doesApply(target, source, 0.0f));
    }
}
