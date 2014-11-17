package com.tritonmon.Battle;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
//@NoArgsConstructor
//@AllArgsConstructor(suppressConstructorProperties = true)
public class PokemonBattle {

    // to reset all statuses
    private BattlingPokemon pokemon1_copy;
    private BattlingPokemon pokemon2_copy;

    private BattlingPokemon pokemon1;
    private BattlingPokemon pokemon2;

    public PokemonBattle(BattleRequest battleRequest) {
        pokemon1 = battleRequest.getPokemon1();
        pokemon2 = battleRequest.getPokemon2();

        pokemon1_copy = pokemon1;
        pokemon2_copy = pokemon2;
    }

    public MoveResponse doMove(int move_id) {
        MoveResponse moveResponse = MoveHandler.doMove(new MoveRequest(pokemon1, pokemon2, move_id));
        pokemon1 = moveResponse.getPokemon1();
        pokemon2 = moveResponse.getPokemon2();
        return moveResponse;
    }

    public PokeballResponse doThrowPokeball() {
        return PokeballHandler.didCatchPokemon(new PokeballRequest(pokemon2.getPokemonId(), pokemon2.getLevel(), pokemon2.getHealth(), pokemon2.getStatus()));
    }

    // called if moveresponse = null im hoping (signifies end of battle idk how else to do it
//    public BattleResponse endBattle() {
//          int xpGained = XpHandler.xpGained(isWild, pokemon2_id, pokemon2_level);
//    }

}
