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

package net.reimaden.voile.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.reimaden.voile.power.EnchantmentVulnerabilityPower;

import java.util.List;
import java.util.function.BiConsumer;

public final class EnchantmentUtil {

    private EnchantmentUtil() {}

    public static void forEachEnchantment(ItemStack stack, BiConsumer<Enchantment, Integer> consumer) {
        if (!stack.isEmpty()) {
            NbtList nbtList = stack.getEnchantments();
            for (int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbt = nbtList.getCompound(i);
                Identifier id = new Identifier(nbt.getString("id"));
                int level = nbt.getInt("lvl");
                Registries.ENCHANTMENT.getOrEmpty(id).ifPresent(enchantment -> consumer.accept(enchantment, level));
            }
        }
    }

    public static boolean isRightEnchantment(List<EnchantmentVulnerabilityPower> powers, Enchantment enchantment) {
        return powers.stream().anyMatch(power -> power.containsEnchantment(enchantment));
    }
}
