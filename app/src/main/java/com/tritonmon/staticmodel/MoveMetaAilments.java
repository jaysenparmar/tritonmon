package com.tritonmon.staticmodel;

import com.google.gson.annotations.SerializedName;
import com.tritonmon.global.Constant;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor(suppressConstructorProperties = true)
public class MoveMetaAilments {


    // ex poison, sleep, paralysis
    @SerializedName("move_meta_ailment_id") private final int moveMetaAilmentId;
    private final String name;
    @SerializedName("catch_rate") private final Float catchRate;
    public static final float PARALYSIS_FACTOR = 0.25f;
    public static final float BURN_FACTOR = 0.5f;
    public static final float UNFREEZE_PROB = 0.2f;
    public static final float CONFUSION_PROB = 0.5f;
    public static final float HURT_FACTOR = 1.0f/8.0f;

    public static final String NONE = "none";
    public static final String PARALYSIS = "paralysis";
    public static final String SLEEP = "sleep";
    public static final String FREEZE = "freeze";
    public static final String BURN = "burn";
    public static final String POISON = "poison";
    public static final String CONFUSION = "confusion";

    public static String getName(Integer moveMetaAilmentId) {
        for (Map.Entry<String, MoveMetaAilments> ele : Constant.moveMetaAilmentsData.entrySet()) {
            if (ele.getValue().getMoveMetaAilmentId() == moveMetaAilmentId) {
                return ele.getKey();
            }
        }
        return null;
    }
}
