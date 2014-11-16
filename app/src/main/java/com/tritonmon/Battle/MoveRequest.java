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
public class MoveRequest {
    private BattlingPokemon pokemon1;
    private BattlingPokemon pokemon2;
    private int moveId;
}
