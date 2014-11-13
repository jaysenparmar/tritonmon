package com.tritonmon.model;

import com.google.gson.annotations.SerializedName;
import com.tritonmon.global.Constant;
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
    private String username;

    @SerializedName("slot_num") private int slotNum;
    @SerializedName("pokemon_id") private int pokemonId;
    private String nickname;
    private int level;
    private int xp;
    private int status;

    private int move1;
    private int move2;
    private int move3;
    private int move4;

    private int pp1;
    private int pp2;
    private int pp3;
    private int pp4;

    private User user;
    private Pokemon pokemon;

    public String getNickname() { // TODO if (nickname) { return nickname; } else { return pokemon.name; }
        if (nickname == null || nickname.isEmpty()) {
            return Constant.pokemonData.get(pokemonId).getName();
        }
        else {
            return nickname;
        }
    }

    public String getName() {
        return Constant.pokemonData.get(pokemonId).getName();
    }
}
