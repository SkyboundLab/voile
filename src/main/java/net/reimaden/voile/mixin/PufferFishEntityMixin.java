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

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.reimaden.voile.entity.ai.goal.EmptyAttackGoal;
import net.reimaden.voile.power.ModifyBehaviorPower;
import net.reimaden.voile.util.BehaviorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PufferfishEntity.class)
public abstract class PufferFishEntityMixin extends FishEntity {

    public PufferFishEntityMixin(EntityType<? extends FishEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void voile$swimTowardsPlayer(CallbackInfo ci) {
        this.goalSelector.add(2, new EmptyAttackGoal(this, 1.0, true));
        BehaviorHelper.targetPlayer(this);
    }

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void voile$preventSting(PlayerEntity player, CallbackInfo ci) {
        BehaviorHelper behaviorHelper = new BehaviorHelper(player, this);

        if (behaviorHelper.checkEntity()) {
            if (behaviorHelper.behaviorMatches(ModifyBehaviorPower.EntityBehavior.PASSIVE)) {
                ci.cancel();
            }
        }
    }
}
