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
    @SerializedName("pokemon_id") private final int pokemonId;
    private final String name;
    @SerializedName("species_id") private final int speciesId;
    private final int height;
    private final int weight;
    @SerializedName("base_xp") private final int baseXP;
    @SerializedName("generation_id") private final int generationId;
    @SerializedName("evolves_into_pokemon_id") private final int evolvesIntoPokemonId;
    @SerializedName("habitat_id") private final int habitatId;
    @SerializedName("gender_rate") private final int genderRate;
    @SerializedName("capture_rate") private final int captureRate;
    @SerializedName("has_gender_differences") private final int hasGenderDifferences;
    @SerializedName("growth_rate_id") private final int growthRateId;
    @SerializedName("evolution_level") private final int evolutionLevel;
    @SerializedName("order_id") private final int orderId;
    @SerializedName("is_default") private final boolean isDefault;
}
