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

package net.reimaden.voile.action;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class ChangeResourceWithStatusEffectsAction {

    public static void action(SerializableData.Instance data, Entity entity) {
        if (!(entity instanceof LivingEntity living)) return;

        PowerHolderComponent component = PowerHolderComponent.KEY.get(living);
        PowerType<?> powerType = data.get("resource");
        Power power = component.getPower(powerType);
        StatusEffectCategory category = data.get("category");
        ResourceOperation operation = data.get("operation");

        // Get the number of status effects of the given category
        int effects = living.getStatusEffects().stream()
                .filter(effect -> effect.getEffectType().getCategory().equals(category))
                .mapToInt(effect -> 1)
                .sum();

        if (power instanceof VariableIntPower variableIntPower) {
            if (operation == ResourceOperation.ADD) {
                int newValue = variableIntPower.getValue() + effects;
                variableIntPower.setValue(newValue);
            } else if (operation == ResourceOperation.SET) {
                variableIntPower.setValue(effects);
            }
            PowerHolderComponent.syncPower(entity, powerType);
        } else if (power instanceof CooldownPower cooldownPower) {
            if (operation == ResourceOperation.ADD) {
                cooldownPower.modify(effects);
            } else if (operation == ResourceOperation.SET) {
                cooldownPower.setCooldown(effects);
            }
            PowerHolderComponent.syncPower(entity, powerType);
        }
    }
}
