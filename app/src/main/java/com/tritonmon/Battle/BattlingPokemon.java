package com.tritonmon.Battle;

import java.util.List;
import java.util.Map;

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
public class BattlingPokemon {

    private int pokemonId;
    private int pokemonLevel;
    private int xp;
    private boolean wild;

    // init all values to 0, keys from 1-8
    private Map<Integer, Integer> statsStages;

    private int hp;
    private int attack;
    private int defense;
    private int specialAttack;
    private int specialDefense;
    private int speed;
    private int accuracy;
    private int evasion;

    private List<Integer> moves;
    private List<Integer> pps;

    private int moveUsed;

    // possibly change to int
    private String status;
    private int statusTurn;
}
