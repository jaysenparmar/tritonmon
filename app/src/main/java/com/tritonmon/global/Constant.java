package com.tritonmon.global;

import android.graphics.Color;
import android.util.Log;

import com.tritonmon.model.User;
import com.tritonmon.staticmodel.Geolocation;
import com.tritonmon.staticmodel.LevelUpXp;
import com.tritonmon.staticmodel.MoveMetaAilments;
import com.tritonmon.staticmodel.Moves;
import com.tritonmon.staticmodel.Pokemon;
import com.tritonmon.staticmodel.Stats;
import com.tritonmon.staticmodel.Types;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constant {

    public static final boolean DEBUG = false;
    public static final boolean DISABLE_ACTION_BAR = false;

    /** animation **/
    public static final int ANIM_FADE_LENGTH = 1000;

    /** request codes **/
    public static final int REQUEST_CODE_BATTLE_PARTY = 1;

    /** battle **/
    public static final int NUM_BATTLE_BGS = 27;
    public static final int NUM_BATTLE_BASES = 31;

    public static final String BATTLE_BG_NAME = "battlebg";
    public static final String BATTLE_BASE_NAME = "enemybase";
    public static final String BATTLE_ATK_BTN_NAME = "atk_btn_";

    public static final int GREEN_HEALTH_THRESHOLD = 50;
    public static final int YELLOW_HEALTH_THRESHOLD = 20;

    public static final int SPLASH_MOVEID = 150;

    public static final int DISABLE_COLOR = Color.parseColor("#AA330000");

    /** server **/
    public static final String ENCODING = "UTF-8";

    // server url
    public static final String SERVER_BASE_URL = "http://ec2-54-193-111-74.us-west-1.compute.amazonaws.com:8080";
    public static final String SERVER_URL = SERVER_BASE_URL + "/tritonmon-server";

    // server response status codes
    public static final int STATUS_CODE_SUCCESS = 200; // success
    public static final int STATUS_CODE_500 = 500; // cannot find table,column,attribute or insert/update into table
    public static final int STATUS_CODE_204 = 204; // returned 0 rows or trying to insert already existing data
    public static final int STATUS_CODE_404 = 404; // invalid query string

    /** ANDRE'S STUFF **/
    public enum Models {
        DAMAGECLASSES,
        GEOLOCATION,
        LEVELUPXP,
        MOVEMETAAILMENTS,
        MOVES,
        POKEMON,
        STATS,
        TYPES
    }

    public static Map<Integer, Float> criticalChanceMap = new HashMap<Integer, Float>();
    public static Map<Integer, Float> attackDefStageMap = new HashMap<Integer, Float>();
    public static Map<Integer, Float> accuracyEvasionStageMap = new HashMap<Integer, Float>();
    public static Map<String, Integer> locationDataMap = new HashMap<String, Integer>();

    public static Map<String, Integer> damageClassesData = new HashMap<String, Integer>();
    public static Map<String, Geolocation> geolocationData = new HashMap<String, Geolocation>();
    public static Map<Integer, LevelUpXp> levelUpXpData = new HashMap<Integer, LevelUpXp>();
    public static Map<String, MoveMetaAilments> moveMetaAilmentsData = new HashMap<String, MoveMetaAilments>();
    public static Map<Integer, Moves> movesData = new HashMap<Integer, Moves>();
    public static Map<Integer, Pokemon> pokemonData = new HashMap<Integer, Pokemon>();
    public static Map<String, Stats> statsData = new HashMap<String, Stats>();
    public static Map<Integer, Types> typesData = new HashMap<Integer, Types>();

    public static Map<Integer, User> userData = new HashMap<Integer, User>();
    public static Map<Integer, Integer> pokemonMinLevelsData = new HashMap<Integer, Integer>();

    public static List<String> femaleAvatars = new ArrayList<String>();
    public static List<String> maleAvatars = new ArrayList<String>();

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

    public static String redText(String text) {
        return "<font color=#ff0000>" + text + "</font>";
    }
}
