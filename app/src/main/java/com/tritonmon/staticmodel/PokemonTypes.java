package com.tritonmon.staticmodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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
public class PokemonTypes {

    @SerializedName("pokemon_id") private final int pokemonId;

    // list of types (poison, psychic) that a pokemon is
    @SerializedName("type_ids") private final List<Integer> typeIds;
}
