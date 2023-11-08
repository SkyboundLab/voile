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
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.reimaden.voile.power.ZombieArmsPower;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public class BipedEntityModelMixin {

    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftArm;

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private <T extends LivingEntity> void voile$positionArms(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (PowerHolderComponent.hasPower(livingEntity, ZombieArmsPower.class) && voile$checkPoses(livingEntity)) {
            final float modifier = 0.05f;
            final float reduction = 1.4137167f;

            this.rightArm.pitch = this.rightArm.pitch * modifier - reduction;
            this.leftArm.pitch = this.leftArm.pitch * modifier - reduction;
        }
    }

    @Unique
    private static boolean voile$checkPoses(LivingEntity livingEntity) {
        return !livingEntity.isInSwimmingPose() && !livingEntity.isFallFlying() && !livingEntity.isUsingItem();
    }
}
