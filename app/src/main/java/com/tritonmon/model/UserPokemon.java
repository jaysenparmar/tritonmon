package com.tritonmon.model;

import com.google.gson.annotations.SerializedName;
import com.tritonmon.staticmodel.Pokemon;

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
public class UserPokemon {
    private int id;
    @SerializedName("user_id") private int userId;
    @SerializedName("party_id") private int partyId;
    @SerializedName("pokemon_id") private int pokemonId;
    private String nickname;
    private int level;
    private int xp;
    private int status;
    private int move1;
    private int move2;
    private int move3;
    private int move4;

    private User user;
    private Pokemon pokemon;

    // TODO if (nickname) { return nickname; } else { return pokemon.name; }
    public String getName() {
        return null;
    }
}
