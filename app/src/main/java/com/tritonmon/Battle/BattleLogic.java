package com.tritonmon.Battle;

import com.tritonmon.global.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BattleLogic {

    //damage = (((((2*poke1_level)+10)/250)*(attack/defence)*base)+2)*modifier
    //modifier = stab*type*critical*other*random(0.85,1)
    public static BattleResponse damageDone(int pokemon1_id, int pokemon1_level, int move_id, int pokemon2_id, int pokemon2_level) {

        float tmp = ((2.0f*pokemon1_level)+10.0f)/250.0f;

        // should check if their stats were afflicted
        float attack = getMaxStat("attack", pokemon1_id, pokemon1_level);
        float defense = getMaxStat("defense", pokemon2_id, pokemon2_level);

        float base = 1.0f* Constant.movesData.get(move_id).getPower();
        List<Integer> pokemon1_types = Constant.pokemonData.get(pokemon1_id).getTypeIds();
        List<Integer> pokemon2_types = Constant.pokemonData.get(pokemon2_id).getTypeIds();
        int move_type = Constant.movesData.get(move_id).getTypeId();
        boolean isStab;
        float stab = 1.0f;
        float type = 1.0f;
        float crit = 1.0f;
        float other = 1.0f;
        float randomVar = generateBattleRandomNumber();
        boolean didCrit = false;
        if (pokemon1_types.contains(move_type)) {
            stab*=1.5f;
        }
        for (Integer ele : pokemon2_types) {
            type*=(1.0f*(Constant.typesData.get(move_type).getTargetTypeToDamageFactor().get(ele))/100.0f);
        }
        boolean superEffective = type > 1.0f ? true : false;
        boolean notEffective = type < 1.0f ? true : false;
        if (didCrit(Constant.movesData.get(move_id).getCritRate())) {
            crit*=1.5f;
            didCrit = true;
        }
        float damage = ((tmp*attack*base/defense)+2.0f)*stab*type*crit*other*randomVar;
        return new BattleResponse(damage, didCrit, superEffective, notEffective, false);
    }

    // stat = ((16 + (2*base) + (128/4)) * level/100) + 5
    public static int getMaxStat(String stat_name, int pokemon_id, int pokemon_level) {
        if (stat_name.equals("hp")) {
            return getMaxHP(pokemon_id, pokemon_level);
        }
        else {
            int stat_id = Constant.statsData.get(stat_name).getStatId();
            int base = Constant.pokemonData.get(pokemon_id).getStatIdToBaseStat().get(stat_id);
            return (Math.round((16 + 2*base) + (128/4) * 1.0f*pokemon_level/100.0f) + 5);
        }
    }

    public static int newHp(int currentHp, int damage) {
        return currentHp >= damage ? currentHp-damage : 0;
    }

    public static List<Integer> getNewMoves(int pokemon_id, int starting_level, int ending_level) {
        List<Integer> moves = new ArrayList<Integer>();
        Map<Integer, List<Integer>> levelToMoves = Constant.pokemonData.get(pokemon_id).getLevelToMoves();
        for (int i = starting_level; i < ending_level + 1; i++) {
            if (levelToMoves.containsKey(i)) {
                moves.addAll(levelToMoves.get(i));
            }
        }
        return moves;
    }

    private static float generateBattleRandomNumber() {
        return (float)(0.85f+(Math.random()*0.15f));
    }

    private static boolean didCrit(int critChance) {
        return (Math.random() <= Constant.criticalChanceMap.get(critChance));
    }

    // hp = ((16 + (2*base) + (128/4) + 100) * level/100) + 10
    private static int getMaxHP(int pokemon_id, int pokemon_level) {
        int hp_stat_id = Constant.statsData.get("hp").getStatId();
        int base = Constant.pokemonData.get(pokemon_id).getStatIdToBaseStat().get(hp_stat_id);
        return (Math.round(((16 + 2*base) + (128/4) + 100) * 1.0f*pokemon_level/100.0f) + 10);
    }

}
