package com.tritonmon.model;

import com.google.gson.annotations.SerializedName;
import com.tritonmon.global.Constant;
import com.tritonmon.staticmodel.Pokemon;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
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
    private int health;
    private int status;

    private List<Integer> moves;
    private List<Integer> pps;

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
