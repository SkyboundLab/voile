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
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import net.reimaden.voile.power.DisableShieldsPower;
import net.reimaden.voile.power.ModifyDeathSoundPower;
import net.reimaden.voile.power.ModifyHurtSoundPower;
import net.reimaden.voile.power.ReverseInstantEffectsPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@SuppressWarnings("unused")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @WrapWithCondition(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"))
    private boolean voile$modifyDeathSound(LivingEntity entity, SoundEvent soundEvent, float volume, float pitch) {
        List<ModifyDeathSoundPower> powers = PowerHolderComponent.getPowers(entity, ModifyDeathSoundPower.class);

        if (!powers.isEmpty()) {
            if (powers.stream().anyMatch(ModifyDeathSoundPower::isSilent)) return true;
            powers.forEach(power -> power.playSound(entity));
            return false;
        }

        return true;
    }

    @Inject(method = "playHurtSound", at = @At("HEAD"), cancellable = true)
    private void voile$modifyHurtSound(DamageSource source, CallbackInfo ci) {
        List<ModifyHurtSoundPower> powers = PowerHolderComponent.getPowers(this, ModifyHurtSoundPower.class);

        if (powers.isEmpty()) return;
        if (powers.stream().anyMatch(ModifyHurtSoundPower::isSilent)) ci.cancel();
        powers.forEach(power -> power.playSound(this));

        ci.cancel();
    }

    @Inject(method = "isUndead", at = @At("RETURN"), cancellable = true)
    private void voile$reverseInstantEffects(CallbackInfoReturnable<Boolean> cir) {
        if (PowerHolderComponent.hasPower(this, ReverseInstantEffectsPower.class)) cir.setReturnValue(!cir.getReturnValue());
    }

    @ModifyReturnValue(method = "disablesShield", at = @At("TAIL"))
    private boolean voile$hasDisableShieldsPower(boolean original) {
        return original || PowerHolderComponent.hasPower(this, DisableShieldsPower.class);
    }
}
