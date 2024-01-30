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

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

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
}
