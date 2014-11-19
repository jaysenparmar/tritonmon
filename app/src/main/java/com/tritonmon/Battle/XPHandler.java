package com.tritonmon.Battle;

import com.tritonmon.global.Constant;
import com.tritonmon.staticmodel.Pokemon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XPHandler {

    // delta xp = a*b*L/(7*s)
    // a = 1 if fainted is wild, 1.5 if fainted is trained
    // b = base xp of fainted pokemon
    // L = level of fainted pokemon
    // s = # of non-fainted pokemon that participated in battle
    public static int xpGained(boolean isWild, int pokemon2_id, int pokemon2_level) {
        float wildFactor = isWild ? 1.5f : 1.0f;
        int base = Constant.pokemonData.get(pokemon2_id).getBaseXP();
        return Math.round(wildFactor*base*pokemon2_level/(7*1));
    }

    public static List<Integer> getNewMoves(int pokemon_id, int starting_level, int ending_level) {
        List<Integer> moves = new ArrayList<Integer>();
        Map<Integer, List<Integer>> levelToMoves = Constant.pokemonData.get(pokemon_id).getLevelToMoves();
        for (int i = starting_level; i < ending_level + 1; i++) {
            if (levelToMoves.containsKey(i)) {
                moves.addAll(levelToMoves.get(i));
            }
        }
        return moves;
    }

    public static Map<Integer, List<Integer>> getNewMovesWithLevel(int pokemon_id, int starting_level, int ending_level) {
        Map<Integer, List<Integer>> moves = new HashMap<Integer, List<Integer>>();
        Map<Integer, List<Integer>> levelToMoves = Constant.pokemonData.get(pokemon_id).getLevelToMoves();
        for (int i = starting_level; i < ending_level + 1; i++) {
            if (levelToMoves.containsKey(i)) {
                moves.put(i, levelToMoves.get(i));
            }
        }
        return moves;
    }

    public static int newLevel(int xp) {
        return (int)Math.floor(Math.pow(xp, (1/3)));
    }

    public static float xpBarFraction (int xp, int level) {
        int baseXpForLevel = Constant.levelUpXpData.get(level).getXp();
        return (float)(xp-baseXpForLevel)/Constant.levelUpXpData.get(level).getXpToNextlevel();
    }

    public static int newPokemonEvolution (int pokemon_id, int pokemon_level) {
        if (Constant.pokemonData.get(pokemon_id).getEvolutionLevel() == pokemon_level) {
            return Constant.pokemonData.get(pokemon_id).getEvolvesIntoPokemonId();
        }
        return pokemon_id;
    }
}
