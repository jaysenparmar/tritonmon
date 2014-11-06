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
public class Pokemon {
    private final int id;
    private final String name;
    @SerializedName("species_id") private final int speciesId;
    private final int height;
    private final int weight;
    @SerializedName("base_xp") private final int baseXP;
    @SerializedName("order_id") private final int orderId;
    @SerializedName("is_default") private final boolean isDefault;
}
