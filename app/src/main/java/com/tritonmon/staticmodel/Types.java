package com.tritonmon.staticmodel;

import com.google.gson.annotations.SerializedName;

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
public class Types {


    //ex fighting/flying
    @SerializedName("type_id") private final int typeId;
    @SerializedName("damage_class_id") private final int damageClassId;
    @SerializedName("generation_id") private final int generationId;
    private final String name;

    // water is 200x v fire so would be {water_id: 200}
    @SerializedName("target_type_to_damage_factor") private final Map<Integer, Integer> targetTypeToDamageFactor;


}
