package com.tritonmon.model;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor(suppressConstructorProperties = true)
@AllArgsConstructor(suppressConstructorProperties = true)
public class User {
    private String username;
    private String password;
    private char gender;
    private String hometown;
    @SerializedName("num_pokeballs") private int numPokeballs;
    @SerializedName("fav_pokemon_id") private int favPokemonId;
}
