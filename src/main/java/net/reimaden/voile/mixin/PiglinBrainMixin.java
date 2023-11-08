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
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.reimaden.voile.power.ModifyBehaviorPower;
import net.reimaden.voile.util.BehaviorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {

    @Inject(method = "wearsGoldArmor", at = @At("HEAD"), cancellable = true)
    private static void voile$markPlayerAsWearingGold(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (!(target instanceof PlayerEntity player)) return;

        List<PiglinEntity> list = player.getWorld().getNonSpectatingEntities(PiglinEntity.class, player.getBoundingBox().expand(16.0));
        list.forEach(piglin -> {
            BehaviorHelper behaviorHelper = new BehaviorHelper(player, piglin);

            if (behaviorHelper.checkEntity()) {
                if (behaviorHelper.neutralOrPassive()) {
                    cir.setReturnValue(true);
                    // If piglins are explicitly hostile, don't mark the player as wearing gold
                } else if (behaviorHelper.behaviorMatches(ModifyBehaviorPower.EntityBehavior.HOSTILE)) {
                    cir.setReturnValue(false);
                }
            }
        });
    }

    @Inject(method = "onGuardedBlockInteracted", at = @At("HEAD"), cancellable = true)
    private static void voile$allowGuardedBlock(PlayerEntity player, boolean blockOpen, CallbackInfo ci) {
        List<PiglinEntity> list = player.getWorld().getNonSpectatingEntities(PiglinEntity.class, player.getBoundingBox().expand(16.0));
        list.forEach(piglin -> {
            BehaviorHelper behaviorHelper = new BehaviorHelper(player, piglin);

            if (behaviorHelper.checkEntity()) {
                if (behaviorHelper.neutralOrPassive()) {
                    ci.cancel();
                }
            }
        });
    }

    @Inject(method = "tryRevenge", at = @At("HEAD"), cancellable = true)
    private static void voile$lobotomizePiglin(AbstractPiglinEntity piglin, LivingEntity target, CallbackInfo ci) {
        BehaviorHelper behaviorHelper = new BehaviorHelper(target, piglin);

        if (behaviorHelper.checkEntity()) {
            if (behaviorHelper.behaviorMatches(ModifyBehaviorPower.EntityBehavior.PASSIVE)) {
                ci.cancel();
            }
        }
    }
}
