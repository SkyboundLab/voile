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

package net.reimaden.voile.mixin.client;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Formatting;
import net.reimaden.voile.power.FlipModelPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @Inject(method = "shouldFlipUpsideDown", at = @At("HEAD"), cancellable = true)
    private static void voile$flipModel(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!PowerHolderComponent.hasPower(entity, FlipModelPower.class)) return;

        String string = Formatting.strip(entity.getName().getString());
        if ("Dinnerbone".equals(string) || "Grumm".equals(string)) {
            // If the entity would already be flipped due to its name, flip it back
            cir.setReturnValue(false);
        } else {
            cir.setReturnValue(true);
        }
    }
}
