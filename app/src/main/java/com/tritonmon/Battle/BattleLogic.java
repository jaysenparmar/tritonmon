package com.tritonmon.Battle;

import com.tritonmon.global.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BattleLogic {

    //damage = (((((2*poke1_level)+10)/250)*(attack/defence)*base)+2)*modifier
    //modifier = stab*type*critical*other*random(0.85,1)
    public static BattleResponse damageDone(boolean isWild, int pokemon1_id, int pokemon1_level, int move_id, int pokemon2_id, int pokemon2_level) {

        float tmp = ((2.0f*pokemon1_level)+10.0f)/250.0f;

        // should check if their stats were afflicted
        float attack = getMaxStat(pokemon1_id, pokemon1_level, "attack");
        float defense = getMaxStat(pokemon2_id, pokemon2_level, "defense");

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
        int xpGained = xpGained(isWild, pokemon2_id, pokemon2_level);
        return new BattleResponse(damage, didCrit, superEffective, notEffective, false, xpGained);
    }

    private static float generateBattleRandomNumber() {
        return new Double(0.85+(Math.random()*0.15)).floatValue();
    }

    // 0.0 <= prob <= 1.0
    private static boolean didRandomEvent(float prob) {
        return (Math.random() <= prob);
    }

    private static boolean didCrit(int critChance) {
        return didRandomEvent(Constant.criticalChanceMap.get(critChance));
    }

    // stat = ((16 + (2*base) + (128/4)) * level/100) + 5
    public static int getMaxStat(int pokemon_id, int pokemon_level, String stat_name) {
        if (stat_name == "hp") {
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

    public static int newHp(int currentHp, int damage) {
        return currentHp >= damage ? currentHp-damage : 0;
    }

    public static List<Integer> possibleLearnedMoves (int pokemon_id, int starting_level, int ending_level) {
        List<Integer> moves = new ArrayList<Integer>();
        Map<Integer, List<Integer>> levelToMoves = Constant.pokemonData.get(pokemon_id).getLevelToMoves();
        for (int i = starting_level; i < ending_level + 1; i++) {
            moves.addAll(levelToMoves.get(i));
        }
        return moves;
    }
//    delta xp = a*b*L/(7*s)
//    a = 1 if fainted is wild, 1.5 if fainted is trained
//    b = base xp of fainted pokemon
//    L = level of fainted pokemon
//    s = # of non-fainted pokemon that participated in battle
    public static int xpGained(boolean isWild, int pokemon2_id, int pokemon2_level) {
        float wildFactor = isWild ? 1.5f : 1.0f;
        int base = Constant.pokemonData.get(pokemon2_id).getBaseXP();
        return Math.round(wildFactor*base*pokemon2_level/(7*1));
    }

// a = (3*hpmax - 2*hpcurr) * pokemon's_capture_rate * ball_bonus * bonus_state / (3*hp_max)
// b = (2^16 - 1) * (a/(2^8 - 1))^0.25
// if a>255: caught
// else: prob(caught) = ((b+1)/2^16)^4

    public boolean caughtPokemon(int pokemon2_id, int pokemon2_level, int pokemon2_currhp, String pokemon2_status) {
        int maxHp = getMaxHP(pokemon2_id, pokemon2_level);
        int pokemon2_capture_rate = Constant.pokemonData.get(pokemon2_id).getCaptureRate();
        float status_capture_rate =  Constant.moveMetaAilmentsData.get(pokemon2_status).getCatchRate();
        float a = ((3*maxHp) - (2*pokemon2_currhp)) * pokemon2_capture_rate * 1 * status_capture_rate / (3*maxHp);

        if (a >= 255) {
            return true;
        } else {
            // might need doubles
            float b = (float)((Math.pow(2.0,  16.0)-1) * Math.pow((a/(Math.pow(2.0, 8.0)-1)),0.25));
            float p = (float)(Math.pow(((b+1.0)/Math.pow(2.0, 16.0)),4.0));
            return didRandomEvent(p);
        }
    }



}
