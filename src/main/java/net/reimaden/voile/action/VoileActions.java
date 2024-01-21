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

package net.reimaden.voile.action;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registry;
import net.reimaden.voile.Voile;

@SuppressWarnings("unused")
public class VoileActions {

    public static final ActionFactory<Entity> CHANGE_RESOURCE_WITH_STATUS_EFFECTS = registerEntityAction(new ActionFactory<>(Voile.id("change_resource_with_status_effects"), new SerializableData()
            .add("resource", ApoliDataTypes.POWER_TYPE)
            .add("category", SerializableDataType.enumValue(StatusEffectCategory.class))
            .add("change", SerializableDataTypes.INT, 1)
            .add("operation", ApoliDataTypes.RESOURCE_OPERATION, ResourceOperation.ADD),
            ChangeResourceWithStatusEffectsAction::action)
    );
    public static final ActionFactory<Entity> DISABLE_SHIELD = registerEntityAction(new ActionFactory<>(Voile.id("disable_shield"), new SerializableData(),
            DisableShieldAction::action)
    );

    private static ActionFactory<Entity> registerEntityAction(ActionFactory<Entity> factory) {
        return Registry.register(ApoliRegistries.ENTITY_ACTION, factory.getSerializerId(), factory);
    }

    /* noinspection
    private static ActionFactory<Pair<Entity, Entity>> registerBiEntityAction(ActionFactory<Pair<Entity, Entity>> factory) {
        return Registry.register(ApoliRegistries.BIENTITY_ACTION, factory.getSerializerId(), factory);
    }
     */

    public static void register() {
        Voile.LOGGER.debug("Registering actions for " + Voile.MOD_ID);
    }
}
