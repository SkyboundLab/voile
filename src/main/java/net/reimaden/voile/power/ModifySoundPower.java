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
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundEvent;

import java.util.ArrayList;
import java.util.List;

/*
 * Based on CustomSoundPower from Apugli.
 * https://github.com/MerchantPug/apugli/blob/1.20/Common/src/main/java/net/merchantpug/apugli/power/CustomSoundPower.java
 *
 * MIT License
 *
 * Copyright (c) 2023 MerchantPug
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public abstract class ModifySoundPower extends Power {

    private final List<SoundEvent> sounds = new ArrayList<>();
    private final boolean silent;
    private final float volume;
    private final float pitch;

    public ModifySoundPower(PowerType<?> type, LivingEntity entity, boolean silent, float volume, float pitch) {
        super(type, entity);
        this.silent = silent;
        this.volume = volume;
        this.pitch = pitch;
    }

    public ModifySoundPower(PowerType<?> type, LivingEntity living, SerializableData.Instance data) {
        this(type, living, data.getBoolean("silent"), data.getFloat("volume"), data.getFloat("pitch"));
        if (data.isPresent("sound")) this.sounds.add(data.get("sound"));
        if (data.isPresent("sounds")) this.sounds.addAll(data.get("sounds"));
    }

    public boolean isSilent() {
        return this.silent;
    }

    public void playSound(Entity entity) {
        if (this.isSilent()) return;

        SoundEvent sound = switch (this.sounds.isEmpty() ? 0 : this.sounds.size()) {
            case 0 -> null;
            case 1 -> this.sounds.get(0);
            default -> this.sounds.get(entity.getWorld().getRandom().nextInt(this.sounds.size()));
        };

        if (sound == null) return;

        this.playSound(entity, sound, this.volume, this.pitch);
    }

    protected abstract void playSound(Entity entity, SoundEvent soundEvent, float volume, float pitch);

    public static SerializableData getSerializableData() {
        return new SerializableData()
                .add("silent", SerializableDataTypes.BOOLEAN, false)
                .add("sound", SerializableDataTypes.SOUND_EVENT, null)
                .add("sounds", SerializableDataType.list(SerializableDataTypes.SOUND_EVENT), null)
                .add("volume", SerializableDataTypes.FLOAT, 1.0f)
                .add("pitch", SerializableDataTypes.FLOAT, 1.0f);
    }
}
