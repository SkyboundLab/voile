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

package net.reimaden.voile.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.util.ResourceOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import net.reimaden.voile.power.ModifyDivergencePower;
import net.reimaden.voile.power.ModifyProjectileSpeedPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin extends Entity implements Ownable {

    public ProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyVariable(method = "setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V", at = @At("HEAD"), ordinal = 4, argsOnly = true)
    private float voile$modifyDivergence(float original, Entity shooter) {
        List<ModifyDivergencePower> powers = PowerHolderComponent.getPowers(shooter, ModifyDivergencePower.class);

        for (ModifyDivergencePower power : powers) {
            if (power.getOperation() == ResourceOperation.SET) {
                return power.getDivergence();
            }
        }

        float totalDivergence = 0f;
        for (ModifyDivergencePower power : powers) {
            totalDivergence += power.getDivergence();
        }
        original += totalDivergence;

        return original;
    }

    @ModifyVariable(method = "setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V", at = @At("HEAD"), ordinal = 3, argsOnly = true)
    private float voile$modifySpeed(float original, Entity shooter) {
        return PowerHolderComponent.modify(shooter, ModifyProjectileSpeedPower.class, original,
                power -> power.doesApply(this),
                power -> power.executeActions(this, shooter));
    }
}
