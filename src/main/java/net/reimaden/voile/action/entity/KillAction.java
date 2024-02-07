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

package net.reimaden.voile.action.entity;

import com.google.gson.JsonSyntaxException;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.reimaden.voile.Voile;

public class KillAction {

    public static void action(SerializableData.Instance data, Entity entity) {
        try {
            DamageSource damageSource = entity.getDamageSources().create(data.get("damage_type"));
            entity.damage(damageSource, Float.MAX_VALUE);
        } catch (JsonSyntaxException e) {
            Voile.LOGGER.error("Error trying to create damage source in a `kill` entity action: " + e.getMessage());
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                Voile.id("kill"),
                new SerializableData()
                        .add("damage_type", SerializableDataTypes.DAMAGE_TYPE, DamageTypes.GENERIC_KILL),
                KillAction::action
        );
    }
}
