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
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class BattleResponse {
    private BattlingPokemon pokemon1;
    private boolean caughtPokemon;

    // maybe should be user pokemon?
    private BattlingPokemon pokemon2;
}
