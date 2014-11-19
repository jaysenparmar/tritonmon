package com.tritonmon.battle.requestresponse;

import com.tritonmon.model.BattleMessages;
import com.tritonmon.model.BattlingPokemon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor(suppressConstructorProperties = true)
public class AttackResponse {
    private BattlingPokemon attackingPokemon;
    private BattlingPokemon defendingPokemon;
    private BattleMessages battleMessages;
    private boolean humanMovedFirst;
}
