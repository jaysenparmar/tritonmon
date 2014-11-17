package com.tritonmon.Battle;

import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.staticmodel.MoveMetaAilments;
import com.tritonmon.staticmodel.Moves;

import java.util.Collections;
import java.util.List;
import java.util.Map;

// TODO: status stuff
public class MoveHandler {

    public static MoveResponse doMove(MoveRequest moveRequest) {
        BattlingPokemon pokemon1 = moveRequest.getPokemon1();
        BattlingPokemon pokemon2 = moveRequest.getPokemon2();
        boolean isWild = pokemon2.isWild();
        int moveIdIndex = pokemon1.getMoves().indexOf(moveRequest.getMoveId());
        if (!stillHavePP(pokemon1.getPps().get(moveIdIndex))) {
            return new MoveResponse();
        }

        int AI_move_id = determineAIMove(pokemon2.getMoves());

        // determine move order
        int pokemon1_priority = Constant.movesData.get(moveRequest.getMoveId()).getPriority();
        int pokemon2_priority = Constant.movesData.get(AI_move_id).getPriority();

        // pokemon1 attacks first
        if (pokemon1_priority > pokemon2_priority) {
            return humanMovesFirst(moveRequest, AI_move_id);

        } else if (pokemon1_priority == pokemon2_priority) {
            int pokemon1_speed = BattleUtil.getCurrentStat("speed", pokemon1.getPokemonId(), pokemon1.getLevel(), pokemon1.getStatsStages());
            int pokemon2_speed = BattleUtil.getCurrentStat("speed", pokemon2.getPokemonId(), pokemon2.getLevel(), pokemon2.getStatsStages());

            // yes i give pref to the player cuz im nice
            // pokemon1 attacks first
            if (pokemon1_speed >= pokemon2_speed) {
                return humanMovesFirst(moveRequest, AI_move_id);
            } else {
                return AIMovesFirst(moveRequest, AI_move_id);
            }
        } else {
            return AIMovesFirst(moveRequest, AI_move_id);
        }
    }

    private static MoveResponse humanMovesFirst(MoveRequest moveRequest, int AI_move_id) {
        MoveResponse firstMoveResponse = doAttack(moveRequest);
        BattleMessages battleMessages1 = firstMoveResponse.getBattleMessages1();
        MoveRequest secondMoveRequest = new MoveRequest(firstMoveResponse.getPokemon2(), firstMoveResponse.getPokemon1(), AI_move_id);
        MoveResponse secondMoveResponse = doAttack(secondMoveRequest);
        BattleMessages battleMessages2 = secondMoveResponse.getBattleMessages1();
        return new MoveResponse(secondMoveResponse.getPokemon2(), secondMoveResponse.getPokemon1(), true, battleMessages1, battleMessages2);
    }

    private static MoveResponse AIMovesFirst(MoveRequest moveRequest, int AI_move_id) {
        MoveRequest firstMoveRequest = new MoveRequest(moveRequest.getPokemon2(), moveRequest.getPokemon1(), AI_move_id);
        MoveResponse firstMoveResponse = doAttack(firstMoveRequest);
        BattleMessages battleMessages1 = firstMoveResponse.getBattleMessages1();
        MoveRequest secondMoveRequest = new MoveRequest(firstMoveResponse.getPokemon2(), firstMoveResponse.getPokemon1(), moveRequest.getMoveId());
        MoveResponse secondMoveResponse = doAttack(secondMoveRequest);
        BattleMessages battleMessages2 = secondMoveResponse.getBattleMessages1();
        return new MoveResponse(secondMoveResponse.getPokemon1(), secondMoveResponse.getPokemon2(), false, battleMessages1, battleMessages2);
    }


//    private MoveResponse afflictAilment(MoveRequest moveRequest) {
//        int pokemon1_status = moveRequest.getPokemon1().getAilment();
//        int pokemon2_status = moveRequest.getPokemon2().getAilment();
//        if (pokemon1_status == Constant.moveMetaAilmentsData.get("none").getMoveMetaAilmentId()) {
//
//        }
//        if (pokemon1_status == Constant.moveMetaAilmentsData.get("paralysis").getMoveMetaAilmentId()) {
//
//        } else {
//
//        }
//
//    }

    private static boolean stillHavePP(int pp) {
        return pp > 0;
    }

    //damage = (((((2*poke1_level)+10)/250)*(attack/defence)*base)+2)*modifier
    //modifier = stab*type*critical*other*random(0.85,1)
    private static MoveResponse doAttack(MoveRequest moveRequest) {

        // determine status stuff somewhere

        BattlingPokemon pokemon1 = moveRequest.getPokemon1();
        BattlingPokemon pokemon2 = moveRequest.getPokemon2();
        Log.e("MoveHandler", pokemon1.toString());
        int pokemon1_id = pokemon1.getPokemonId();
        int pokemon1_level = pokemon1.getLevel();
        int pokemon2_id = pokemon2.getPokemonId();
        int pokemon2_level = pokemon2.getLevel();
        int move_id = moveRequest.getMoveId();
        int move_index = pokemon1.getMoves().indexOf(move_id);
        Moves move = Constant.movesData.get(move_id);
        boolean isWild = pokemon2.isWild();
        boolean didHit = didHit(pokemon1_id, pokemon1_level, move_id, pokemon2_id, pokemon2_level, pokemon1.getStatsStages(), pokemon2.getStatsStages());
        if (!didHit) {
            return new MoveResponse(pokemon1, pokemon2, false, new BattleMessages(false, false, false, false, move.getName()), new BattleMessages());
        }

        float base = 1.0f * Constant.movesData.get(move_id).getPower();
        int damage;
        boolean didCrit = false;
        boolean superEffective = false;
        boolean notEffective = false;
        if (base == 0.0f) {
            damage = 0;
        } else {
            float attack;
            float defense;
            if (isSpecialAttack(move_id)) {
                attack = BattleUtil.getCurrentStat("special-attack", pokemon1_id, pokemon1_level, pokemon1.getStatsStages());
                defense = BattleUtil.getCurrentStat("special-defense", pokemon2_id, pokemon2_level, pokemon2.getStatsStages());
            } else {
                attack = BattleUtil.getCurrentStat("attack", pokemon1_id, pokemon1_level, pokemon1.getStatsStages());
                defense = BattleUtil.getCurrentStat("defense", pokemon2_id, pokemon2_level, pokemon2.getStatsStages());
            }
            float tmp = ((2.0f * pokemon1_level) + 10.0f) / 250.0f;


            List<Integer> pokemon1_types = Constant.pokemonData.get(pokemon1_id).getTypeIds();
            List<Integer> pokemon2_types = Constant.pokemonData.get(pokemon2_id).getTypeIds();
            int move_type = move.getTypeId();
            boolean isStab;
            float stab = 1.0f;
            float type = 1.0f;
            float crit = 1.0f;
            float other = 1.0f;
            float randomVar = generateBattleRandomNumber();

            if (pokemon1_types.contains(move_type)) {
                stab *= 1.5f;
            }
            Log.e("movehandler", Constant.typesData.get(move_type).getTargetTypeIdToDamageFactor().toString());
            for (Integer ele : pokemon2_types) {
                type *= (1.0f * (Constant.typesData.get(move_type).getTargetTypeIdToDamageFactor().get(ele)) / 100.0f);
            }
            superEffective = type > 1.0f ? true : false;
            notEffective = type < 1.0f ? true : false;
            if (didCrit(move.getCritRate())) {
                crit *= 1.5f;
                didCrit = true;
            }

            damage = (int) (((tmp * attack * base / defense) + 2.0f) * stab * type * crit * other * randomVar);
        }
        if (!move.getStatIdToStatDifference().isEmpty()) {
            int stat_chance = move.getStatChance();
            if (stat_chance ==  0) {
                pokemon1 = applyMoveStatChanges(move_id, pokemon1);
            } else {
                pokemon2 = applyMoveStatChanges(move_id, pokemon2);
            }
        }
        pokemon2.setHealth(pokemon2.getHealth()-damage);
        Log.e("movehandler", "old_pp: " + pokemon1.getPps().get(move_index));
        // for some reason im pretty sure i implemented this before but i cant find it.. sry if im decrementing pps twice
        int new_pp = pokemon1.getPps().get(move_index)-1;
        pokemon1.getPps().set(move_index, new_pp);
        Log.e("movehandler", "new_pp: " + pokemon1.getPps().get(move_index));
        Log.e("movehandler", "did dmg: " + damage);
        return new MoveResponse(pokemon1, pokemon2, false, new BattleMessages(didHit, didCrit, superEffective, notEffective, move.getName()), new BattleMessages());
//        return new BattleResponse(damage, didCrit, superEffective, notEffective, false, xpGained);
    }

    private static BattlingPokemon applyMoveStatChanges(int move_id, BattlingPokemon pokemon){
        Moves move = Constant.movesData.get(move_id);
        for (Map.Entry<Integer, Integer> ele : move.getStatIdToStatDifference().entrySet()) {
            Map<Integer, Integer> currentStatStages = pokemon.getStatsStages();
            int currentVal = currentStatStages.get(ele.getKey());
            currentStatStages.put(ele.getKey(), currentVal+ele.getValue());
            pokemon.setStatsStages(currentStatStages);
        }
        return pokemon;
    }

    private static int determineAIMove(List<Integer> moves) {
        moves.removeAll(Collections.singleton(null));
        return (moves.get((int)(Math.random()*(moves.size()))));
    }

    // prob = a_base * (accuracy/evasion)
    private static boolean didHit(int pokemon1_id, int pokemon1_level, int move_id, int pokemon2_id, int pokemon2_level, Map<Integer, Integer> pokemon1_stat_stages, Map<Integer, Integer> pokemon2_stat_stages) {
        float a_base = Constant.movesData.get(move_id).getAccuracy()/100.0f;
        float accuracy = BattleUtil.getCurrentStat("accuracy", pokemon1_id, pokemon1_level, pokemon1_stat_stages);
        float evasion = BattleUtil.getCurrentStat("evasion", pokemon2_id, pokemon2_level, pokemon2_stat_stages);

        return BattleUtil.didRandomEvent(a_base*accuracy/evasion);
    }

    private static float generateBattleRandomNumber() {
        return new Double(0.85+(Math.random()*0.15)).floatValue();
    }

    private static boolean didCrit(int critChance) {
        return BattleUtil.didRandomEvent(Constant.criticalChanceMap.get(critChance));
    }

    public static boolean isSpecialAttack(int move_id) {
        return (Constant.movesData.get(move_id).getDamageClassId() == Constant.damageClassesData.get("special").getDamageClassId());
    }
}
