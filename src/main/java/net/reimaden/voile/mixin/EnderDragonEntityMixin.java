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
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.reimaden.voile.power.ModifyBehaviorPower;
import net.reimaden.voile.util.BehaviorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends MobEntity implements Monster {

    protected EnderDragonEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "launchLivingEntities", at = @At("HEAD"))
    private void voile$preventLaunching(List<Entity> entities, CallbackInfo ci) {
        List<Entity> players = entities.stream().filter(entity -> entity instanceof PlayerEntity).toList();

        if (!players.isEmpty()) {
            for (Entity target : players) {
                BehaviorHelper behaviorHelper = new BehaviorHelper(target, this);

                if (behaviorHelper.checkEntity()) {
                    if (behaviorHelper.behaviorMatches(ModifyBehaviorPower.EntityBehavior.PASSIVE) ||
                            (behaviorHelper.behaviorMatches(ModifyBehaviorPower.EntityBehavior.NEUTRAL) && this.getAttacker() != target)) {
                        entities.remove(target);
                    }
                }
            }
        }
    }

    @Inject(method = "damageLivingEntities", at = @At("HEAD"))
    private void voile$preventDamaging(List<Entity> entities, CallbackInfo ci) {
        List<Entity> players = entities.stream().filter(entity -> entity instanceof PlayerEntity).toList();

        if (!players.isEmpty()) {
            for (Entity target : players) {
                BehaviorHelper behaviorHelper = new BehaviorHelper(target, this);

                if (behaviorHelper.checkEntity()) {
                    if (behaviorHelper.behaviorMatches(ModifyBehaviorPower.EntityBehavior.PASSIVE) ||
                            (behaviorHelper.behaviorMatches(ModifyBehaviorPower.EntityBehavior.NEUTRAL) && this.getAttacker() != target)) {
                        entities.remove(target);
                    }
                }
            }
        }
    }
}
