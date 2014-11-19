package com.tritonmon.Battle;

import com.tritonmon.model.BattlingPokemon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(suppressConstructorProperties = true)
public class AttackResponse {
    private BattlingPokemon attackingPokemon;
    private BattlingPokemon defendingPokemon;
    private boolean humanMovedFirst;
    private BattleMessages battleMessages;
}
