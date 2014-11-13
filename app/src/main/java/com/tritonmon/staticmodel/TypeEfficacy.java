package com.tritonmon.staticmodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;
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
public class TypeEfficacy {

    @SerializedName("damager_type_id") private final int damagerTypeId;

    // water is 200x v fire so would be {water_id: 200}
    @SerializedName("target_type_to_damage_factor") private final Map<Integer, Integer> targetTypeToDamageFactor;
}
