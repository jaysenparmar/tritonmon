package com.tritonmon.Battle;


import com.tritonmon.model.BattlingPokemon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor(suppressConstructorProperties = true)
public class MoveRequest {
    private BattlingPokemon pokemon1;
    private BattlingPokemon pokemon2;
    private int moveId;
}
