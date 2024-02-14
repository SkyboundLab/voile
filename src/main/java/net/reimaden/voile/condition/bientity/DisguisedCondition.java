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

package net.reimaden.voile.condition.bientity;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.ladysnake.impersonate.Impersonator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Pair;
import net.reimaden.voile.Voile;

public class DisguisedCondition {

    public static boolean condition(SerializableData.Instance data, Pair<Entity, Entity> actorAndTarget) {
        Entity actor = actorAndTarget.getLeft();
        Entity target = actorAndTarget.getRight();

        if (!(actor instanceof PlayerEntity actorPlayer) || !(target instanceof PlayerEntity targetPlayer)) return false;

        return Impersonator.get(actorPlayer).getImpersonatedProfile(Voile.IMPERSONATION_KEY) == targetPlayer.getGameProfile();
    }

    public static ConditionFactory<Pair<Entity, Entity>> getFactory() {
        return new ConditionFactory<>(
                Voile.id("disguised"),
                new SerializableData(),
                DisguisedCondition::condition
        );
    }
}
