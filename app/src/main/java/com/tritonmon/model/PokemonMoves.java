package com.tritonmon.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
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
public class PokemonMoves {
    @SerializedName("pokemon_id") private final int pokemonId;
    @SerializedName("level_to_moves") private final Map<Integer, ArrayList<Integer>> levelToMoves;
}
