package com.tritonmon.Battle;

import com.tritonmon.model.UsersPokemon;
import com.tritonmon.staticmodel.MoveMetaAilments;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
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
    private int maxTurns;

    public BattlingPokemon(int pokemonId, int level, int xp, boolean wild, Map<Integer, Integer> statStages, int hp, int attack, int defense,
                           int specialAttack, int specialDefense, int speed, int accuracy, int evasion,
                           List<Integer> moves, List<Integer> pps, String status, int statusTurn, int maxTurns) {
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
        this.maxTurns = maxTurns;
    }

    public BattlingPokemon(int pokemonId, int level, boolean wild) {
        this.pokemonId = pokemonId;
        this.level = level;
        this.wild = wild;

        // shouldnt be a case where this goes to the else.. or something is wrong
        if (wild) {
            Map<String, Integer> allStats = BattleUtil.getAllMaxStats(pokemonId, level);
//        return new BattlingPokemon(pokemon2_id, pokemon2_level, -1, true,)
            List<Integer> pps = Arrays.asList(999, 999, 999, 999);
            Map<Integer, Integer> statStages = new HashMap<Integer, Integer>();
            for (int i = 1; i < 9; i++) {
                statStages.put(i, 0);
            }

            this.moves = BattleUtil.generateMoves(XPHandler.getNewMovesWithLevel(pokemonId, 0, level));
            this.pps = pps;
            this.xp = -1;

            this.statsStages = statStages;
            this.health = allStats.get("hp");
            this.attack = allStats.get("attack");
            this.defense = allStats.get("defense");
            this.specialAttack = allStats.get("special-attack");
            this.specialDefense = allStats.get("special-defense");
            this.speed = allStats.get("speed");
            this.accuracy = allStats.get("accuracy");
            this.evasion = allStats.get("evasion");
            this.status = "none";
            this.statusTurn = 0;
            this.maxTurns = 0;

        }

    }

    // not sure if need this one. will figure out when implementing swapping
    public BattlingPokemon(UsersPokemon usersPokemon) {
        this.usersPokemonId = usersPokemon.getUsersPokemonId();
        this.username = usersPokemon.getUsername();
        this.pokemonId = usersPokemon.getPokemonId();
        this.slotNum = usersPokemon.getSlotNum();
        this.nickname = usersPokemon.getNickname();
        this.level = usersPokemon.getLevel();
        this.xp = usersPokemon.getXp();
        this.moves  = usersPokemon.getMoves();
        this.pps = usersPokemon.getPps();

        this.wild = false;

        Map<String, Integer> allStats = BattleUtil.getAllMaxStats(usersPokemon.getPokemonId(), usersPokemon.getLevel());
        Map<Integer, Integer> statStages = new HashMap<Integer, Integer>();
        for (int i = 1; i < 9; i++) {
            statStages.put(i, 0);
        }

        this.statsStages = statStages;
        this.health = usersPokemon.getHealth();
        this.attack = allStats.get("attack");
        this.defense = allStats.get("defense");
        this.specialAttack = allStats.get("special-attack");
        this.specialDefense = allStats.get("special-defense");
        this.speed = allStats.get("speed");
        this.accuracy = allStats.get("accuracy");
        this.evasion = allStats.get("evasion");
        this.status = "none";
        this.statusTurn = 0;
        this.maxTurns = 0;
    }

//
//    // not sure if need this one. will figure out when implementing swapping
//    public BattlingPokemon(UsersPokemon usersPokemon, boolean wild, Map<Integer, Integer> statStages, int attack, int defense,
//                           int specialAttack, int specialDefense, int speed, int accuracy, int evasion,
//                           String status, int statusTurn) {
//        this.usersPokemonId = usersPokemon.getUsersPokemonId();
//        this.username = usersPokemon.getUsername();
//        this.pokemonId = usersPokemon.getPokemonId();
//        this.slotNum = usersPokemon.getSlotNum();
//        this.nickname = usersPokemon.getNickname();
//        this.level = usersPokemon.getLevel();
//        this.xp = usersPokemon.getXp();
//        this.moves  = usersPokemon.getMoves();
//        this.pps = usersPokemon.getPps();
//
//        this.wild = wild;
//        this.statsStages = statStages;
//        this.health = usersPokemon.getHealth();
//        this.attack = attack;
//        this.defense = defense;
//        this.specialAttack = specialAttack;
//        this.specialDefense = specialDefense;
//        this.speed = speed;
//        this.accuracy = accuracy;
//        this.evasion = evasion;
//        this.status = status;
//        this.statusTurn = statusTurn;
//    }

    public static UsersPokemon toUsersPokemon(BattlingPokemon battlingPokemon) {
        return new UsersPokemon(
                battlingPokemon.getUsersPokemonId(),
                battlingPokemon.getUsername(),
                battlingPokemon.getPokemonId(),
                battlingPokemon.getSlotNum(),
                battlingPokemon.getNickname(),
                battlingPokemon.getLevel(),
                battlingPokemon.getXp(),
                battlingPokemon.getHealth(),
                battlingPokemon.getMoves(),
                battlingPokemon.getPps()
        );
    }

    public void clearStatus() {
        this.setStatus(MoveMetaAilments.NONE);
        this.setStatusTurn(0);
        this.setMaxTurns(0);
    }
}
