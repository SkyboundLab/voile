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

package net.reimaden.voile.action.bientity;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.ladysnake.impersonate.Impersonator;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.reimaden.voile.Voile;

public class DisguiseAction {

    public static void action(SerializableData.Instance data, Pair<Entity, Entity> actorAndTarget) {
        Entity actor = actorAndTarget.getLeft();
        Entity target = actorAndTarget.getRight();

        if (!(actor instanceof ServerPlayerEntity serverActor) || !(target instanceof ServerPlayerEntity serverTarget)) return;

        Impersonator impersonator = Impersonator.get(serverActor);
        if (impersonator.isImpersonating() && !data.getBoolean("overwrite")) return;
        impersonator.impersonate(Voile.IMPERSONATION_KEY, serverTarget.getGameProfile());
    }

    public static ActionFactory<Pair<Entity, Entity>> getFactory() {
        return new ActionFactory<>(
                Voile.id("disguise"),
                new SerializableData()
                        .add("overwrite", SerializableDataTypes.BOOLEAN, true),
                DisguiseAction::action
        );
    }
}
