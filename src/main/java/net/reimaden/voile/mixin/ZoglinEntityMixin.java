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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.Hoglin;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.reimaden.voile.power.ModifyBehaviorPower;
import net.reimaden.voile.util.BehaviorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ZoglinEntity.class)
public abstract class ZoglinEntityMixin extends HostileEntity implements Monster, Hoglin {

    protected ZoglinEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getHoglinTarget", at = @At("RETURN"), cancellable = true)
    private void voile$preventTargeting(CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
        Optional<? extends LivingEntity> entity = cir.getReturnValue();

        if (entity.isPresent()) {
            LivingEntity target = entity.get();

            if (target instanceof PlayerEntity player) {
                BehaviorHelper behaviorHelper = new BehaviorHelper(player, this);

                if (behaviorHelper.checkEntity()) {
                    if (behaviorHelper.neutralOrPassive()) {
                        cir.setReturnValue(Optional.empty());
                    }
                }
            }
        }
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/ZoglinEntity;setAttackTarget(Lnet/minecraft/entity/LivingEntity;)V"), cancellable = true)
    private void voile$lobotomizeZoglin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = source.getAttacker();
        if (!(entity instanceof PlayerEntity player)) return;

        BehaviorHelper behaviorHelper = new BehaviorHelper(player, this);

        if (behaviorHelper.checkEntity()) {
            if (behaviorHelper.behaviorMatches(ModifyBehaviorPower.EntityBehavior.PASSIVE)) {
                cir.setReturnValue(true);
            }
        }
    }
}
