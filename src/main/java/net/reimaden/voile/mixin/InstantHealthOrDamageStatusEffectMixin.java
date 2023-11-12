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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.InstantHealthOrDamageStatusEffect;
import net.minecraft.entity.effect.InstantStatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.reimaden.voile.power.InstantEffectImmunityPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InstantHealthOrDamageStatusEffect.class)
public abstract class InstantHealthOrDamageStatusEffectMixin extends InstantStatusEffect {

    public InstantHealthOrDamageStatusEffectMixin(StatusEffectCategory statusEffectCategory, int i) {
        super(statusEffectCategory, i);
    }

    @Inject(method = "applyInstantEffect", at = @At("HEAD"), cancellable = true)
    private void voile$cancelInstantEffects(Entity source, Entity attacker, LivingEntity target, int amplifier, double proximity, CallbackInfo ci) {
        if (PowerHolderComponent.getPowers(target, InstantEffectImmunityPower.class).stream().anyMatch(power -> power.containsEffect(this))) {
            ci.cancel();
        }
    }
}
