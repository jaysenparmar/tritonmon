package com.tritonmon.Battle;

import com.tritonmon.model.UsersPokemon;

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
@ToString
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class BattlingPokemon extends UsersPokemon {

    private boolean wild;

    // init all values to 0, keys from 1-8
    private Map<Integer, Integer> statsStages;

//    private int hp;
    private int attack;
    private int defense;
    private int specialAttack;
    private int specialDefense;
    private int speed;
    private int accuracy;
    private int evasion;

    // possibly change to int
    private String status;
    private int statusTurn;

    public BattlingPokemon(int pokemonId, int level, int xp, boolean wild, Map<Integer, Integer> statStages, int hp, int attack, int defense,
                           int specialAttack, int specialDefense, int speed, int accuracy, int evasion,
                           List<Integer> moves, List<Integer> pps, String status, int statusTurn) {
        this.pokemonId = pokemonId;
        this.level = level;
        this.xp = xp;
        this.moves  = moves;
        this.pps = pps;

        this.wild = wild;
        this.statsStages = statStages;
        this.health = hp;
        this.attack = attack;
        this.defense = defense;
        this.specialAttack = specialAttack;
        this.specialDefense = specialDefense;
        this.speed = speed;
        this.accuracy = accuracy;
        this.evasion = evasion;
        this.status = status;
        this.statusTurn = statusTurn;
    }

    public BattlingPokemon(UsersPokemon usersPokemon, boolean wild, Map<Integer, Integer> statStages, int attack, int defense,
                           int specialAttack, int specialDefense, int speed, int accuracy, int evasion,
                           String status, int statusTurn) {
        this.usersPokemonId = usersPokemon.getUsersPokemonId();
        this.username = usersPokemon.getUsername();
        this.pokemonId = usersPokemon.getPokemonId();
        this.slotNum = usersPokemon.getSlotNum();
        this.nickname = usersPokemon.getNickname();
        this.level = usersPokemon.getLevel();
        this.xp = usersPokemon.getXp();
        this.moves  = usersPokemon.getMoves();
        this.pps = usersPokemon.getPps();

        this.wild = wild;
        this.statsStages = statStages;
        this.health = usersPokemon.getHealth();
        this.attack = attack;
        this.defense = defense;
        this.specialAttack = specialAttack;
        this.specialDefense = specialDefense;
        this.speed = speed;
        this.accuracy = accuracy;
        this.evasion = evasion;
        this.status = status;
        this.statusTurn = statusTurn;
    }
}
