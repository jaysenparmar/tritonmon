package com.tritonmon.battle.handler;

import android.util.Log;

import com.tritonmon.battle.BattleUtil;
import com.tritonmon.battle.requestresponse.AttackRequest;
import com.tritonmon.battle.requestresponse.AttackResponse;
import com.tritonmon.battle.requestresponse.MoveRequest;
import com.tritonmon.battle.requestresponse.MoveResponse;
import com.tritonmon.battle.requestresponse.PokeballRequest;
import com.tritonmon.battle.requestresponse.PokeballResponse;
import com.tritonmon.global.Constant;
import com.tritonmon.model.BattleMessages;
import com.tritonmon.model.BattlingPokemon;
import com.tritonmon.staticmodel.DamageClasses;
import com.tritonmon.staticmodel.MoveMetaAilments;
import com.tritonmon.staticmodel.Moves;
import com.tritonmon.staticmodel.Stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

// TODO: status stuff
public class MoveHandler {

    public static MoveResponse doMove(MoveRequest moveRequest) {
        BattlingPokemon pokemon1 = moveRequest.getPokemon1();
        BattlingPokemon pokemon2 = moveRequest.getPokemon2();

        int aiMoveId = determineAIMove(pokemon2.getMoves());

        // determine move order
        int pokemon1_priority = Constant.movesData.get(moveRequest.getMoveId()).getPriority();
        int pokemon2_priority = Constant.movesData.get(aiMoveId).getPriority();

        // pokemon1 attacks first
        if (pokemon1_priority > pokemon2_priority) {
            return humanMovesFirst(moveRequest, aiMoveId);

        } else if (pokemon1_priority == pokemon2_priority) {
            int pokemon1_speed = BattleUtil.getCurrentStat(Stats.SPEED, pokemon1.getPokemonId(), pokemon1.getLevel(), pokemon1.getStatsStages());
            int pokemon2_speed = BattleUtil.getCurrentStat(Stats.SPEED, pokemon2.getPokemonId(), pokemon2.getLevel(), pokemon2.getStatsStages());

            if (pokemon1.getStatus().equals(MoveMetaAilments.PARALYSIS)) {
                pokemon1_speed*=MoveMetaAilments.PARALYSIS_FACTOR;
            }
            if (pokemon2.getStatus().equals(MoveMetaAilments.PARALYSIS)) {
                pokemon2_speed*=MoveMetaAilments.PARALYSIS_FACTOR;
            }
            // yes i give pref to the player cuz im nice
            // pokemon1 attacks first
            if (pokemon1_speed >= pokemon2_speed) {
                return humanMovesFirst(moveRequest, aiMoveId);
            } else {
                return AIMovesFirst(moveRequest, aiMoveId);
            }
        } else {
            return AIMovesFirst(moveRequest, aiMoveId);
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
            BattleMessages battleMessages1 = new BattleMessages(BattleMessages.THREW_POKBEALL);
            battleMessages1.addCaughtPokemon(BattleMessages.CAUGHT_POKEMON);
            return new MoveResponse(pokemon1, pokemon2, battleMessages1, new BattleMessages(), false, true);
        } else {
            int aiMoveId = determineAIMove(pokemon2.getMoves());
            return missedThrow(moveRequest, aiMoveId);
        }
    }

    private static MoveResponse humanMovesFirst(MoveRequest moveRequest, int aiMoveId) {
        AttackRequest attackRequest1 = new AttackRequest(moveRequest.getPokemon1(), moveRequest.getPokemon2(), moveRequest.getMoveId());
        AttackResponse attackResponse1 = doAttack(attackRequest1);
        BattleMessages battleMessages1 = attackResponse1.getBattleMessages();

        // might need to fix battle messages too (swap stuff around). will test when we have transition screens
        // (cant really test without them)
        if (attackResponse1.getAttackingPokemon().isDead() || attackResponse1.getDefendingPokemon().isDead()) {
            return new MoveResponse(
                attackResponse1.getAttackingPokemon(),
                attackResponse1.getDefendingPokemon(),
                battleMessages1,
                new BattleMessages(),
                true,
                false
            );
        }

        AttackRequest attackRequest2 = new AttackRequest(attackResponse1.getDefendingPokemon(), attackResponse1.getAttackingPokemon(), aiMoveId);
        AttackResponse attackResponse2 = doAttack(attackRequest2);
        BattleMessages battleMessages2 = attackResponse2.getBattleMessages();

        // swap stat changes (i.e. "your accuracy lowered" affects other pokemon)
//        String tempStatChanges = battleMessages1.getStatChanges();
//        battleMessages1.setStatChanges(battleMessages2.getStatChanges());
//        battleMessages2.setStatChanges(tempStatChanges);

        return new MoveResponse(
                attackResponse2.getDefendingPokemon(),
                attackResponse2.getAttackingPokemon(),
                battleMessages1,
                battleMessages2,
                true,
                false);
    }

    private static MoveResponse AIMovesFirst(MoveRequest moveRequest, int aiMoveId) {
        AttackRequest attackRequest1 = new AttackRequest(moveRequest.getPokemon2(), moveRequest.getPokemon1(), aiMoveId);
        AttackResponse attackResponse1 = doAttack(attackRequest1);
        BattleMessages battleMessages2 = attackResponse1.getBattleMessages();

        // might need to fix battle messages too (swap stuff around). will test when we have transition screens
        // (cant really test without them)
        if (attackResponse1.getAttackingPokemon().isDead() || attackResponse1.getDefendingPokemon().isDead()) {
            return new MoveResponse(
                    attackResponse1.getDefendingPokemon(),
                    attackResponse1.getAttackingPokemon(),
                    new BattleMessages(),
                    battleMessages2,
                    false,
                    false
            );
        }

        AttackRequest attackRequest2 = new AttackRequest(attackResponse1.getDefendingPokemon(), attackResponse1.getAttackingPokemon(), moveRequest.getMoveId());
        AttackResponse attackResponse2 = doAttack(attackRequest2);
        BattleMessages battleMessages1 = attackResponse2.getBattleMessages();

//        // swap stat changes (i.e. "your accuracy fell" affects other pokemon)
//        String tempStatChanges = battleMessages1.getStatChanges();
//        battleMessages1.setStatChanges(battleMessages2.getStatChanges());
//        battleMessages2.setStatChanges(tempStatChanges);

        return new MoveResponse(
                attackResponse2.getAttackingPokemon(),
                attackResponse2.getDefendingPokemon(),
                battleMessages1,
                battleMessages2,
                false,
                false);
    }

    private static MoveResponse missedThrow(MoveRequest moveRequest, int AI_move_id) {

        BattleMessages battleMessages1 = new BattleMessages(BattleMessages.THREW_POKBEALL);
        battleMessages1.addCaughtPokemon(BattleMessages.DID_NOT_CATCH_POKEMON);

        AttackRequest attackRequest2 = new AttackRequest(moveRequest.getPokemon2(), moveRequest.getPokemon1(), AI_move_id);
        AttackResponse attackResponse2 = doAttack(attackRequest2);

        BattleMessages battleMessages2 = attackResponse2.getBattleMessages();
        return new MoveResponse(
                attackResponse2.getDefendingPokemon(),
                attackResponse2.getAttackingPokemon(),
                battleMessages1,
                battleMessages2,
                true,
                false);
    }

    //damage = (((((2*poke1_level)+10)/250)*(attack/defence)*base)+2)*modifier
    //modifier = stab*type*critical*other*random(0.85,1)

    /**
     * moveRequest's pokemon1 is the attacking pokemon, moveRequest's pokemon2 is the defending pokemon
     * @param attackRequest
     * @return MoveResponse
     */
    private static AttackResponse doAttack(AttackRequest attackRequest) {

        BattlingPokemon pokemon1 = attackRequest.getAttackingPokemon();
        BattlingPokemon pokemon2 = attackRequest.getDefendingPokemon();
        int pokemon1_id = pokemon1.getPokemonId();
        int pokemon1_level = pokemon1.getLevel();
        int pokemon2_id = pokemon2.getPokemonId();
        int pokemon2_level = pokemon2.getLevel();
        int move_id = attackRequest.getMoveId();
        int move_index = pokemon1.getMoves().indexOf(move_id);
        Moves move = Constant.movesData.get(move_id);
        boolean isWild = pokemon2.isWild();

        BattleMessages battleMessages = new BattleMessages();
        battleMessages.addMoveUsed(move.getName());
        // for some reason im pretty sure i implemented this before but i cant find it.. sry if im decrementing pps twice
        int new_pp = pokemon1.getPps().get(move_index)-1;
        pokemon1.getPps().set(move_index, new_pp);

        battleMessages.addPrelimAilment(AilmentHandler.getPrelimAilmentMessage(pokemon1));
        boolean canHit = AilmentHandler.canHit(pokemon1);
        if (!canHit) {

            if (pokemon1.getStatus() !=  MoveMetaAilments.NONE) {
                String old_status = pokemon1.getStatus();
                pokemon1 = AilmentHandler.continueAilment(pokemon1, move);
                if (old_status.equals(MoveMetaAilments.FREEZE) && pokemon1.getStatus().equals(MoveMetaAilments.NONE)) {
                    battleMessages.removePrelimAilment();
                    battleMessages.addContinueAilment(BattleMessages.UNFROZE);
                } else if (old_status.equals(MoveMetaAilments.SLEEP) && pokemon1.getStatus().equals(MoveMetaAilments.NONE)) {
                    battleMessages.addContinueAilment(BattleMessages.WOKEUP);
                    battleMessages.removePrelimAilment();
                }
            }

            return new AttackResponse(
                    pokemon1,
                    pokemon2,
                    battleMessages,
                    false);
        }

        boolean didHit = didHit(attackRequest);
        if (!didHit) {
            // maybe combine two lower if statements?
            if (pokemon1.getStatus().equals(MoveMetaAilments.BURN) || pokemon1.getStatus().equals(MoveMetaAilments.POISON)) {
                int hurtDamage = AilmentHandler.getHurtDamage(pokemon1);
                pokemon1.setHealth(pokemon1.getHealth()-hurtDamage);
                battleMessages.addContinueAilment(AilmentHandler.getHurtMessage(pokemon1));
            }

            if (pokemon1.getStatus() !=  MoveMetaAilments.NONE) {
                String old_status = pokemon1.getStatus();
                pokemon1 = AilmentHandler.continueAilment(pokemon1, move);
                if (old_status.equals(MoveMetaAilments.FREEZE) && pokemon1.getStatus().equals(MoveMetaAilments.NONE)) {
                    battleMessages.addContinueAilment(BattleMessages.UNFROZE);
                    battleMessages.removePrelimAilment();
                } else if (old_status.equals(MoveMetaAilments.SLEEP) && pokemon1.getStatus().equals(MoveMetaAilments.NONE)) {
                    battleMessages.addContinueAilment(BattleMessages.WOKEUP);
                    battleMessages.removePrelimAilment();
                }
            }

            battleMessages.addEffectiveness(BattleMessages.MISSED);
            return new AttackResponse(
                    pokemon1,
                    pokemon2,
                    battleMessages,
                    false);
        }

        if (pokemon1.getStatus().equals(MoveMetaAilments.CONFUSION)) {
            if (BattleUtil.didRandomEvent(MoveMetaAilments.CONFUSION_PROB)) {
                return AilmentHandler.hitSelf(attackRequest, battleMessages);
            }
        }

        float base = 1.0f * Constant.movesData.get(move_id).getPower();
        int damage;
        if (base == 0.0f) {
            damage = 0;
        } else {
            float attack;
            float defense;
            if (BattleUtil.isSpecialAttack(move_id)) {
                attack = BattleUtil.getCurrentStat(Stats.SPECIAL_ATTACK, pokemon1_id, pokemon1_level, pokemon1.getStatsStages());
                defense = BattleUtil.getCurrentStat(Stats.SPECIAL_DEFENSE, pokemon2_id, pokemon2_level, pokemon2.getStatsStages());
            } else {
                attack = BattleUtil.getCurrentStat(Stats.ATTACK, pokemon1_id, pokemon1_level, pokemon1.getStatsStages());
                defense = BattleUtil.getCurrentStat(Stats.DEFENSE, pokemon2_id, pokemon2_level, pokemon2.getStatsStages());
            }
            if (pokemon1.getStatus().equals(MoveMetaAilments.BURN) && move.getDamageClassId() == Constant.damageClassesData.get(DamageClasses.PHYSICAL))  {
                attack*=MoveMetaAilments.BURN_FACTOR;
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
            float randomVar = BattleUtil.generateBattleRandomNumber();

            if (pokemon1_types.contains(move_type)) {
                stab *= 1.5f;
            }
            //Log.e("movehandler", Constant.typesData.get(move_type).getTargetTypeIdToDamageFactor().toString());
            for (Integer ele : pokemon2_types) {
                type *= (1.0f * (Constant.typesData.get(move_type).getTargetTypeIdToDamageFactor().get(ele)) / 100.0f);
            }
            if (type > 1.0f) {
                battleMessages.addEffectiveness(BattleMessages.SUPER_EFFECTIVE);
            }
            if (type < 1.0f) {
                battleMessages.addEffectiveness(BattleMessages.NOT_EFFECTIVE);
            }

            if (BattleUtil.didCrit(move.getCritRate())) {
                crit *= 1.5f;
                battleMessages.addCrit(BattleMessages.CRIT);
            }

            damage = (int) (((tmp * attack * base / defense) + 2.0f) * stab * type * crit * other * randomVar);
        }
        List<String> statChanges = new ArrayList<String>();

        if (!move.getStatIdToStatDifference().isEmpty()) {
            int stat_chance = move.getStatChance();
            if (stat_chance ==  0) {
                for (Map.Entry<Integer, Integer> ele : move.getStatIdToStatDifference().entrySet()) {
//                    String tmp = BattleMessages.YOUR;
                    //String tmp = ele.getValue() > 0 ? BattleMessages.SELF : BattleMessages.OPPONENT;
                    String tmp = Stats.getName(ele.getKey()) + " ";
                    tmp+= ele.getValue() > 0 ? BattleMessages.ROSE : BattleMessages.FELL;
                    statChanges.add(tmp);
                    Map<Integer, Integer> currentStatStages = pokemon1.getStatsStages();
                    int currentVal = currentStatStages.get(ele.getKey());
                    int newVal = currentVal+ele.getValue();
                    newVal = newVal > 6 ? 6 : newVal;
                    newVal = newVal < -6 ? -6 : newVal;
                    currentStatStages.put(ele.getKey(), newVal);
                    pokemon1.setStatsStages(currentStatStages);
                }

            } else {
                if (BattleUtil.didRandomEvent(((float)stat_chance)/100.0f)) {
                    for (Map.Entry<Integer, Integer> ele : move.getStatIdToStatDifference().entrySet()) {
                        //                    String tmp = BattleMessages.YOUR;
                        //String tmp = ele.getValue() > 0 ? BattleMessages.SELF : BattleMessages.OPPONENT;
                        String tmp = Stats.getName(ele.getKey()) + " ";
                        tmp += ele.getValue() > 0 ? BattleMessages.ROSE : BattleMessages.FELL;
                        statChanges.add(tmp);
                        Map<Integer, Integer> currentStatStages = pokemon2.getStatsStages();
                        int currentVal = currentStatStages.get(ele.getKey());
                        int newVal = currentVal + ele.getValue();
                        newVal = (newVal > 6) ? 6 : newVal;
                        newVal = (newVal < -6) ? -6 : newVal;
                        currentStatStages.put(ele.getKey(), newVal);
                        pokemon2.setStatsStages(currentStatStages);
                    }
                }
            }
        }
        battleMessages.addStatChanges(statChanges);
        battleMessages.addDamageDone(Integer.toString(damage), false);
        pokemon2.setHealth(pokemon2.getHealth()-damage);
        //Log.e("movehandler", "old_pp: " + pokemon1.getPps().get(move_index));

        //Log.e("movehandler", "new_pp: " + pokemon1.getPps().get(move_index));
        //Log.e("movehandler", "did dmg: " + damage);

        // if here, mustve hit

        // maybe combine two lower if statements?
        if (pokemon1.getStatus().equals(MoveMetaAilments.BURN) || pokemon1.getStatus().equals(MoveMetaAilments.POISON)) {
            int hurtDamage = AilmentHandler.getHurtDamage(pokemon1);
            pokemon1.setHealth(pokemon1.getHealth()-hurtDamage);
            battleMessages.addContinueAilment(AilmentHandler.getHurtMessage(pokemon1));
        }

        if (pokemon1.getStatus() !=  MoveMetaAilments.NONE) {
            String old_status = pokemon1.getStatus();
            pokemon1 = AilmentHandler.continueAilment(pokemon1, move);
            if (old_status.equals(MoveMetaAilments.FREEZE) && pokemon1.getStatus().equals(MoveMetaAilments.NONE)) {
                battleMessages.addContinueAilment(BattleMessages.UNFROZE);
                battleMessages.removePrelimAilment();
            } else if (old_status.equals(MoveMetaAilments.SLEEP) && pokemon1.getStatus().equals(MoveMetaAilments.NONE)) {
                battleMessages.addContinueAilment(BattleMessages.WOKEUP);
                battleMessages.removePrelimAilment();
            }
        }

        if (move.getMoveMetaAilmentId() != Constant.moveMetaAilmentsData.get(MoveMetaAilments.NONE).getMoveMetaAilmentId()) {
            pokemon2 = AilmentHandler.afflictAilment(pokemon2, move);
            if (pokemon2.getStatus() != MoveMetaAilments.NONE) {
//                Log.e("added afflicted ailment", pokemon2.getStatus());
                battleMessages.addAfflictedAilment(AilmentHandler.getAfflictAilmentMessage(MoveMetaAilments.getName(move.getMoveMetaAilmentId())));

            }
        }

        if (pokemon1.getHealth() < 0) {
            pokemon1.setHealth(0);
        }

        if (pokemon2.getHealth() < 0) {
            pokemon2.setHealth(0);
        }

        return new AttackResponse(
                pokemon1,
                pokemon2,
                battleMessages,
                false);
    }

    private static int determineAIMove(List<Integer> moves) {
        moves.removeAll(Collections.singleton(null));
        return (moves.get((int)(Math.random()*(moves.size()))));
    }

    // prob = a_base * (accuracy/evasion)
    private static boolean didHit(AttackRequest attackRequest) {
//    private static boolean didHit(int pokemon1_id, int pokemon1_level, int move_id, int pokemon2_id, int pokemon2_level, Map<Integer, Integer> pokemon1_stat_stages, Map<Integer, Integer> pokemon2_stat_stages) {

        float a_base = Constant.movesData.get(attackRequest.getMoveId()).getAccuracy()/100.0f;
        float accuracy = BattleUtil.getCurrentStat(
                Stats.ACCURACY,
                attackRequest.getAttackingPokemon().getPokemonId(),
                attackRequest.getAttackingPokemon().getLevel(),
                attackRequest.getAttackingPokemon().getStatsStages());
        float evasion = BattleUtil.getCurrentStat(
                Stats.EVASION,
                attackRequest.getDefendingPokemon().getPokemonId(),
                attackRequest.getDefendingPokemon().getLevel(),
                attackRequest.getDefendingPokemon().getStatsStages());

        return BattleUtil.didRandomEvent(a_base*accuracy/evasion);
    }

}
