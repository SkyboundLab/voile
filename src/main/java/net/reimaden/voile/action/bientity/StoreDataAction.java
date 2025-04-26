package net.reimaden.voile.action.bientity;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;  // Updated import for newer Minecraft versions
import net.minecraft.util.Pair;
import net.reimaden.voile.Voile;

public class StoreDataAction {

    public static void action(SerializableData.Instance data, Pair<Entity, Entity> actorAndTarget) {
        Entity actor = actorAndTarget.getLeft();
        Entity target = actorAndTarget.getRight();

        if (actor == null || target == null) return;

        NbtCompound nbt = target.writeNbt(new NbtCompound());

        String name = actor instanceof PlayerEntity playerEntity ? playerEntity.getName().getString() : actor.getUuidAsString();

        int score = 0;
        String nbtData = data.getString("path");

        String[] keys = nbtData.split("\\.");
        NbtCompound currentNbt = nbt;
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            if (key.contains("[")) {
                String arrayKey = key.substring(0, key.indexOf("["));
                String indexString = key.substring(key.indexOf("[") + 1, key.indexOf("]"));
                int index = Integer.parseInt(indexString);
                if (currentNbt.contains(arrayKey, 11) && currentNbt.getIntArray(arrayKey).length > index) {
                    score = currentNbt.getIntArray(arrayKey)[index];
                } else {
                    return;
                }
                break;
            } else if (i == keys.length - 1) {
                score = currentNbt.getInt(key);
            } else {
                currentNbt = currentNbt.getCompound(key);
            }
        }

        Scoreboard scoreboard = actor.getWorld().getScoreboard();
        ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(data.getString("objective"));

        if (scoreboardObjective != null) {
            // Use ScoreboardScore instead of ScoreboardPlayerScore
            ScoreboardScore scoreboardScore = scoreboard.getPlayerScore(name, scoreboardObjective);
            scoreboardScore.setScore(score);
        }
    }

    public static ActionFactory<Pair<Entity, Entity>> getFactory() {
        return new ActionFactory<>(
                Voile.id("store_data"),
                new SerializableData()
                        .add("path", SerializableDataTypes.STRING)
                        .add("objective", SerializableDataTypes.STRING),
                StoreDataAction::action
        );
    }
}
