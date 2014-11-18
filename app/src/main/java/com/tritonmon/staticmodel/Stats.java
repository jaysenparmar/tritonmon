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
public class Stats {

    public static final String HP = "hp";
    public static final String ATTACK = "attack";
    public static final String DEFENSE = "defense";
    public static final String SPECIAL_ATTACK = "special-attack";
    public static final String SPECIAL_DEFENSE = "special-defense";
    public static final String SPEED = "speed";
    public static final String ACCURACY = "accuracy";
    public static final String EVASION = "evasion";

    // attack/defence/specialattack/speed etc
    @SerializedName("stat_id") private final int statId;
    @SerializedName("damage_class_id") private final int damageClassId;
    private final String name;

    public static String getName(int statId) {
        for (Map.Entry<String, Stats> ele : Constant.statsData.entrySet()) {
            if (ele.getValue().getStatId() == statId) {
                return ele.getValue().getName();
            }
        }
        return null;
    }
}