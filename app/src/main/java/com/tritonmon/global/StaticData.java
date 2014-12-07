package com.tritonmon.global;

import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.tritonmon.global.singleton.MyGson;
import com.tritonmon.staticmodel.DamageClasses;
import com.tritonmon.staticmodel.Geolocation;
import com.tritonmon.staticmodel.LevelUpXp;
import com.tritonmon.staticmodel.MoveMetaAilments;
import com.tritonmon.staticmodel.Moves;
import com.tritonmon.staticmodel.Pokemon;
import com.tritonmon.staticmodel.Stats;
import com.tritonmon.staticmodel.Types;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticData {

    public static void load(AssetManager assetManager) throws ParseException {
        loadData("damage_classes.json", Constant.Models.DAMAGECLASSES, assetManager);
        loadData("geolocation.json", Constant.Models.GEOLOCATION, assetManager);
        loadData("level_up_xp.json", Constant.Models.LEVELUPXP, assetManager);
        loadData("move_meta_ailments.json", Constant.Models.MOVEMETAAILMENTS, assetManager);
        loadData("moves.json", Constant.Models.MOVES, assetManager);
        loadData("pokemon.json", Constant.Models.POKEMON, assetManager);
        loadData("stats.json", Constant.Models.STATS, assetManager);
        loadData("types.json", Constant.Models.TYPES, assetManager);
        populateMaps();

    }

    private static void populateMaps() {
        Constant.criticalChanceMap.put(0, 0.0625f);
        Constant.criticalChanceMap.put(1, 0.125f);
        Constant.criticalChanceMap.put(6, 1.0f);

        float attackDefVal;
        float accuracyEvasionVal;
        for (int i = -6; i < 7; i++) {
            attackDefVal = i < 0 ? 2.0f/(float)(-1*(i-2)) : (float)(i+2)/2.0f;
            accuracyEvasionVal = i < 0 ? 3.0f/(float)(-1*(i-3)) : (float)(i+3)/3.0f;
            Constant.attackDefStageMap.put(i, attackDefVal);
            Constant.accuracyEvasionStageMap.put(i, accuracyEvasionVal);
        }

        Constant.locationDataMap.put("ERC", 1);
        Constant.locationDataMap.put("Geisel", 2);
        Constant.locationDataMap.put("Marshall", 3);
        Constant.locationDataMap.put("Matthews Quad", 4);
        Constant.locationDataMap.put("Muir", 5);
        Constant.locationDataMap.put("Muir Parking", 6);
        Constant.locationDataMap.put("Price Center", 7);
        Constant.locationDataMap.put("Revelle", 8);
        Constant.locationDataMap.put("Revelle Parking", 9);
        Constant.locationDataMap.put("Rimac", 10);
        Constant.locationDataMap.put("Sixth Apartment", 11);
        Constant.locationDataMap.put("Sixth Res Halls", 12);
        Constant.locationDataMap.put("VA Hospital", 13);
        Constant.locationDataMap.put("Village", 14);
        Constant.locationDataMap.put("Warren", 15);
        Constant.locationDataMap.put("Warren Field", 16);
        Constant.locationDataMap.put("Warren Mall", 17);
        Constant.locationDataMap.put("UCSD", -1);
        Constant.locationDataMap.put("", -1);
    }

    // TODO: make generic somehow
    private static void loadData(String filename, Constant.Models theModel, AssetManager assetManager) throws ParseException {
        BufferedReader br = null;
        String content = null;

        try {
            br = new BufferedReader(new InputStreamReader(assetManager.open(filename)));
            // TODO: add error checking
            content = br.readLine();
            if (br.readLine() != null) {
                Log.e("Tritonmon", "too many lines!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (content == null || content.isEmpty()) {
            throw new ParseException("error reading files for static data", 0);
        }

        switch (theModel) {

            case DAMAGECLASSES:
                populateDamageClasses(content);
                break;
            case GEOLOCATION:
                populateGeolocation(content);
                break;
            case LEVELUPXP:
                populateLevelUpXp(content);
                break;
            case MOVEMETAAILMENTS:
                populateMoveMetaAilments(content);
                break;
            case MOVES:
                populateMoves(content);
                break;
            case POKEMON:
                populatePokemon(content);
                break;
            case STATS:
                populateStats(content);
                break;
            case TYPES:
                populateTypes(content);
                break;
            default:
                Log.e("Tritonmon", "unexpected model");
                break;
        }
    }

    private static void populateDamageClasses(String content) {
        List<DamageClasses> arr = MyGson.getInstance().fromJson(content, new TypeToken<List<DamageClasses>>(){}.getType());
        for (DamageClasses ele : arr) {
            Constant.damageClassesData.put(ele.getName(), ele.getDamageClassId());
        }
    }

    private static void populateGeolocation(String content) {
        List<Geolocation> arr = MyGson.getInstance().fromJson(content, new TypeToken<List<Geolocation>>(){}.getType());
        for (Geolocation ele : arr) {
            Constant.geolocationData.put(ele.getName(), ele);
        }
    }

    private static void populateLevelUpXp(String content) {
        List<LevelUpXp> arr = MyGson.getInstance().fromJson(content, new TypeToken<List<LevelUpXp>>(){}.getType());
        for (LevelUpXp ele : arr) {
            Constant.levelUpXpData.put(ele.getLevel(), ele);
        }
    }

    private static void populateMoveMetaAilments(String content) {
        List<MoveMetaAilments> arr = MyGson.getInstance().fromJson(content, new TypeToken<List<MoveMetaAilments>>(){}.getType());
        for (MoveMetaAilments ele : arr) {
            Constant.moveMetaAilmentsData.put(ele.getName(), ele);
        }
    }

    private static void populateMoves(String content) {
        List<Moves> arr = MyGson.getInstance().fromJson(content, new TypeToken<List<Moves>>(){}.getType());
        for (Moves ele : arr) {
            Constant.movesData.put(ele.getMoveId(), ele);
        }
    }

    private static void populatePokemon(String content) {
        List<Pokemon> arr = MyGson.getInstance().fromJson(content, new TypeToken<List<Pokemon>>(){}.getType());

        // cuz im too lazy/come too far to break anything to do foreign key constraints in the db
        Pokemon tmpPokemon;
        Map<Integer, List<Integer>> tmpLevelToMoves;
        List<Integer> tmpMovesPerLevel;
        for (Pokemon perPokemon : arr) {
            tmpPokemon = perPokemon;
            tmpLevelToMoves = new HashMap<Integer, List<Integer>>();
            for (Map.Entry<Integer, List<Integer>> perLevelToMove : perPokemon.getLevelToMoves().entrySet()) {
                tmpMovesPerLevel = new ArrayList<Integer>();
                for (Integer move : perLevelToMove.getValue()) {
                    if (Constant.movesData.get(move) != null) {
                        tmpMovesPerLevel.add(move);
                    }
                }
                if (tmpMovesPerLevel.isEmpty()) {
                    tmpLevelToMoves.remove(perLevelToMove.getKey());
                } else {
                    tmpLevelToMoves.put(perLevelToMove.getKey(), tmpMovesPerLevel);
                }
            }
            tmpPokemon.setLevelToMoves(tmpLevelToMoves);
//            Log.e("CHICHI", tmpPokemon.toString());
            if (perPokemon.getEvolvesIntoPokemonId() > 386) {
                tmpPokemon.setEvolvesIntoPokemonId(0);
                tmpPokemon.setEvolutionLevel(0);
            }
            Constant.pokemonData.put(perPokemon.getPokemonId(), tmpPokemon);
            if (perPokemon.getEvolvesIntoPokemonId() != 0) {
                Constant.pokemonMinLevelsData.put(perPokemon.getEvolvesIntoPokemonId(), perPokemon.getEvolutionLevel());
            }
        }
        for (int i = 1; i < 387; i++) {
            if (!Constant.pokemonMinLevelsData.keySet().contains(i)) {
                Constant.pokemonMinLevelsData.put(i, 0);
            }
        }
    }

    private static void populateStats(String content) {
        List<Stats> arr = MyGson.getInstance().fromJson(content, new TypeToken<List<Stats>>(){}.getType());
        for (Stats ele : arr) {
            Constant.statsData.put(ele.getName(), ele);
        }
    }

    private static void populateTypes(String content) {
        List<Types> arr = MyGson.getInstance().fromJson(content, new TypeToken<List<Types>>(){}.getType());
        for (Types ele : arr) {
            Constant.typesData.put(ele.getTypeId(), ele);
        }
    }

}
