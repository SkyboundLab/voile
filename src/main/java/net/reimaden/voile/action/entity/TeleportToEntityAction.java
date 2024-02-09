/*
 * This file is part of Voile, a library mod for Minecraft.
 * Copyright (C) 2024  Maxmani
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

package net.reimaden.voile.action.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Pair;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.reimaden.voile.Voile;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TeleportToEntityAction {

    public static void action(SerializableData.Instance data, Entity entity) {
        World world = entity.getWorld();
        if (world.isClient()) return;

        MinecraftServer server = entity.getServer();
        if (server == null) return;

        Consumer<Pair<Entity, Entity>> biEntityAction = data.get("bientity_action");
        Predicate<Pair<Entity, Entity>> biEntityCondition = data.get("bientity_condition");
        Consumer<Entity> failAction = data.get("fail_action");

        List<Entity> entities = StreamSupport.stream(server.getWorlds().spliterator(), false)
                .flatMap(w -> w.getEntitiesByType(TypeFilter.instanceOf(Entity.class),
                        target -> target != entity && (biEntityCondition == null || biEntityCondition.test(new Pair<>(entity, target)))).stream())
                .distinct().collect(Collectors.toList());

        if (entities.isEmpty()) {
            if (failAction != null) {
                failAction.accept(entity);
            }
            return;
        }

        Entity target = entities.get(world.getRandom().nextInt(entities.size()));
        ServerWorld serverWorld = (ServerWorld) target.getWorld();

        Vec3d vec3d = entity.getPos();
        boolean teleported = entity.teleport(serverWorld, target.getX(), target.getY(), target.getZ(), Set.of(), entity.getYaw(), entity.getPitch());
        if (teleported) {
            SoundEvent soundEvent = data.get("sound");

            if (soundEvent != null) {
                entity.getWorld().emitGameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Emitter.of(entity));
                if (!entity.isSilent()) {
                    float volume = data.get("volume");
                    float pitch = data.get("pitch");

                    entity.getWorld().playSound(null, entity.prevX, entity.prevY, entity.prevZ,
                           soundEvent, entity.getSoundCategory(), volume, pitch);
                    entity.playSound(soundEvent, volume, pitch);
                }
            }

            if (biEntityAction != null) {
                biEntityAction.accept(new Pair<>(entity, target));
            }
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                Voile.id("teleport_to_entity"),
                new SerializableData()
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("fail_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("sound", SerializableDataTypes.SOUND_EVENT)
                        .add("volume", SerializableDataTypes.FLOAT, 1.0f)
                        .add("pitch", SerializableDataTypes.FLOAT, 1.0f),
                TeleportToEntityAction::action
        );
    }
}
