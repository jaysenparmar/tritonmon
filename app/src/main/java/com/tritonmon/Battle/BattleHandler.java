package com.tritonmon.Battle;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class BattleHandler {

    public static BattlingPokemon initWildPokemon(BattlingPokemon pokemon2) {
        int pokemon2_id = pokemon2.getPokemonId();
        int pokemon2_level =  pokemon2.getLevel();

        // possible make it better than just random moves. make a pokemon with
        List<Integer> moves = generateMoves(XpHandler.getNewMovesWithLevel(pokemon2_id, 0, pokemon2_level));

        Map<String, Integer> allStats = BattleUtil.getAllMaxStats(pokemon2_id, pokemon2_level);
//        return new BattlingPokemon(pokemon2_id, pokemon2_level, -1, true,)
        List<Integer> pps = Arrays.asList(1, 1, 1, 1);
        Map<Integer, Integer> statStages = new HashMap<Integer, Integer>();
        for (int i = 0; i < 8; i++) {
            statStages.put(i, 0);
        }
        return new BattlingPokemon(pokemon2_id, pokemon2_level, -1, true, statStages, allStats.get("hp"), allStats.get("attack"), allStats.get("defense"),
                allStats.get("special-attack"), allStats.get("special-defense"), allStats.get("speed"), allStats.get("accuracy"), allStats.get("evasion"),
                moves, pps, "None", -1);
    }

    private static List<Integer> generateMoves(Map<Integer, List<Integer>> possMoves) {
        List<Integer> chosenMoves = new ArrayList<Integer>();
        SortedMap<Integer, Integer> moveProbMap = new TreeMap<Integer, Integer>();

        int count = 0;
        int total = 0;
        for (Map.Entry<Integer, List<Integer>> entry : possMoves.entrySet()) {
            for (Integer innerEle : entry.getValue()) {
                total+=entry.getKey();
                moveProbMap.put(total, innerEle);
                count++;
            }
        }

        if (count <= 4) {
            for (Map.Entry<Integer, List<Integer>> entry : possMoves.entrySet()) {
                for (Integer innerEle : entry.getValue()) {
                    chosenMoves.add(innerEle);
                }
            }
            while (chosenMoves.size() < 4) {
                chosenMoves.add(null);
            }
            return chosenMoves;
        }

        int chosenMove;
        while (chosenMoves.size() < 4) {
            chosenMove = populateChosenMoves(total, moveProbMap);
            if (!chosenMoves.contains(chosenMove)) {
                chosenMoves.add(chosenMove);
            }
        }
        return chosenMoves;
    }

    // TODO: check this actually works
    private static int populateChosenMoves(int total, SortedMap<Integer, Integer> moveProbMap) {
        double randomVar = Math.random() * total;

        for (Map.Entry<Integer, Integer> entry : moveProbMap.entrySet()) {
            if (entry.getKey() > randomVar) {
                return entry.getValue();
            }
        }
        // technically should never reach here
        return moveProbMap.lastKey();

    }


    public static void continueBattle(BattleRequest battleRequest) {

    }

    public static void doMove(int moveOrder) {

    }

//    public static boolean throwPokeball() {
//
//    }

//    public static void switchPokemon(int pokemonId) {
//    }

//    public static int newHp(int currentHp, int damage) {
//        return currentHp >= damage ? currentHp-damage : 0;
//    }

}
