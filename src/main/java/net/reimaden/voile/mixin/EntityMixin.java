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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.reimaden.voile.power.ModifyFootstepSoundPower;
import net.reimaden.voile.power.PreventSprintingParticlesPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void voile$modifyFootstepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (!((Entity) (Object) this instanceof LivingEntity entity)) return;

        List<ModifyFootstepSoundPower> powers = PowerHolderComponent.getPowers(entity, ModifyFootstepSoundPower.class);

        if (powers.isEmpty()) return;
        if (powers.stream().anyMatch(ModifyFootstepSoundPower::isSilent)) ci.cancel();
        powers.forEach(power -> power.playSound(entity));

        ci.cancel();
    }

    @ModifyReturnValue(method = "shouldSpawnSprintingParticles", at = @At("TAIL"))
    private boolean voile$preventSprintingParticles(boolean original) {
        if (PowerHolderComponent.hasPower((Entity) (Object) this, PreventSprintingParticlesPower.class)) {
            return false;
        }

        return original;
    }
}
