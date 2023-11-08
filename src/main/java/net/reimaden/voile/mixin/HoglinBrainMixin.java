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

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HoglinBrain;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.reimaden.voile.power.ModifyBehaviorPower;
import net.reimaden.voile.util.BehaviorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(HoglinBrain.class)
public class HoglinBrainMixin {

    @Inject(method = "getNearestVisibleTargetablePlayer", at = @At("RETURN"), cancellable = true)
    private static void voile$preventTargeting(HoglinEntity hoglin, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
        Optional<? extends LivingEntity> entity = cir.getReturnValue();

        if (entity.isPresent()) {
            LivingEntity target = entity.get();

            if (target instanceof PlayerEntity player) {
                BehaviorHelper behaviorHelper = new BehaviorHelper(player, hoglin);

                if (behaviorHelper.checkEntity()) {
                    if (behaviorHelper.neutralOrPassive()) {
                        cir.setReturnValue(Optional.empty());
                    }
                }
            }
        }
    }

    @Inject(method = "onAttacked", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/HoglinBrain;targetEnemy(Lnet/minecraft/entity/mob/HoglinEntity;Lnet/minecraft/entity/LivingEntity;)V"), cancellable = true)
    private static void voile$lobotomizeHoglin(HoglinEntity hoglin, LivingEntity attacker, CallbackInfo ci) {
        BehaviorHelper behaviorHelper = new BehaviorHelper(attacker, hoglin);

        if (behaviorHelper.checkEntity()) {
            if (behaviorHelper.behaviorMatches(ModifyBehaviorPower.EntityBehavior.PASSIVE)) {
                ci.cancel();
            }
        }
    }
}
