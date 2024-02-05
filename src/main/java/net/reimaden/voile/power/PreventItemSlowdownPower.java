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

package net.reimaden.voile.power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import net.reimaden.voile.Voile;

import java.util.function.Predicate;

public class PreventItemSlowdownPower extends Power {

    private final Predicate<Pair<World, ItemStack>> itemCondition;
    private final boolean canStartSprinting;

    public PreventItemSlowdownPower(PowerType<?> type, LivingEntity entity, Predicate<Pair<World, ItemStack>> itemCondition, boolean canStartSprinting) {
        super(type, entity);
        this.itemCondition = itemCondition;
        this.canStartSprinting = canStartSprinting;
    }

    public boolean doesPrevent(ItemStack stack) {
        return itemCondition == null || itemCondition.test(new Pair<>(entity.getWorld(), stack));
    }

    public boolean getCanStartSprinting() {
        return this.canStartSprinting;
    }

    public static PowerFactory<Power> createFactory() {
        return new PowerFactory<>(
                Voile.id("prevent_item_slowdown"),
                new SerializableData()
                        .add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
                        .add("can_start_sprinting", SerializableDataTypes.BOOLEAN, true),
                data -> (type, entity) -> new PreventItemSlowdownPower(type, entity, data.get("item_condition"), data.getBoolean("can_start_sprinting"))
        ).allowCondition();
    }
}
