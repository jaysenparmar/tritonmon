package com.tritonmon.Battle;

import com.tritonmon.model.BattlingPokemon;

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
    private BattlingPokemon pokemon2;
    private BattlingPokemon pokemon1Initial;

    private List<Integer> movesThatCanBeLearned;
    private boolean evolved;
}
