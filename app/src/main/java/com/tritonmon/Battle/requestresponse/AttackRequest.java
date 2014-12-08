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
public class AttackRequest {
    private BattlingPokemon attackingPokemon;
    private BattlingPokemon defendingPokemon;
    private int moveId;

    // for flinching, is -1 if is first move
    private int prevMoveId;
}
