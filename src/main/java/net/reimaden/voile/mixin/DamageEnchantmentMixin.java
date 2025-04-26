package net.reimaden.voile.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.reimaden.voile.power.EnchantmentVulnerabilityPower;
import net.reimaden.voile.util.EnchantmentUtil;
import net.reimaden.voile.util.ModifiedDamageEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Enchantment.class) // Change to the specific enchantment class you are targeting
public abstract class DamageEnchantmentMixin implements ModifiedDamageEnchantment {

    @Unique
    private boolean voile$isImpaling() {
        return (Object)this == Enchantments.IMPALING;
    }

    @Override
    public float voile$getAttackDamage(int level, Entity entity) {
        List<EnchantmentVulnerabilityPower> powers = PowerHolderComponent.getPowers(entity, EnchantmentVulnerabilityPower.class);
        final float damage = (float) level * 2.5f;

        boolean hasRightEnchantment = switch (this.typeIndex) {
            case 1 -> EnchantmentUtil.isRightEnchantment(powers, Enchantments.SMITE);
            case 2 -> EnchantmentUtil.isRightEnchantment(powers, Enchantments.BANE_OF_ARTHROPODS);
            default -> false;
        };

        if (hasRightEnchantment) {
            return damage;
        }

        return this.getAttackDamage(level, entity instanceof LivingEntity livingEntity ? livingEntity.getGroup() : null); // Adjust entity group handling
    }

    @Inject(method = "onTargetDamaged", at = @At("HEAD"), cancellable = true)
    private void voile$applySlowness(LivingEntity user, Entity target, int level, CallbackInfo ci) {
        if (!(target instanceof LivingEntity livingEntity) || livingEntity.getGroup() == null
                || this.typeIndex != 2 || level <= 0) return;

        List<EnchantmentVulnerabilityPower> powers = PowerHolderComponent.getPowers(livingEntity, EnchantmentVulnerabilityPower.class);
        if (EnchantmentUtil.isRightEnchantment(powers, Enchantments.BANE_OF_ARTHROPODS)) {
            int i = 20 + user.getRandom().nextInt(10 * level);
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, i, 3));
            ci.cancel();
        }
    }
}
