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

package net.reimaden.voile.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.ImpalingEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.reimaden.voile.power.EnchantmentVulnerabilityPower;
import net.reimaden.voile.util.EnchantmentUtil;
import net.reimaden.voile.util.ModifiedDamageEnchantment;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(ImpalingEnchantment.class)
public abstract class ImpalingEnchantmentMixin extends Enchantment implements ModifiedDamageEnchantment {

    protected ImpalingEnchantmentMixin(Rarity rarity, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        super(rarity, target, slotTypes);
    }

    @Override
    public float voile$getAttackDamage(int level, Entity entity) {
        List<EnchantmentVulnerabilityPower> powers = PowerHolderComponent.getPowers(entity, EnchantmentVulnerabilityPower.class);

        if (EnchantmentUtil.isRightEnchantment(powers, Enchantments.IMPALING)) {
            return (float) level * 2.5f;
        }

        return this.getAttackDamage(level, entity instanceof LivingEntity livingEntity ? livingEntity.getGroup() : EntityGroup.DEFAULT);
    }
}
