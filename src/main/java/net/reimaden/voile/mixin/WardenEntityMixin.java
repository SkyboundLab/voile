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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.world.World;
import net.minecraft.world.event.Vibrations;
import net.reimaden.voile.power.ModifyBehaviorPower;
import net.reimaden.voile.util.BehaviorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WardenEntity.class)
public abstract class WardenEntityMixin extends HostileEntity implements Vibrations {

    protected WardenEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "isValidTarget", at = @At("HEAD"), cancellable = true)
    private void voile$preventTargeting(Entity target, CallbackInfoReturnable<Boolean> cir) {
        BehaviorHelper behaviorHelper = new BehaviorHelper(target, this);

        if (behaviorHelper.checkEntity()) {
            if (behaviorHelper.behaviorMatches(ModifyBehaviorPower.EntityBehavior.PASSIVE)) {
                cir.setReturnValue(false);
            } else if (behaviorHelper.behaviorMatches(ModifyBehaviorPower.EntityBehavior.NEUTRAL)) {
                if (this.getAttacker() == target || this.getTarget() == target) {
                    cir.setReturnValue(true);
                } else {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
