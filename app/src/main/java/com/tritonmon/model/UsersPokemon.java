package com.tritonmon.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.tritonmon.battle.BattleUtil;
import com.tritonmon.global.Constant;
import com.tritonmon.global.ImageUtil;
import com.tritonmon.staticmodel.Moves;
import com.tritonmon.staticmodel.Stats;

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
public class UsersPokemon implements Parcelable, Comparable<UsersPokemon> {

    @SerializedName("users_pokemon_id") protected int usersPokemonId;
    @SerializedName("users_id") protected int usersId;
    @SerializedName("pokemon_id") protected int pokemonId;
    @SerializedName("slot_num") protected int slotNum;

    protected String nickname;
    protected int level;
    protected int xp;
    protected int health;

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
        usersId = parcel.readInt();
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

    public UsersPokemon(UsersPokemon pokemon) {
        usersPokemonId = pokemon.getUsersPokemonId();
        usersId = pokemon.getUsersId();
        pokemonId = pokemon.getPokemonId();
        slotNum = pokemon.getSlotNum();

        nickname = pokemon.getNickname();
        level = pokemon.getLevel();
        xp = pokemon.getXp();
        health = pokemon.getHealth();

        moves = new ArrayList<Integer>(pokemon.getMoves());
        pps = new ArrayList<Integer>(pokemon.getPps());
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

    public int getMaxHealth() {
        return BattleUtil.getMaxStat(Stats.HP, pokemonId, level);
    }

    public int getTotalXPBar() {
        return Constant.levelUpXpData.get(level).getXpToNextlevel();
    }

    public int getCurrentXPBar() {
        return xp - Constant.levelUpXpData.get(level).getXp();
    }

    public int getFrontImageResource(Context context) {
        return ImageUtil.getPokemonFrontImageResource(context, pokemonId);
    }

    public int getBackImageResource(Context context) {
        return ImageUtil.getPokemonBackImageResource(context, pokemonId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(usersPokemonId);
        dest.writeInt(usersId);
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

    public boolean isDead() {
        return getHealth() <= 0;
    }

    public void resetPps() {
        pps = new ArrayList<Integer>();
        for (Integer ele : moves) {
            if (ele == null) {
                pps.add(null);
            } else {
                pps.add(Moves.getMaxPp(ele));
            }
        }
    }

    public BattlingPokemon toBattlingPokemon() {
        return new BattlingPokemon(this);
    }

    @Override
    public int compareTo(UsersPokemon other) {
        return Integer.compare(this.getSlotNum(), other.getSlotNum());
    }
}
