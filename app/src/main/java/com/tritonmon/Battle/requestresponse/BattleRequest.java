package com.tritonmon.battle.requestresponse;

import com.tritonmon.model.BattlingPokemon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor(suppressConstructorProperties = true)
public class BattleRequest {
    private BattlingPokemon pokemon1;
    // if wild, init pokemon2 with only id,level, and isWild=true
    // else init all fields for me pls
    private BattlingPokemon pokemon2;


}
