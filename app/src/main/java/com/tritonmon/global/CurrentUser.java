package com.tritonmon.global;

import android.util.Log;

import com.tritonmon.asynctask.GetUpdatedUsersPokemonTask;
import com.tritonmon.model.PokemonParty;
import com.tritonmon.model.User;
import com.tritonmon.model.UsersPokemon;

import java.util.ArrayList;
import java.util.List;

public class CurrentUser {
    private static User user = null;
    private static PokemonParty pokemonParty = null;
    private static List<UsersPokemon> pokemonStash = null;

    public CurrentUser() {

    }

    public static void setUser(User u) {
        user = u;
        pokemonParty = new PokemonParty();
        pokemonStash = new ArrayList<UsersPokemon>();
        new GetUpdatedUsersPokemonTask().execute(u.getUsername());
        Log.d("CurrentUser", user.getUsername() + " logged in");
    }

    public static String getUsername() {
        return (user != null) ? user.getUsername() : null;
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
        new GetUpdatedUsersPokemonTask().execute(user.getUsername());
    }
}
