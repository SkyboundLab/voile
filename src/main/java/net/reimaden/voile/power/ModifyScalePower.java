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

package net.reimaden.voile.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModifyScalePower extends Power {

    public static final ArrayList<Identifier> DEFAULT_SCALE_TYPES = new ArrayList<>() {{
        add(ScaleRegistries.getId(ScaleRegistries.SCALE_TYPES, ScaleTypes.WIDTH));
        add(ScaleRegistries.getId(ScaleRegistries.SCALE_TYPES, ScaleTypes.HEIGHT));
        add(ScaleRegistries.getId(ScaleRegistries.SCALE_TYPES, ScaleTypes.DROPS));
        add(ScaleRegistries.getId(ScaleRegistries.SCALE_TYPES, ScaleTypes.VISIBILITY));
    }};

    private static final int DEFAULT_SCALE = 1;
    private final Set<ScaleType> scaleTypes = new HashSet<>();
    private final float scale;

    public ModifyScalePower(PowerType<?> type, LivingEntity entity, List<Identifier> identifiers, float scale) {
        super(type, entity);
        identifiers.forEach(identifier -> scaleTypes.add(ScaleRegistries.SCALE_TYPES.get(identifier)));
        this.scale = scale;
        setTicking(true);
    }

    @Override
    public void tick() {
        this.scaleTypes.forEach(this::updateScale);
    }

    private void updateScale(ScaleType scaleType) {
        ScaleData data = scaleType.getScaleData(entity);
        if (this.isActive()) {
            if (data.getScale() != this.scale) {
                data.setScale(this.scale);
            }
        } else if (data.getScale() != DEFAULT_SCALE) {
            data.setScale(DEFAULT_SCALE);
        }
    }

    @Override
    public void onLost() {
        this.scaleTypes.forEach(scaleType -> scaleType.getScaleData(entity).resetScale());
    }
}
