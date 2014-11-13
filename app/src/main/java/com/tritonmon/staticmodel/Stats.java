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
public class Stats {

    // attack/defence/specialattack/speed etc
    @SerializedName("stat_id") private final int statId;
    @SerializedName("damage_class_id") private final int damageClassId;
    private final String name;
}