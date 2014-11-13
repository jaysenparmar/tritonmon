package com.tritonmon.model;

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
    @SerializedName("target_type_to_damage_factor") private Map<Integer, Integer> targetTypeToDamageFactor;
}
