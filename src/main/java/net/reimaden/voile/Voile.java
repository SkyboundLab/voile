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

package net.reimaden.voile;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.reimaden.voile.action.VoileActions;
import net.reimaden.voile.condition.VoileConditions;
import net.reimaden.voile.power.VoilePowers;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApiStatus.Internal
public class Voile implements ModInitializer {

	public static final String MOD_ID = "voile";
	public static final String MOD_NAME = "Voile";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	@Override
	public void onInitialize() {
		// Init stuff
		VoilePowers.register();
		VoileConditions.register();
		VoileActions.register();

		LOGGER.info(MOD_NAME + " has initialized. Ready to further power up your game!");
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}