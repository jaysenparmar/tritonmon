package com.tritonmon.global;

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

import java.util.HashMap;
import java.util.Map;

public class Constant {

    public static final int ANIM_FADE_LENGTH = 1000;

    /** Server **/
    public static final String ENCODING = "UTF-8";

    // server url
    public static final String SERVER_BASE_URL = "http://ec2-54-193-111-74.us-west-1.compute.amazonaws.com:8080";
    public static final String SERVER_URL = SERVER_BASE_URL + "/tritonmon-server";

    // server response status codes
    public static final int STATUS_CODE_SUCCESS = 200; // success
    public static final int STATUS_CODE_500 = 500; // cannot find table,column,attribute or insert/update into table
    public static final int STATUS_CODE_204 = 204; // returned 0 rows or trying to insert already existing data
    public static final int STATUS_CODE_404 = 404; // invalid query string

    // ANDRE'S STUFF
    public enum Models {
        DAMAGECLASSES,
        LEVELUPXP,
        MOVEMETAAILMENTS,
        MOVEMETASTATCHANGES,
        MOVES,
        POKEMON,
        POKEMONMOVES,
        POKEMONSTATS,
        POKEMONTYPES,
        STATS,
        TYPEEFFICACY,
        TYPES
    }

    public static Map<Integer, DamageClasses> damageClassesData = new HashMap<Integer, DamageClasses>();
    public static Map<Integer, LevelUpXp> levelUpXpData = new HashMap<Integer, LevelUpXp>();
    public static Map<Integer, MoveMetaAilments> moveMetaAilmentsData = new HashMap<Integer, MoveMetaAilments>();
    public static Map<Integer, MoveMetaStatChanges> moveMetaStatChangesData = new HashMap<Integer, MoveMetaStatChanges>();
    public static Map<Integer, Moves> movesData = new HashMap<Integer, Moves>();
    public static Map<Integer, Pokemon> pokemonData = new HashMap<Integer, Pokemon>();
    public static Map<Integer, PokemonMoves> pokemonMovesData = new HashMap<Integer, PokemonMoves>();
    public static Map<Integer, PokemonStats> pokemonStatsData = new HashMap<Integer, PokemonStats>();
    public static Map<Integer, PokemonTypes> pokemonTypesData = new HashMap<Integer, PokemonTypes>();
    public static Map<Integer, Stats> statsData = new HashMap<Integer, Stats>();
    public static Map<Integer, TypeEfficacy> typeEfficacyData = new HashMap<Integer, TypeEfficacy>();
    public static Map<Integer, Types> typesData = new HashMap<Integer, Types>();

}
