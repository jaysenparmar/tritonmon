package com.tritonmon.global;

import android.location.Location;
import android.util.Log;

import com.tritonmon.asynctask.user.GetUpdatedUsersPokemonTask;
import com.tritonmon.exception.PartyException;
import com.tritonmon.model.PokemonParty;
import com.tritonmon.model.Trade;
import com.tritonmon.model.User;
import com.tritonmon.model.UsersPokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class CurrentUser {
    private static User user = null;
    private static PokemonParty pokemonParty = null;
    private static List<UsersPokemon> pokemonStash = null;

    @Getter
    @Setter
    private static List<Trade> trades = new ArrayList<Trade>();

    @Getter
    @Setter
    public static String currentCity = "";

    @Getter
    @Setter
    public static Location currentLocation;

    public CurrentUser() {

    }

    public static void setUser(User u) {
        user = u;
        pokemonParty = new PokemonParty();
        pokemonStash = new ArrayList<UsersPokemon>();
        new GetUpdatedUsersPokemonTask().execute();
        Log.d("CurrentUser", user.getUsername() + " logged in");
    }

    public static String getName() {
        return user.getName();
    }

    public static String getUsername() {
        return user.getUsername();
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

    public static boolean isFacebookUser() {
        return user.isFacebookUser();
    }

    public static void logout() {
        Log.d("CurrentUser", "CurrentUser logged out");
        user = null;
        pokemonParty = null;
        pokemonStash = null;
    }

    public static void setPokemon(List<UsersPokemon> allPokemon) {
        CurrentUser.clearPokemonParty();
        CurrentUser.clearPokemonStash();

        List<UsersPokemon> party = new ArrayList<UsersPokemon>();
        for (UsersPokemon pokemon : allPokemon) {
            if (pokemon.getSlotNum() >= 0) {
                party.add(pokemon);
            }
            else {
                CurrentUser.getPokemonStash().add(pokemon);
            }
        }

        Collections.sort(party);
        for (UsersPokemon pokemon : party) {
            try {
                if (CurrentUser.getPokemonParty() == null) {
                    CurrentUser.setPokemonParty(new PokemonParty());
                }
                CurrentUser.getPokemonParty().add(pokemon.getSlotNum(), pokemon);
            } catch (PartyException e) {
                Log.e("CurrentUser/setPokemon", "Error when adding " + pokemon.getName() + " to user " + CurrentUser.getUsername() + "'s party");
                e.printStackTrace();
            }
        }
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
        if (pokemonParty != null) {
            pokemonParty.getPokemonList().clear();
        }
    }

    public static List<UsersPokemon> getPokemonStash() {
        return pokemonStash;
    }

    public static void setPokemonStash(List<UsersPokemon> pokemonList) {
        pokemonStash = pokemonList;
    }

    public static void clearPokemonStash() {
        if (pokemonStash != null) {
            pokemonStash.clear();
        }
    }

    public static void updatePokemon() {
        new GetUpdatedUsersPokemonTask().execute();
    }

}
