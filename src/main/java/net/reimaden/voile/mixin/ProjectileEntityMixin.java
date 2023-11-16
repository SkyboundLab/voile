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

package net.reimaden.voile.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.reimaden.voile.power.ModifyDivergencePower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin {

    @ModifyVariable(method = "setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V", at = @At("HEAD"), ordinal = 4, argsOnly = true)
    private float voile$modifyDivergence(float original, Entity shooter) {
        Optional<ModifyDivergencePower> power = PowerHolderComponent.getPowers(shooter, ModifyDivergencePower.class).stream().findFirst();
        return power.map(ModifyDivergencePower::getDivergence).orElse(original);
    }
}
