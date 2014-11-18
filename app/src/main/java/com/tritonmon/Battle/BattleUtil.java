package com.tritonmon.Battle;

import com.tritonmon.global.Constant;
import com.tritonmon.staticmodel.Stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class BattleUtil {

    // 0.0 <= prob <= 1.0
    public static boolean didRandomEvent(float prob) {
        return (Math.random() <= prob);
    }

    // stat = ((16 + (2*base) + (128/4)) * level/100) + 5
    public static int getMaxStat(String stat_name, int pokemon_id, int pokemon_level) {
        if (stat_name.equals(Stats.HP)) {
            return getMaxHP(pokemon_id, pokemon_level);
        }
        else if (stat_name.equals(Stats.ACCURACY) || stat_name.equals(Stats.EVASION)) {
            return 100;
        }
        else {
            int stat_id = Constant.statsData.get(stat_name).getStatId();
            int base = Constant.pokemonData.get(pokemon_id).getStatIdToBaseStat().get(stat_id);
            return (Math.round((16 + 2*base) + (128/4) * 1.0f*pokemon_level/100.0f) + 5);
        }
    }

    // hp = ((16 + (2*base) + (128/4) + 100) * level/100) + 10
    private static int getMaxHP(int pokemon_id, int pokemon_level) {
        int hp_stat_id = Constant.statsData.get(Stats.HP).getStatId();
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
        if (stat_name.equals(Stats.ACCURACY) || stat_name.equals(Stats.EVASION)) {
            return (int)(getMaxStat(stat_name, pokemon_id, pokemon_level)*
                    Constant.accuracyEvasionStageMap.get(statStages.get(Constant.statsData.get(stat_name).getStatId())));
        }
        else {
            return (int)(getMaxStat(stat_name, pokemon_id, pokemon_level)*
                    Constant.attackDefStageMap.get(statStages.get(Constant.statsData.get(stat_name).getStatId())));
        }
    }

    public static List<Integer> generateMoves(Map<Integer, List<Integer>> possMoves) {
        List<Integer> chosenMoves = new ArrayList<Integer>();
        SortedMap<Integer, Integer> moveProbMap = new TreeMap<Integer, Integer>();

        int count = 0;
        int total = 0;
        for (Map.Entry<Integer, List<Integer>> entry : possMoves.entrySet()) {
            for (Integer innerEle : entry.getValue()) {
                total+=entry.getKey();
                moveProbMap.put(total, innerEle);
                count++;
            }
        }

        if (count <= 4) {
            for (Map.Entry<Integer, List<Integer>> entry : possMoves.entrySet()) {
                for (Integer innerEle : entry.getValue()) {
                    chosenMoves.add(innerEle);
                }
            }
            while (chosenMoves.size() < 4) {
                chosenMoves.add(null);
            }
            return chosenMoves;
        }

        int chosenMove;
        while (chosenMoves.size() < 4) {
            chosenMove = populateChosenMoves(total, moveProbMap);
            if (!chosenMoves.contains(chosenMove)) {
                chosenMoves.add(chosenMove);
            }
        }
        return chosenMoves;
    }

    // TODO: check this actually works
    private static int populateChosenMoves(int total, SortedMap<Integer, Integer> moveProbMap) {
        double randomVar = Math.random() * total;

        for (Map.Entry<Integer, Integer> entry : moveProbMap.entrySet()) {
            if (entry.getKey() > randomVar) {
                return entry.getValue();
            }
        }
        // technically should never reach here
        return moveProbMap.lastKey();

    }


}
