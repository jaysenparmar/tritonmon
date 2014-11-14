package com.tritonmon.global;

import android.util.Log;

import com.google.common.collect.ImmutableMap;
import com.tritonmon.staticmodel.DamageClasses;
import com.tritonmon.staticmodel.LevelUpXp;
import com.tritonmon.staticmodel.MoveMetaAilments;
import com.tritonmon.staticmodel.Moves;
import com.tritonmon.staticmodel.Pokemon;
import com.tritonmon.staticmodel.Stats;
import com.tritonmon.staticmodel.Types;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
        MOVES,
        POKEMON,
        STATS,
        TYPES
    }

    public static final Map<Integer, Float> criticalChanceMap = ImmutableMap.of(0, 0.0625f, 1, 0.125f, 6, 1.0f);

    public static Map<Integer, DamageClasses> damageClassesData = new HashMap<Integer, DamageClasses>();
    public static Map<Integer, LevelUpXp> levelUpXpData = new HashMap<Integer, LevelUpXp>();
    public static Map<Integer, MoveMetaAilments> moveMetaAilmentsData = new HashMap<Integer, MoveMetaAilments>();
    public static Map<Integer, Moves> movesData = new HashMap<Integer, Moves>();
    public static Map<Integer, Pokemon> pokemonData = new HashMap<Integer, Pokemon>();
    public static Map<String, Stats> statsData = new HashMap<String, Stats>();
    public static Map<Integer, Types> typesData = new HashMap<Integer, Types>();

    public static String encode(String unencoded) {
        try {
            return URLEncoder.encode(unencoded, Constant.ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            Log.e("Constant", "URLEncoder threw UnsupportedEncodingException");
            e.printStackTrace();
            return null;
        }
    }
}
