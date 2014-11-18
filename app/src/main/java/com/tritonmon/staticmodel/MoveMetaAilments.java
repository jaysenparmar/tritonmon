package com.tritonmon.staticmodel;

import com.google.gson.annotations.SerializedName;

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
    public static final String NONE = "none";
    public static final String PARALYSIS = "paralysis";
    public static final String SLEEP = "sleep";
    public static final String FREEZE = "freeze";
    public static final String BURN = "burn";

}
