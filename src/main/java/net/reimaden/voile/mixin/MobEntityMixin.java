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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.reimaden.voile.power.ModifyBehaviorPower;
import net.reimaden.voile.util.BehaviorHelper;
import net.reimaden.voile.util.EnchantmentUtil;
import net.reimaden.voile.util.ModifiedDamageEnchantment;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyVariable(method = "setTarget", at = @At("HEAD"), argsOnly = true)
    private LivingEntity voile$modifyTarget(LivingEntity target) {
        if (this.getWorld().isClient() || !(target instanceof PlayerEntity)) {
            return target;
        }

        BehaviorHelper behaviorHelper = new BehaviorHelper(target, this);

        if (behaviorHelper.checkEntity()) {
            if (behaviorHelper.behaviorMatches(ModifyBehaviorPower.EntityBehavior.PASSIVE)) {
                return null;
            }
        }

        return target;
    }

    @ModifyExpressionValue(method = "tryAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F"))
    public float voidedAttackDamage(@SuppressWarnings("unused") float original) {
        return 0;
    }

    @ModifyVariable(method = "tryAttack", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F")), at = @At(value = "STORE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F"), ordinal = 0)
    private float voile$modifyAttackDamage(float original, Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) return original;

        ItemStack stack = this.getMainHandStack();
        MutableFloat mut = new MutableFloat(original);

        EnchantmentUtil.forEachEnchantment(stack, (enchantment, level) -> {
            if (enchantment instanceof ModifiedDamageEnchantment modified) {
                mut.add(modified.voile$getAttackDamage(level, livingEntity));
            } else {
                mut.add(enchantment.getAttackDamage(level, livingEntity.getGroup()));
            }
        });

        return mut.floatValue();
    }
}
