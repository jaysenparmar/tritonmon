package com.tritonmon.global;

import android.media.AudioManager;
import android.util.Log;

import com.tritonmon.asynctask.user.GetUpdatedUsersPokemonTask;
import com.tritonmon.model.PokemonParty;
import com.tritonmon.model.User;
import com.tritonmon.model.UsersPokemon;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class CurrentUser {
    private static User user = null;
    private static PokemonParty pokemonParty = null;
    private static List<UsersPokemon> pokemonStash = null;
    private static AudioManager soundGuy = null;
    private static boolean soundEnabled = true;

    // all who have challenged the user
    @Getter
    @Setter
    private static List<String> usersChallengers = new ArrayList<String>();

    // who the user is challenging
    @Getter
    @Setter
    private static List<String> usersChallenging = new ArrayList<String>();

    // unseen people who challenged the user
    @Getter
    @Setter
    private static List<String> unseenUsersChallengers = new ArrayList<String>();


    // who has declined the user's challenge
    @Getter
    @Setter
    private static List<String> unseenDeclinedUsersChallengers = new ArrayList<String>();

    public CurrentUser() {

    }

    public static void setUser(User u) {
        user = u;
        pokemonParty = new PokemonParty();
        pokemonStash = new ArrayList<UsersPokemon>();
        new GetUpdatedUsersPokemonTask().execute();
        Log.d("CurrentUser", user.getUsername() + " logged in");
    }

    public static String getUsername() {
        return (user != null) ? user.getUsername() : null;
    }

    public static int getUsersId() {
        return user.getUsersId();
    }

    public static User getUser() {
        return user;
    }

    public static boolean isLoggedIn() {
        return user != null;
    }

    public static void logout() {
        Log.d("CurrentUser", user.getUsername() + " logged out");
        user = null;
        pokemonParty = null;
        pokemonStash = null;
    }

    public static PokemonParty getPokemonParty() {
        return pokemonParty;
    }

    public static void setPokemonParty(PokemonParty party) {
        pokemonParty = party;
    }

    public static void setPokemonParty(List<UsersPokemon> pokemonList) {
        pokemonParty.setPokemonList(pokemonList);
    }

    public static void clearPokemonParty() {
        pokemonParty.getPokemonList().clear();
    }

    public static List<UsersPokemon> getPokemonStash() {
        return pokemonStash;
    }

    public static void setPokemonStash(List<UsersPokemon> pokemonList) {
        pokemonStash = pokemonList;
    }

    public static void clearPokemonStash() {
        pokemonStash.clear();
    }

    public static void updatePokemon() {
        new GetUpdatedUsersPokemonTask().execute();
    }

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static void setSoundEnabled(boolean thing) {
        soundEnabled = thing;
    }

    public static AudioManager getSoundGuy() {
        return soundGuy;
    }

    public static void setSoundGuy(AudioManager thing2){
        soundGuy = thing2;
    }
}
