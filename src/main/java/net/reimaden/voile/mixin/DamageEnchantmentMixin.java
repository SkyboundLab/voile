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
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.reimaden.voile.power.EnchantmentVulnerabilityPower;
import net.reimaden.voile.util.EnchantmentUtil;
import net.reimaden.voile.util.ModifiedDamageEnchantment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DamageEnchantment.class)
public abstract class DamageEnchantmentMixin extends Enchantment implements ModifiedDamageEnchantment {

    @Shadow @Final public int typeIndex;

    protected DamageEnchantmentMixin(Rarity rarity, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        super(rarity, target, slotTypes);
    }

    @Override
    public float voile$getAttackDamage(int level, Entity entity) {
        List<EnchantmentVulnerabilityPower> powers = PowerHolderComponent.getPowers(entity, EnchantmentVulnerabilityPower.class);
        final float damage = (float) level * 2.5f;

        boolean hasRightEnchantment = switch (this.typeIndex) {
            case 1 -> EnchantmentUtil.isRightEnchantment(powers, EnchantmentVulnerabilityPower.Enchantment.SMITE);
            case 2 -> EnchantmentUtil.isRightEnchantment(powers, EnchantmentVulnerabilityPower.Enchantment.BANE_OF_ARTHROPODS);
            default -> false;
        };

        if (hasRightEnchantment) {
            return damage;
        }

        return this.getAttackDamage(level, entity instanceof LivingEntity livingEntity ? livingEntity.getGroup() : EntityGroup.DEFAULT);
    }

    @Inject(method = "onTargetDamaged", at = @At("HEAD"), cancellable = true)
    private void voile$applySlowness(LivingEntity user, Entity target, int level, CallbackInfo ci) {
        if (!(target instanceof LivingEntity livingEntity) || livingEntity.getGroup() == EntityGroup.ARTHROPOD
                || this.typeIndex != 2 || level <= 0) return;

        List<EnchantmentVulnerabilityPower> powers = PowerHolderComponent.getPowers(livingEntity, EnchantmentVulnerabilityPower.class);
        if (EnchantmentUtil.isRightEnchantment(powers, EnchantmentVulnerabilityPower.Enchantment.BANE_OF_ARTHROPODS)) {
            int i = 20 + user.getRandom().nextInt(10 * level);
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, i, 3));
            ci.cancel();
        }
    }
}
