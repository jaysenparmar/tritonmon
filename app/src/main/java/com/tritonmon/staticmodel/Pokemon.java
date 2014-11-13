package com.tritonmon.staticmodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
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
public class Pokemon {

    // everything pokemon related. important ones are evolution levels and what they evolve into
    @SerializedName("pokemon_id") private final int pokemonId;
    private final String name;
    @SerializedName("species_id") private final int speciesId;
    private final int height;
    private final int weight;

    // this refers to the xp you get from killing this pokemon
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

    // hashmap of {levelN: [move1, move2]} where you learn move1 and move2 when you level up to levelN
    @SerializedName("level_to_moves") private final Map<Integer, ArrayList<Integer>> levelToMoves;
    // hashmap of {stat: amount of base stat} where stat = hp, attack etc
    @SerializedName("stat_id_to_base_stat") private final Map<Integer, Integer> statIdToBaseStat;
    // list of types (poison, psychic) that a pokemon is
    @SerializedName("type_ids") private final List<Integer> typeIds;
}
