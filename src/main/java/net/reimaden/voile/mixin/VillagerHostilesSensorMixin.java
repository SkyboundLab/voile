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
import net.minecraft.entity.ai.brain.sensor.VillagerHostilesSensor;
import net.minecraft.entity.player.PlayerEntity;
import net.reimaden.voile.power.ModifyBehaviorPower;
import net.reimaden.voile.util.BehaviorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerHostilesSensor.class)
public class VillagerHostilesSensorMixin {

    // This mixin used to have injects for "isHostile" and "isCloseEnoughForDanger"
    // Since I can't easily get a villager instance in here, I had to inject to "matches" instead of "isHostile"
    // That also made "isCloseEnoughForDanger" redundant, so I removed it

    @Inject(method = "matches", at = @At("HEAD"), cancellable = true)
    private void voile$markPlayerAsHostile(LivingEntity villager, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (!(target instanceof PlayerEntity player) || player.isCreative()) return;

        BehaviorHelper behaviorHelper = new BehaviorHelper(player, villager);

        if (behaviorHelper.checkEntity()) {
            if (behaviorHelper.behaviorMatches(ModifyBehaviorPower.EntityBehavior.HOSTILE)) {
                final float distanceRequired = 8.0f;
                if (player.squaredDistanceTo(villager) <= (double) (distanceRequired * distanceRequired)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
