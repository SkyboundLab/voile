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

package net.reimaden.voile.event;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Difficulty;
import net.minecraft.world.WorldEvents;
import net.reimaden.voile.power.ConvertEntityPower;

import java.util.Optional;

public class ConvertEntityEvent implements ServerLivingEntityEvents.AllowDeath {

    @Override
    public boolean allowDeath(LivingEntity entity, DamageSource source, float amount) {
        if (!(source.getAttacker() instanceof LivingEntity killer)) return true;
        if (!(entity instanceof MobEntity mobEntity) || killer instanceof ZombieEntity) return true;

        Optional<ConvertEntityPower> convertEntityPower = PowerHolderComponent.getPowers(killer, ConvertEntityPower.class).stream()
                .filter(power -> power.checkEntity(mobEntity)).findFirst();

        // Make sure we don't proceed to convert the entity if we don't have a power for it
        if (convertEntityPower.isEmpty()) return true;
        ConvertEntityPower power = convertEntityPower.get();
        ServerWorld world = (ServerWorld) entity.getWorld();

        if (!power.ignoreDifficulty()) {
            // Follow the same rules as vanilla for converting villagers
            Difficulty difficulty = world.getDifficulty();
            if (difficulty == Difficulty.EASY || difficulty == Difficulty.PEACEFUL) return true;
            else if (difficulty == Difficulty.NORMAL && world.getRandom().nextBoolean()) return true;
        }

        // Get the entity to convert to
        EntityType<? extends MobEntity> newEntity = power.getNewEntity();

        // We need to handle villager conversion separately
        if (mobEntity instanceof VillagerEntity villagerEntity && newEntity == EntityType.ZOMBIE_VILLAGER) {
            ZombieVillagerEntity zombieVillagerEntity = villagerEntity.convertTo(EntityType.ZOMBIE_VILLAGER, false);
            if (zombieVillagerEntity != null) {
                convertVillager(killer, villagerEntity, zombieVillagerEntity, world);
                // Return false to prevent death
                return false;
            }
        }

        // Convert the entity and return false to prevent death
        MobEntity convertedTo = mobEntity.convertTo(newEntity, true);
        if (convertedTo != null) {
            convertedTo.playAmbientSound();
            convertedTo.setAttacker(killer);
        }
        return false;
    }

    private static void convertVillager(Entity killer, VillagerEntity villagerEntity, ZombieVillagerEntity zombieVillagerEntity, ServerWorld serverWorld) {
        zombieVillagerEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(zombieVillagerEntity.getBlockPos()),
                SpawnReason.CONVERSION, new ZombieEntity.ZombieData(false, true), null);
        zombieVillagerEntity.setVillagerData(villagerEntity.getVillagerData());
        zombieVillagerEntity.setGossipData(villagerEntity.getGossip().serialize(NbtOps.INSTANCE));
        zombieVillagerEntity.setOfferData(villagerEntity.getOffers().toNbt());
        zombieVillagerEntity.setXp(villagerEntity.getExperience());
        if (!killer.isSilent()) {
            serverWorld.syncWorldEvent(null, WorldEvents.ZOMBIE_INFECTS_VILLAGER, killer.getBlockPos(), 0);
        }
    }
}
