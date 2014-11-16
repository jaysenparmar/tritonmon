package com.tritonmon.Battle;

import com.tritonmon.global.Constant;
import com.tritonmon.staticmodel.Stats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattleUtil {

    // 0.0 <= prob <= 1.0
    public static boolean didRandomEvent(float prob) {
        return (Math.random() <= prob);
    }

    // stat = ((16 + (2*base) + (128/4)) * level/100) + 5
    public static int getMaxStat(String stat_name, int pokemon_id, int pokemon_level) {
        if (stat_name.equals("hp")) {
            return getMaxHP(pokemon_id, pokemon_level);
        } else {
            int stat_id = Constant.statsData.get(stat_name).getStatId();
            int base = Constant.pokemonData.get(pokemon_id).getStatIdToBaseStat().get(stat_id);
            return (Math.round((16 + 2*base) + (128/4) * 1.0f*pokemon_level/100.0f) + 5);
        }
    }

    // hp = ((16 + (2*base) + (128/4) + 100) * level/100) + 10
    private static int getMaxHP(int pokemon_id, int pokemon_level) {
        int hp_stat_id = Constant.statsData.get("hp").getStatId();
        int base = Constant.pokemonData.get(pokemon_id).getStatIdToBaseStat().get(hp_stat_id);
        return (Math.round(((16 + 2*base) + (128/4) + 100) * 1.0f*pokemon_level/100.0f) + 10);
    }

    public static Map<String, Integer> getAllMaxStats(int pokemon_id, int pokemon_level) {
        Map<String, Integer> statsMap = new HashMap<String, Integer>();
        for (Map.Entry<String, Stats> entry : Constant.statsData.entrySet()) {
            statsMap.put(entry.getKey(), getMaxStat(entry.getKey(), pokemon_id, pokemon_level));
        }
        return statsMap;
    }

    public static int getCurrentStat(String stat_name, int pokemon_id, int pokemon_level, Map<Integer, Integer> statStages) {
        if (stat_name.equals("accuracy") || stat_name.equals("evasion")) {
            return (int)(getMaxStat(stat_name, pokemon_id, pokemon_level)*
                    Constant.accuracyEvasionStageMap.get(statStages.get(Constant.statsData.get(stat_name).getStatId())));
        } else {
            return (int)(getMaxStat(stat_name, pokemon_id, pokemon_level)*
                    Constant.attackDefStageMap.get(statStages.get(Constant.statsData.get(stat_name).getStatId())));
        }
    }


}
