package com.tritonmon.global;

import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.tritonmon.model.DamageClasses;
import com.tritonmon.model.LevelUpXp;
import com.tritonmon.model.MoveMetaAilments;
import com.tritonmon.model.MoveMetaStatChanges;
import com.tritonmon.model.Moves;
import com.tritonmon.model.Pokemon;
import com.tritonmon.model.PokemonMoves;
import com.tritonmon.model.PokemonStats;
import com.tritonmon.model.PokemonTypes;
import com.tritonmon.model.Stats;
import com.tritonmon.model.TypeEfficacy;
import com.tritonmon.model.Types;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.List;

public class StaticData {

    public static void load(AssetManager assetManager) throws ParseException {
        loadData("damage_classes.json", Constant.Models.DAMAGECLASSES, assetManager);
        loadData("level_up_xp.json", Constant.Models.LEVELUPXP, assetManager);
        loadData("move_meta_ailments.json", Constant.Models.MOVEMETAAILMENTS, assetManager);
        loadData("move_meta_stat_changes.json", Constant.Models.MOVEMETASTATCHANGES, assetManager);
        loadData("moves.json", Constant.Models.MOVES, assetManager);
        loadData("pokemon.json", Constant.Models.POKEMON, assetManager);
        loadData("pokemon_moves.json", Constant.Models.POKEMONMOVES, assetManager);
        loadData("pokemon_stats.json", Constant.Models.POKEMONSTATS, assetManager);
        loadData("pokemon_types.json", Constant.Models.POKEMONTYPES, assetManager);
        loadData("stats.json", Constant.Models.STATS, assetManager);
        loadData("type_efficacy.json", Constant.Models.TYPEEFFICACY, assetManager);
        loadData("types.json", Constant.Models.TYPES, assetManager);
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
            case LEVELUPXP:
                populateLevelUpXp(content);
                break;
            case MOVEMETAAILMENTS:
                populateMoveMetaAilments(content);
                break;
            case MOVEMETASTATCHANGES:
                populateMoveMetaStatChanges(content);
                break;
            case MOVES:
                populateMoves(content);
                break;
            case POKEMON:
                populatePokemon(content);
                break;
            case POKEMONMOVES:
                populatePokemonMoves(content);
                break;
            case POKEMONSTATS:
                populatePokemonStats(content);
                break;
            case POKEMONTYPES:
                populatePokemonTypes(content);
                break;
            case STATS:
                populateStats(content);
                break;
            case TYPEEFFICACY:
                populateTypeEfficacy(content);
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
            Constant.damageClassesData.put(ele.getDamageClassId(), ele);
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
            Constant.moveMetaAilmentsData.put(ele.getMoveMetaAilmentId(), ele);
        }
    }

    private static void populateMoveMetaStatChanges(String content) {
        List<MoveMetaStatChanges> arr = MyGson.getInstance().fromJson(content, new TypeToken<List<MoveMetaStatChanges>>(){}.getType());
        for (MoveMetaStatChanges ele : arr) {
            Constant.moveMetaStatChangesData.put(ele.getMoveId(), ele);
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
        System.out.println(arr.size());
        for (Pokemon ele : arr) {
            Constant.pokemonData.put(ele.getPokemonId(), ele);
        }
    }

    private static void populatePokemonMoves(String content) {
        List<PokemonMoves> arr = MyGson.getInstance().fromJson(content, new TypeToken<List<PokemonMoves>>(){}.getType());
        for (PokemonMoves ele : arr) {
            Constant.pokemonMovesData.put(ele.getPokemonId(), ele);
        }
    }

    private static void populatePokemonStats(String content) {
        List<PokemonStats> arr = MyGson.getInstance().fromJson(content, new TypeToken<List<PokemonStats>>(){}.getType());
        for (PokemonStats ele : arr) {
            Constant.pokemonStatsData.put(ele.getPokemonId(), ele);
        }
    }

    private static void populatePokemonTypes(String content) {
        List<PokemonTypes> arr = MyGson.getInstance().fromJson(content, new TypeToken<List<PokemonTypes>>(){}.getType());
        for (PokemonTypes ele : arr) {
            Constant.pokemonTypesData.put(ele.getPokemonId(), ele);
        }
    }

    private static void populateStats(String content) {
        List<Stats> arr = MyGson.getInstance().fromJson(content, new TypeToken<List<Stats>>(){}.getType());
        for (Stats ele : arr) {
            Constant.statsData.put(ele.getStatId(), ele);
        }
    }

    private static void populateTypeEfficacy(String content) {
        List<TypeEfficacy> arr = MyGson.getInstance().fromJson(content, new TypeToken<List<TypeEfficacy>>(){}.getType());
        for (TypeEfficacy ele : arr) {
            Constant.typeEfficacyData.put(ele.getDamagerTypeId(), ele);
        }
    }

    private static void populateTypes(String content) {
        List<Types> arr = MyGson.getInstance().fromJson(content, new TypeToken<List<Types>>(){}.getType());
        for (Types ele : arr) {
            Constant.typesData.put(ele.getTypeId(), ele);
        }
    }

}
