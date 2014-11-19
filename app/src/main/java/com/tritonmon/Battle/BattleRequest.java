package com.tritonmon.Battle;

import com.tritonmon.model.BattlingPokemon;

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
public class BattleRequest {
    private BattlingPokemon pokemon1;
    // if wild, init pokemon2 with only id,level, and isWild=true
    // else init all fields for me pls
    private BattlingPokemon pokemon2;


}
