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

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.reimaden.voile.util.EnchantmentUtil;
import net.reimaden.voile.util.ModifiedDamageEnchantment;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(TridentEntity.class)
public class TridentEntityMixin {

    @Shadow private ItemStack tridentStack;

    @ModifyVariable(method = "onEntityHit", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F")), at = @At(value = "STORE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F"), ordinal = 0)
    private float voile$modifyAttackDamage(float original, EntityHitResult entityHitResult) {
        LivingEntity livingEntity = (LivingEntity) entityHitResult.getEntity();
        MutableFloat mut = new MutableFloat(original);

        EnchantmentUtil.forEachEnchantment(this.tridentStack, (enchantment, level) -> {
            if (enchantment instanceof ModifiedDamageEnchantment modified) {
                mut.add(modified.voile$getAttackDamage(level, livingEntity));
            } else {
                mut.add(enchantment.getAttackDamage(level, livingEntity.getGroup()));
            }
        });

        return mut.floatValue();
    }
}
