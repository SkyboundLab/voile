/*
 * This file is part of Voile, a library mod for Minecraft.
 * Copyright (C) 2024  Maxmani
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

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Pair;
import net.reimaden.voile.Voile;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnEntityDeathPower extends Power {

    private final Consumer<Pair<Entity, Entity>> bientityAction;
    private final Predicate<Pair<Entity, Entity>> biEntityCondition;
    private final Predicate<Pair<DamageSource, Float>> damageCondition;

    public ActionOnEntityDeathPower(PowerType<?> type, LivingEntity entity, Consumer<Pair<Entity, Entity>> bientityAction,
                                    Predicate<Pair<Entity, Entity>> biEntityCondition, Predicate<Pair<DamageSource, Float>> damageCondition) {
        super(type, entity);
        this.bientityAction = bientityAction;
        this.biEntityCondition = biEntityCondition;
        this.damageCondition = damageCondition;
    }

    public boolean doesApply(Entity target, DamageSource source, float amount) {
        return (this.biEntityCondition == null || this.biEntityCondition.test(new Pair<>(this.entity, target))) &&
                (this.damageCondition == null || this.damageCondition.test(new Pair<>(source, amount)));
    }

    public void onDeath(Entity target) {
        this.bientityAction.accept(new Pair<>(this.entity, target));
    }

    public static PowerFactory<Power> createFactory() {
        return new PowerFactory<>(
                Voile.id("action_on_entity_death"),
                new SerializableData()
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null),
                data -> (type, entity) -> new ActionOnEntityDeathPower(type, entity, data.get("bientity_action"), data.get("bientity_condition"), data.get("damage_condition"))
        ).allowCondition();
    }
}
