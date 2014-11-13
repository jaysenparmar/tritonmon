package com.tritonmon.model;

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
public class PokemonStats {
    @SerializedName("pokemon_id") private final int pokemonId;
    @SerializedName("stat_id_to_base_stat") private final Map<Integer, Integer> statIdToBaseStat;
}
