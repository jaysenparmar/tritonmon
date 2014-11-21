package com.tritonmon.battle;


import com.tritonmon.battle.handler.MoveHandler;
import com.tritonmon.battle.handler.XPHandler;
import com.tritonmon.battle.requestresponse.BattleResponse;
import com.tritonmon.battle.requestresponse.CatchResponse;
import com.tritonmon.battle.requestresponse.MoveRequest;
import com.tritonmon.battle.requestresponse.MoveResponse;
import com.tritonmon.global.Constant;
import com.tritonmon.model.BattlingPokemon;
import com.tritonmon.staticmodel.Moves;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor(suppressConstructorProperties = true)
public class PokemonBattle {

    // to reset all statuses
    private BattlingPokemon pokemon1Initial;
    private BattlingPokemon pokemon2Initial;

    private BattlingPokemon pokemon1;
    private BattlingPokemon pokemon2;

    int numPokeballs;

    public PokemonBattle(BattlingPokemon pokemon1, BattlingPokemon pokemon2, int numPokeballs) {
        this.pokemon1 = pokemon1;
        this.pokemon2 = pokemon2;

        pokemon1Initial = this.pokemon1;
        pokemon2Initial = this.pokemon2;

        this.numPokeballs = numPokeballs;
    }

    public MoveResponse doMove(int moveId) {
        MoveResponse moveResponse = MoveHandler.doMove(new MoveRequest(pokemon1, pokemon2, moveId));
        pokemon1 = moveResponse.getPokemon1();
        pokemon2 = moveResponse.getPokemon2();
        moveResponse.getBattleMessages1().pruneAll(pokemon1.getName(), pokemon2.getName(), moveResponse.isCaughtPokemon());
        moveResponse.getBattleMessages2().pruneAll(pokemon2.getName(), pokemon1.getName(), moveResponse.isCaughtPokemon());
        moveResponse.getBattleMessages1().populateAllMessages();
        moveResponse.getBattleMessages2().populateAllMessages();
        return moveResponse;
    }

    public MoveResponse throwPokeball() {
        numPokeballs-=1;
        MoveResponse moveResponse = MoveHandler.throwPokeball(new MoveRequest(pokemon1, pokemon2, -1));
        pokemon1 = moveResponse.getPokemon1();
        pokemon2 = moveResponse.getPokemon2();
        moveResponse.getBattleMessages1().pruneAll(pokemon1.getName(), pokemon2.getName(), moveResponse.isCaughtPokemon());
        moveResponse.getBattleMessages2().pruneAll(pokemon2.getName(), pokemon1.getName(), moveResponse.isCaughtPokemon());
        moveResponse.getBattleMessages1().populateAllMessages();
        moveResponse.getBattleMessages2().populateAllMessages();
        return moveResponse;
    }

    // todo: make this shorter if it gets much longer (>~50 lines)
    public BattleResponse endBattle() {
        int xpGained = XPHandler.xpGained(pokemon2.isWild(), pokemon2.getPokemonId(), pokemon2.getLevel());
        int newXP = pokemon1.getXp() + xpGained;
        pokemon1.setXp(newXP);
        int newLevel = XPHandler.newLevel(newXP);
        List<Integer> newMoves = new ArrayList<Integer>();
        int pokemon_id = pokemon1.getPokemonId();
        boolean evolved = false;
        for (int i = pokemon1.getLevel(); i < newLevel+1; i++) {
            pokemon_id = XPHandler.newPokemonEvolution(pokemon_id, i);
            newMoves.addAll(XPHandler.getNewMoves(pokemon_id, i, i));
        }
        if (pokemon_id != pokemon1.getPokemonId()) {
            evolved = true;
        }
        List<Integer> movesLearned = new ArrayList<Integer>();
        if (!newMoves.isEmpty()) {
            if (XPHandler.canLearnMoreMoves(pokemon1.getMoves())) {
                List<Integer> currentMoves = pokemon1.getMoves();
                List<Integer> currentPps = pokemon1.getPps();
                currentMoves.removeAll(Collections.singleton(null));
                currentPps.removeAll(Collections.singleton(null));
                for (Integer ele : newMoves) {
                    if (currentMoves.size() >= 4 || currentPps.size() >= 4) {
                        break;
                    }
                    currentMoves.add(ele);
                    currentPps.add(Moves.getMaxPp(ele));
                    movesLearned.add(ele);
                }
                pokemon1.setMoves(currentMoves);
                pokemon1.setPps(currentPps);
            }
        }
        pokemon1.setPokemonId(pokemon_id);
        pokemon1.setLevel(newLevel);
        pokemon1.clearStatus();
        pokemon2.clearStatus();
        return new BattleResponse(pokemon1, pokemon2, pokemon1Initial, newMoves, evolved, movesLearned, numPokeballs);
    }

    public CatchResponse endBattleWithCatch() {
        pokemon1.clearStatus();
        pokemon2.clearStatus();
        pokemon2.resetPps();
        return new CatchResponse(pokemon1, pokemon2, numPokeballs);
    }


}
