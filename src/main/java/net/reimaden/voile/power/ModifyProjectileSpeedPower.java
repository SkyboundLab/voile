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
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.reimaden.voile.Voile;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModifyProjectileSpeedPower extends ValueModifyingPower {

    private final Predicate<Entity> projectileCondition;

    private Consumer<Entity> selfAction;
    private Consumer<Entity> projectileAction;

    public ModifyProjectileSpeedPower(PowerType<?> type, LivingEntity entity, Predicate<Entity> projectileCondition) {
        super(type, entity);
        this.projectileCondition = projectileCondition;
    }

    public boolean doesApply(Entity projectile) {
        return this.projectileCondition == null || this.projectileCondition.test(projectile);
    }

    public void setSelfAction(Consumer<Entity> selfAction) {
        this.selfAction = selfAction;
    }

    public void setProjectileAction(Consumer<Entity> projectileAction) {
        this.projectileAction = projectileAction;
    }

    public void executeActions(Entity projectile, Entity shooter) {
        if (this.selfAction != null) {
            this.selfAction.accept(shooter);
        }
        if (this.projectileAction != null) {
            this.projectileAction.accept(projectile);
        }
    }

    public static PowerFactory<Power> createFactory() {
        return new PowerFactory<>(
                Voile.id("modify_projectile_speed"),
                new SerializableData()
                        .add("modifier", Modifier.DATA_TYPE, null)
                        .add("modifiers", Modifier.LIST_TYPE, null)
                        .add("projectile_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("self_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("projectile_action", ApoliDataTypes.ENTITY_ACTION, null),
                data -> (type, entity) -> {
                    ModifyProjectileSpeedPower power = new ModifyProjectileSpeedPower(type, entity,
                            data.get("projectile_condition"));
                    data.ifPresent("modifier", power::addModifier);
                    data.<List<Modifier>>ifPresent("modifiers",
                            mods -> mods.forEach(power::addModifier)
                    );
                    if (data.isPresent("self_action")) {
                        power.setSelfAction(data.get("self_action"));
                    }
                    if (data.isPresent("target_action")) {
                        power.setProjectileAction(data.get("projectile_action"));
                    }
                    return power;
                })
                .allowCondition();
    }
}
