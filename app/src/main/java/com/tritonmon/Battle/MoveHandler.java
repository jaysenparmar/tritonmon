package com.tritonmon.Battle;

import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.staticmodel.Moves;
import com.tritonmon.staticmodel.Stats;

import java.util.ArrayList;
import java.util.Arrays;
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

        // anu should be handling this
//        if (!stillHavePP(pokemon1.getPps().get(moveIdIndex))) {
//            return new MoveResponse();
//        }

        int AI_move_id = determineAIMove(pokemon2.getMoves());

        // determine move order
        int pokemon1_priority = Constant.movesData.get(moveRequest.getMoveId()).getPriority();
        int pokemon2_priority = Constant.movesData.get(AI_move_id).getPriority();

        // pokemon1 attacks first
        if (pokemon1_priority > pokemon2_priority) {
            return humanMovesFirst(moveRequest, AI_move_id);

        } else if (pokemon1_priority == pokemon2_priority) {
            int pokemon1_speed = BattleUtil.getCurrentStat(Stats.SPEED, pokemon1.getPokemonId(), pokemon1.getLevel(), pokemon1.getStatsStages());
            int pokemon2_speed = BattleUtil.getCurrentStat(Stats.SPEED, pokemon2.getPokemonId(), pokemon2.getLevel(), pokemon2.getStatsStages());

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

    public static MoveResponse throwPokeball(MoveRequest moveRequest) {
        BattlingPokemon pokemon1 = moveRequest.getPokemon1();
        BattlingPokemon pokemon2 = moveRequest.getPokemon2();

        // this better be true lol
        boolean isWild = pokemon2.isWild();

        PokeballResponse pokeballResponse = PokeballHandler.didCatchPokemon(
                new PokeballRequest(pokemon2.getPokemonId(), pokemon2.getLevel(), pokemon2.getHealth(), pokemon2.getStatus()));

        if (pokeballResponse.isCaughtPokemon()) {
            List<String> battleMessages1 = Arrays.asList(BattleMessages.CAUGHT_POKEMON);
            return new MoveResponse(pokemon1, pokemon2, false, new BattleMessages(battleMessages1, null, null), new BattleMessages(), true);
        } else {
            int AI_move_id = determineAIMove(pokemon2.getMoves());
            return threwPokeballFirst(moveRequest, AI_move_id);
        }
    }

    private static MoveResponse humanMovesFirst(MoveRequest moveRequest, int AI_move_id) {
        MoveResponse firstMoveResponse = doAttack(moveRequest);
        BattleMessages battleMessages1 = firstMoveResponse.getBattleMessages1();
        MoveRequest secondMoveRequest = new MoveRequest(firstMoveResponse.getPokemon2(), firstMoveResponse.getPokemon1(), AI_move_id);
        MoveResponse secondMoveResponse = doAttack(secondMoveRequest);
        BattleMessages battleMessages2 = secondMoveResponse.getBattleMessages1();

        String tmp = battleMessages1.getStatChanges();
        battleMessages1.setStatChanges(battleMessages2.getStatChanges());
        battleMessages2.setStatChanges(tmp);
        return new MoveResponse(secondMoveResponse.getPokemon2(), secondMoveResponse.getPokemon1(), true, battleMessages1, battleMessages2, false);
    }

    private static MoveResponse AIMovesFirst(MoveRequest moveRequest, int AI_move_id) {
        MoveRequest firstMoveRequest = new MoveRequest(moveRequest.getPokemon2(), moveRequest.getPokemon1(), AI_move_id);
        MoveResponse firstMoveResponse = doAttack(firstMoveRequest);
        BattleMessages battleMessages1 = firstMoveResponse.getBattleMessages1();
        MoveRequest secondMoveRequest = new MoveRequest(firstMoveResponse.getPokemon2(), firstMoveResponse.getPokemon1(), moveRequest.getMoveId());
        MoveResponse secondMoveResponse = doAttack(secondMoveRequest);
        BattleMessages battleMessages2 = secondMoveResponse.getBattleMessages1();

        String tmp = battleMessages1.getStatChanges();
        battleMessages1.setStatChanges(battleMessages2.getStatChanges());
        battleMessages2.setStatChanges(tmp);
        return new MoveResponse(secondMoveResponse.getPokemon1(), secondMoveResponse.getPokemon2(), false, battleMessages1, battleMessages2, false);
    }

    private static MoveResponse threwPokeballFirst(MoveRequest moveRequest, int AI_move_id) {
        List<String> battleMessages1 = Arrays.asList(BattleMessages.DID_NOT_CATCH_POKEMON);
        MoveRequest secondMoveRequest = new MoveRequest(moveRequest.getPokemon2(), moveRequest.getPokemon1(), AI_move_id);
        MoveResponse secondMoveResponse = doAttack(secondMoveRequest);
        BattleMessages battleMessages2 = secondMoveResponse.getBattleMessages1();
        battleMessages2.setStatChanges("");
        return new MoveResponse(secondMoveResponse.getPokemon2(), secondMoveResponse.getPokemon1(), true, new BattleMessages(battleMessages1, null, null), battleMessages2, false);
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
            List<String> battleMessages = Arrays.asList(BattleMessages.MISSED);
            return new MoveResponse(pokemon1, pokemon2, false, new BattleMessages(battleMessages, null, move.getName()), new BattleMessages(), false);
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
                attack = BattleUtil.getCurrentStat(Stats.SPECIAL_ATTACK, pokemon1_id, pokemon1_level, pokemon1.getStatsStages());
                defense = BattleUtil.getCurrentStat(Stats.SPECIAL_DEFENSE, pokemon2_id, pokemon2_level, pokemon2.getStatsStages());
            } else {
                attack = BattleUtil.getCurrentStat(Stats.ATTACK, pokemon1_id, pokemon1_level, pokemon1.getStatsStages());
                defense = BattleUtil.getCurrentStat(Stats.DEFENSE, pokemon2_id, pokemon2_level, pokemon2.getStatsStages());
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
        String statChanges = "";
        List<String> battleMessages = new ArrayList<String>();
        if (!move.getStatIdToStatDifference().isEmpty()) {
            int stat_chance = move.getStatChance();
            if (stat_chance ==  0) {
                for (Map.Entry<Integer, Integer> ele : move.getStatIdToStatDifference().entrySet()) {
                    String tmp = BattleMessages.YOUR;
                    //String tmp = ele.getValue() > 0 ? BattleMessages.SELF : BattleMessages.OPPONENT;
                    tmp+=" " + Stats.getName(ele.getKey()) + " ";
                    tmp+= ele.getValue() > 0 ? BattleMessages.ROSE : BattleMessages.FELL;
                    statChanges = tmp;
                    Map<Integer, Integer> currentStatStages = pokemon1.getStatsStages();
                    int currentVal = currentStatStages.get(ele.getKey());
                    currentStatStages.put(ele.getKey(), currentVal+ele.getValue());
                    pokemon1.setStatsStages(currentStatStages);
                }

            } else {
                for (Map.Entry<Integer, Integer> ele : move.getStatIdToStatDifference().entrySet()) {
                    String tmp = BattleMessages.YOUR;
                    //String tmp = ele.getValue() > 0 ? BattleMessages.SELF : BattleMessages.OPPONENT;
                    tmp+=" " + Stats.getName(ele.getKey()) + " ";
                    tmp+= ele.getValue() > 0 ? BattleMessages.ROSE : BattleMessages.FELL;
                    statChanges = tmp;
                    Map<Integer, Integer> currentStatStages = pokemon2.getStatsStages();
                    int currentVal = currentStatStages.get(ele.getKey());
                    currentStatStages.put(ele.getKey(), currentVal+ele.getValue());
                    pokemon2.setStatsStages(currentStatStages);
                }
            }
        }
        pokemon2.setHealth(pokemon2.getHealth()-damage);
        //Log.e("movehandler", "old_pp: " + pokemon1.getPps().get(move_index));
        // for some reason im pretty sure i implemented this before but i cant find it.. sry if im decrementing pps twice
        int new_pp = pokemon1.getPps().get(move_index)-1;
        pokemon1.getPps().set(move_index, new_pp);
        //Log.e("movehandler", "new_pp: " + pokemon1.getPps().get(move_index));
        Log.e("movehandler", "did dmg: " + damage);


        // if here, mustve hit
        if (didCrit) {
            battleMessages.add(BattleMessages.CRIT);
        }
        if (superEffective) {
            battleMessages.add(BattleMessages.SUPER_EFFECTIVE);
        }
        if (notEffective) {
            battleMessages.add(BattleMessages.NOT_EFFECTIVE);
        }
        return new MoveResponse(pokemon1, pokemon2, false, new BattleMessages(battleMessages, statChanges, move.getName()), new BattleMessages(), false);
//        return new BattleResponse(damage, didCrit, superEffective, notEffective, false, xpGained);
    }



    private static int determineAIMove(List<Integer> moves) {
        moves.removeAll(Collections.singleton(null));
        return (moves.get((int)(Math.random()*(moves.size()))));
    }

    // prob = a_base * (accuracy/evasion)
    private static boolean didHit(int pokemon1_id, int pokemon1_level, int move_id, int pokemon2_id, int pokemon2_level, Map<Integer, Integer> pokemon1_stat_stages, Map<Integer, Integer> pokemon2_stat_stages) {
        float a_base = Constant.movesData.get(move_id).getAccuracy()/100.0f;
        float accuracy = BattleUtil.getCurrentStat(Stats.ACCURACY, pokemon1_id, pokemon1_level, pokemon1_stat_stages);
        float evasion = BattleUtil.getCurrentStat(Stats.EVASION, pokemon2_id, pokemon2_level, pokemon2_stat_stages);

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
