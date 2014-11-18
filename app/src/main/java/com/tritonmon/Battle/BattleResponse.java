package com.tritonmon.Battle;

import java.util.List;

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

    // maybe should be user pokemon?
    private BattlingPokemon pokemon2;

    private boolean caughtPokemon;

    private BattlingPokemon pokemon1Initial;

    private List<Integer> movesThatCanBeLearned;

}
