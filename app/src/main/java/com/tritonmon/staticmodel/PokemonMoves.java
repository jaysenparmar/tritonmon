package com.tritonmon.staticmodel;

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

    // hashmap of {levelN: [move1, move2]} where you learn move1 and move2 when you level up to levelN
    @SerializedName("level_to_moves") private final Map<Integer, ArrayList<Integer>> levelToMoves;
}
