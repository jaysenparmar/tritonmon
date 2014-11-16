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
public class MoveResponse {
    private BattlingPokemon pokemon1;
    private BattlingPokemon pokemon2;

    // for person who moved first
    private BattleMessages battleMessages1;
    // second
    private BattleMessages battleMessages2;
}
