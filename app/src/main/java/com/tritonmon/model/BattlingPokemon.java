package com.tritonmon.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.tritonmon.battle.BattleUtil;
import com.tritonmon.battle.handler.XPHandler;
import com.tritonmon.global.Constant;
import com.tritonmon.staticmodel.MoveMetaAilments;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class BattlingPokemon extends UsersPokemon implements Parcelable {

    private boolean wild;

    // init all values to 0, keys from 1-8
    private Map<Integer, Integer> statsStages;

//    private int hp;
    private int attack;
    private int defense;
    private int specialAttack;
    private int specialDefense;
    private int speed;
    private int accuracy;
    private int evasion;

    // possibly change to int
    private String status;
    private int statusTurn;
    private int maxTurns;

    // method to recreate a UserPokemon from a Parcel
    public static Creator<BattlingPokemon> CREATOR = new Creator<BattlingPokemon>() {

        @Override
        public BattlingPokemon createFromParcel(Parcel source) {
            return new BattlingPokemon(source);
        }

        @Override
        public BattlingPokemon[] newArray(int size) {
            return new BattlingPokemon[size];
        }
    };

    public BattlingPokemon(Parcel parcel) {
        super(parcel);

        wild = parcel.readByte()!=0;
        statsStages = new HashMap<Integer, Integer>();
        parcel.readMap(statsStages, null);
        attack = parcel.readInt();
        defense = parcel.readInt();
        specialAttack = parcel.readInt();
        specialDefense = parcel.readInt();
        speed = parcel.readInt();
        accuracy = parcel.readInt();
        evasion = parcel.readInt();

        status = parcel.readString();
        statusTurn = parcel.readInt();
        maxTurns = parcel.readInt();

    }

    public BattlingPokemon(int pokemonId, int level, int xp, boolean wild, Map<Integer, Integer> statStages, int hp, int attack, int defense,
                           int specialAttack, int specialDefense, int speed, int accuracy, int evasion,
                           List<Integer> moves, List<Integer> pps, String status, int statusTurn, int maxTurns) {
        this.pokemonId = pokemonId;
        this.level = level;
        this.xp = xp;
        this.moves  = moves;
        this.pps = pps;

        this.wild = wild;
        this.statsStages = statStages;
        this.health = hp;
        this.attack = attack;
        this.defense = defense;
        this.specialAttack = specialAttack;
        this.specialDefense = specialDefense;
        this.speed = speed;
        this.accuracy = accuracy;
        this.evasion = evasion;
        this.status = status;
        this.statusTurn = statusTurn;
        this.maxTurns = maxTurns;
    }

    public BattlingPokemon(int pokemonId, int level, boolean wild) {
        this.pokemonId = pokemonId;
        this.level = level;
        this.wild = wild;

        // shouldnt be a case where this goes to the else.. or something is wrong
        if (wild) {
            Map<String, Integer> allStats = BattleUtil.getAllMaxStats(pokemonId, level);

            Map<Integer, Integer> statStages = new HashMap<Integer, Integer>();
            for (int i = 1; i < 9; i++) {
                statStages.put(i, 0);
            }

            List<Integer> moves = BattleUtil.generateMoves(XPHandler.getNewMovesWithLevel(pokemonId, 0, level));

            // if moves is [null, null, null, null], set move1 to splash
            boolean allNull = true;
            for (int i=0; i<moves.size(); i++) {
                if (moves.get(i) != null) {
                    allNull = false;
                }
            }
            if (allNull) {
                moves.set(0, Constant.SPLASH_MOVEID);
            }
            this.moves = moves;

            this.pps = Arrays.asList(999, 999, 999, 999);
            this.xp = XPHandler.baseXpAtLevel(level);

            this.statsStages = statStages;
            this.health = allStats.get("hp");
            this.attack = allStats.get("attack");
            this.defense = allStats.get("defense");
            this.specialAttack = allStats.get("special-attack");
            this.specialDefense = allStats.get("special-defense");
            this.speed = allStats.get("speed");
            this.accuracy = allStats.get("accuracy");
            this.evasion = allStats.get("evasion");
            this.status = "none";
            this.statusTurn = 0;
            this.maxTurns = 0;

        }

    }

    // not sure if need this one. will figure out when implementing swapping
    public BattlingPokemon(UsersPokemon usersPokemon) {
        this.usersPokemonId = usersPokemon.getUsersPokemonId();
        this.usersId = usersPokemon.getUsersId();
        this.pokemonId = usersPokemon.getPokemonId();
        this.slotNum = usersPokemon.getSlotNum();
        this.nickname = usersPokemon.getNickname();
        this.level = usersPokemon.getLevel();
        this.xp = usersPokemon.getXp();
        this.moves  = usersPokemon.getMoves();
        this.pps = usersPokemon.getPps();

        this.wild = false;
//        Log.e("Battling Pokemon", Integer.toString(usersPokemon.getPokemonId()) + ", " + Integer.toString(usersPokemon.getLevel()));
        Map<String, Integer> allStats = BattleUtil.getAllMaxStats(usersPokemon.getPokemonId(), usersPokemon.getLevel());
        Map<Integer, Integer> statStages = new HashMap<Integer, Integer>();
        for (int i = 1; i < 9; i++) {
            statStages.put(i, 0);
        }

        this.statsStages = statStages;
        this.health = usersPokemon.getHealth();
        this.attack = allStats.get("attack");
        this.defense = allStats.get("defense");
        this.specialAttack = allStats.get("special-attack");
        this.specialDefense = allStats.get("special-defense");
        this.speed = allStats.get("speed");
        this.accuracy = allStats.get("accuracy");
        this.evasion = allStats.get("evasion");
        this.status = "none";
        this.statusTurn = 0;
        this.maxTurns = 0;
    }

//
//    // not sure if need this one. will figure out when implementing swapping
//    public BattlingPokemon(UsersPokemon usersPokemon, boolean wild, Map<Integer, Integer> statStages, int attack, int defense,
//                           int specialAttack, int specialDefense, int speed, int accuracy, int evasion,
//                           String status, int statusTurn) {
//        this.usersPokemonId = usersPokemon.getUsersPokemonId();
//        this.username = usersPokemon.getUsername();
//        this.pokemonId = usersPokemon.getPokemonId();
//        this.slotNum = usersPokemon.getSlotNum();
//        this.nickname = usersPokemon.getNickname();
//        this.level = usersPokemon.getLevel();
//        this.xp = usersPokemon.getXp();
//        this.moves  = usersPokemon.getMoves();
//        this.pps = usersPokemon.getPps();
//
//        this.wild = wild;
//        this.statsStages = statStages;
//        this.health = usersPokemon.getHealth();
//        this.attack = attack;
//        this.defense = defense;
//        this.specialAttack = specialAttack;
//        this.specialDefense = specialDefense;
//        this.speed = speed;
//        this.accuracy = accuracy;
//        this.evasion = evasion;
//        this.status = status;
//        this.statusTurn = statusTurn;
//    }

    public UsersPokemon toUsersPokemon() {
        return new UsersPokemon(
                super.getUsersPokemonId(),
                super.getUsersId(),
                super.getPokemonId(),
                super.getSlotNum(),
                super.getNickname(),
                super.getLevel(),
                super.getXp(),
                super.getHealth(),
                super.getMoves(),
                super.getPps()
        );
    }

    public void clearStatus() {
        this.setStatus(MoveMetaAilments.NONE);
        this.setStatusTurn(0);
        this.setMaxTurns(0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        super.writeToParcel(dest, flags);

        // i assume this works
        byte wildByte = wild ? (byte) 1 : 0;
        dest.writeByte(wildByte);
        dest.writeMap(statsStages);
        dest.writeInt(attack);
        dest.writeInt(defense);
        dest.writeInt(specialAttack);
        dest.writeInt(specialDefense);
        dest.writeInt(speed);
        dest.writeInt(accuracy);
        dest.writeInt(evasion);
        dest.writeString(status);
        dest.writeInt(statusTurn);
        dest.writeInt(maxTurns);

    }
}
