package com.tritonmon.Battle;

import com.tritonmon.global.Constant;
import com.tritonmon.staticmodel.MoveMetaAilments;
import com.tritonmon.staticmodel.Moves;
import com.tritonmon.staticmodel.Stats;

import java.util.Arrays;

public class AilmentHandler {

    public static boolean canHit(BattlingPokemon pokemon1) {

        String status = pokemon1.getStatus();
        // 25% chance cant move
        if (status.equals(MoveMetaAilments.PARALYSIS)) {
            return BattleUtil.didRandomEvent(0.25f);
        }
        if (status.equals(MoveMetaAilments.SLEEP) || status.equals(MoveMetaAilments.FREEZE)) {
            return false;
        }
        return true;
    }

    public static String getAilmentMessage(BattlingPokemon pokemon1) {
        String status = pokemon1.getStatus();

        if (status.equals(MoveMetaAilments.PARALYSIS)) {
            return BattleMessages.PARALYZED;
        }
        if (status.equals(MoveMetaAilments.SLEEP)) {
            return BattleMessages.ASLEEP;
        }
        if (status.equals(MoveMetaAilments.FREEZE)) {
            return BattleMessages.FROZEN;
        }
        if (status.equals(MoveMetaAilments.BURN)) {
            return BattleMessages.BURNED;
        }
        if (status.equals(MoveMetaAilments.POISON)) {
            return BattleMessages.POISONED;
        }
        // technically should never reach here
        return BattleMessages.EMPTY_AILMENT;
    }

//    [(((2A/5 + 2)*B*40)/C)/50] + 2
//    A = the confusion victim's Level
//    B = the confusion victim's Attack
//    C = the confusion victim's Defense
    public static MoveResponse hitSelf(MoveRequest moveRequest) {
        BattlingPokemon pokemon1 = moveRequest.getPokemon1();
        int level = pokemon1.getLevel();
        int attack = BattleUtil.getCurrentStat(Stats.HP, pokemon1.getPokemonId(), level, pokemon1.getStatsStages());
        int defense = BattleUtil.getCurrentStat(Stats.HP, pokemon1.getPokemonId(), level, pokemon1.getStatsStages());
        int damage = (int)((((2.0f * level / 5.0f) + 2.0f) * attack * 40.0f / defense) / 50.0f) + 2;
        pokemon1.setHealth(pokemon1.getHealth()-damage);
        String moveName = Constant.movesData.get(moveRequest.getMoveId()).getName();
        return new MoveResponse(
                pokemon1,
                moveRequest.getPokemon2(),
                new BattleMessages(Arrays.asList(BattleMessages.HIT_SELF), BattleMessages.EMPTY_STAT_CHANGES, moveName, BattleMessages.CONFUSED),
                new BattleMessages(),
                false,
                false);
    }

    // i might need a better name for this method: applies poison/burn dmg
    public static int getHurtDamage(BattlingPokemon pokemon1) {
        return (int)(BattleUtil.getMaxStat(Stats.HP, pokemon1.getPokemonId(), pokemon1.getLevel())*MoveMetaAilments.HURT_FACTOR);
    }

    public static BattlingPokemon continueAilment(BattlingPokemon pokemon1, Moves move) {
        if (pokemon1.getMaxTurns() > 0) {
            //Log.e("AilmentHandler", "ailment: " + pokemon1.getStatus());
            if (pokemon1.getStatus().equals(MoveMetaAilments.FREEZE)) {
                if (BattleUtil.didRandomEvent(MoveMetaAilments.UNFREEZE_PROB)) {
                    pokemon1.clearStatus();
                    return pokemon1;
                }
            }
            String status = pokemon1.getStatus();
            pokemon1.setStatusTurn(pokemon1.getStatusTurn() + 1);
            if (pokemon1.getStatusTurn() >= pokemon1.getMaxTurns()) {
                pokemon1.clearStatus();
            }
        }
        return pokemon1;
    }

    public static BattlingPokemon afflictAilment(BattlingPokemon pokemon2, Moves move) {
        if (BattleUtil.didRandomEvent((float)move.getAilmentChance()/100.0f)) {
            //Log.e("AilmentHandler", "afflicted: " + move.getName());
            pokemon2.setStatus(MoveMetaAilments.getName(move.getMoveMetaAilmentId()));
            pokemon2.setStatusTurn(0);
            pokemon2.setMaxTurns(BattleUtil.chooseRandomNumberBetween(move.getMinHits(), move.getMaxHits()));
        }
        return pokemon2;
    }
}
