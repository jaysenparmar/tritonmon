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
    private float damage;
    private boolean didCrit;
    private boolean superEffective;
    private boolean notEffective;
    private boolean caughtPokemon;
}
