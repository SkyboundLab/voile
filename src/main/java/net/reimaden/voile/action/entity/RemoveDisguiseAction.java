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

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.ladysnake.impersonate.Impersonator;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.reimaden.voile.Voile;

public class RemoveDisguiseAction {

    public static void action(SerializableData.Instance data, Entity entity) {
        if (!(entity instanceof ServerPlayerEntity player)) return;

        Impersonator impersonator = Impersonator.get(player);
        if (impersonator.isImpersonating()) {
            impersonator.stopImpersonation(Voile.IMPERSONATION_KEY);
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                Voile.id("remove_disguise"),
                new SerializableData(),
                RemoveDisguiseAction::action
        );
    }
}
