package com.tritonmon.Battle;

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
}
