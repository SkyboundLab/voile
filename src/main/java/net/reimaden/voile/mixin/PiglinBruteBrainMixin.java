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
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PiglinBruteBrain;
import net.minecraft.entity.mob.PiglinBruteEntity;
import net.reimaden.voile.power.ModifyBehaviorPower;
import net.reimaden.voile.util.BehaviorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PiglinBruteBrain.class)
public class PiglinBruteBrainMixin {

    @Inject(method = "getTargetIfInRange", at = @At("RETURN"), cancellable = true)
    private static void voile$preventTargeting(AbstractPiglinEntity piglin, MemoryModuleType<? extends LivingEntity> targetMemoryModule, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
        if (cir.getReturnValue().isPresent()) {
            //noinspection DataFlowIssue
            piglin.getBrain().getOptionalMemory(targetMemoryModule).filter(livingEntity -> {
                BehaviorHelper behaviorHelper = new BehaviorHelper(livingEntity, piglin);

                if (behaviorHelper.checkEntity()) {
                    if (behaviorHelper.neutralOrPassive()) {
                        cir.setReturnValue(Optional.empty());
                    }
                }

                return false;
            });
        }
    }

    @Inject(method = "tryRevenge", at = @At("HEAD"), cancellable = true)
    private static void voile$lobotomizePiglinBrute(PiglinBruteEntity piglinBrute, LivingEntity target, CallbackInfo ci) {
        BehaviorHelper behaviorHelper = new BehaviorHelper(target, piglinBrute);

        if (behaviorHelper.checkEntity()) {
            if (behaviorHelper.behaviorMatches(ModifyBehaviorPower.EntityBehavior.PASSIVE)) {
                ci.cancel();
            }
        }
    }
}
