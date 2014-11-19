package com.tritonmon.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.tritonmon.global.Constant;

import java.util.ArrayList;
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
public class UsersPokemon implements Parcelable {

    @SerializedName("users_pokemon_id") protected int usersPokemonId;
    protected String username;
    @SerializedName("pokemon_id") protected int pokemonId;
    @SerializedName("slot_num") protected int slotNum;

    protected String nickname;
    protected int level;
    protected int xp;
    protected int health;

    // i dont think we need this
//    private int status;

    protected List<Integer> moves;
    protected List<Integer> pps;

    // method to recreate a UserPokemon from a Parcel
    public static Creator<UsersPokemon> CREATOR = new Creator<UsersPokemon>() {

        @Override
        public UsersPokemon createFromParcel(Parcel source) {
            return new UsersPokemon(source);
        }

        @Override
        public UsersPokemon[] newArray(int size) {
            return new UsersPokemon[size];
        }
    };

    public UsersPokemon(Parcel parcel) {
        usersPokemonId = parcel.readInt();
        username = parcel.readString();
        pokemonId = parcel.readInt();
        slotNum = parcel.readInt();

        nickname = parcel.readString();
        level = parcel.readInt();
        xp = parcel.readInt();
        health = parcel.readInt();
//        status = parcel.readInt();

        moves = new ArrayList<Integer>();
        parcel.readList(moves, null);
        pps = new ArrayList<Integer>();
        parcel.readList(pps, null);
    }

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

    public int getTotalXPBar() {
        return Constant.levelUpXpData.get(level).getXpToNextlevel();
    }

    public int getCurrentXPBar() {
        return xp - Constant.levelUpXpData.get(level).getXp();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(usersPokemonId);
        dest.writeString(username);
        dest.writeInt(pokemonId);
        dest.writeInt(slotNum);

        dest.writeString(nickname);
        dest.writeInt(level);
        dest.writeInt(xp);
        dest.writeInt(health);
//        dest.writeInt(status);

        dest.writeList(moves);
        dest.writeList(pps);
    }
}
