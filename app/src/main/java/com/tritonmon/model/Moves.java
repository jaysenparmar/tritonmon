package com.tritonmon.model;

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
public class Moves {
    @SerializedName("move_id") private final int moveId;
    @SerializedName("generation_id") private final int generationId;
    @SerializedName("flinch_chance") private final int flinchChance;
    @SerializedName("max_hits") private final int maxHits;
    @SerializedName("min_hits") private final int minHits;
    @SerializedName("max_turns") private final int maxTurns;
    @SerializedName("min_turns") private final int minTurns;
    private final int drain;
    @SerializedName("type_id") private final int typeId;
    @SerializedName("target_id") private final int targetId;
    @SerializedName("stat_chance") private final int statChance;
    @SerializedName("ailment_chance") private final int ailmentChance;
    private final int priority;
    private final String name;
    @SerializedName("crit_chance") private final int critRate;
    private final int power;
    @SerializedName("damage_class_id") private final int damageClassId;
    @SerializedName("move_meta_ailment_id") private final int moveMetaAilmentId;
    private final int accuracy;
    private final int pp;
    private final int healing;
}
