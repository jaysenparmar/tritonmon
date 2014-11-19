package com.tritonmon.Battle;

import com.tritonmon.model.BattlingPokemon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class MoveResponse {
    private BattlingPokemon pokemon1;
    private BattlingPokemon pokemon2;

    private BattleMessages battleMessages1;
    private BattleMessages battleMessages2;

    private boolean humanMovedFirst;
    private boolean caughtPokemon;
}
