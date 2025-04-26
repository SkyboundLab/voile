package net.reimaden.voile.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.reimaden.voile.power.EnchantmentVulnerabilityPower;
import net.reimaden.voile.util.EnchantmentUtil;
import net.reimaden.voile.util.ModifiedDamageEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(Enchantment.class)
public abstract class ImpalingEnchantmentMixin implements ModifiedDamageEnchantment {

    @Unique
    private boolean voile$isImpaling() {
        return (Object)this == Enchantments.IMPALING;
    }

    @Override
    public float voile$getAttackDamage(int level, Entity entity) {
        if (!voile$isImpaling()) {
            return 0.0F;
        }

        List<EnchantmentVulnerabilityPower> powers = PowerHolderComponent.getPowers(entity, EnchantmentVulnerabilityPower.class);

        if (EnchantmentUtil.isRightEnchantment(powers, Enchantments.IMPALING)) {
            return (float) level * 2.5f;
        }

        return this.getAttackDamage(level, entity instanceof LivingEntity livingEntity ? livingEntity.getGroup() : null);
    }
}
