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

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.reimaden.voile.Voile;

public class VoileEvents {

    public static void register() {
        Voile.LOGGER.debug("Registering events for " + Voile.MOD_ID);

        ServerLivingEntityEvents.ALLOW_DEATH.register(new ConvertEntityEvent());
        ServerLivingEntityEvents.AFTER_DEATH.register(new ListenToEntityDeathEvent());
    }
}
