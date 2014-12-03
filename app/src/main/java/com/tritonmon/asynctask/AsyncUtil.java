package com.tritonmon.asynctask;


import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.tritonmon.exception.PartyException;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.MyGson;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.User;
import com.tritonmon.model.UsersPokemon;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AsyncUtil {

    public static String getUserJson(String username, boolean isFacebookUser) {
        String url;
        if (isFacebookUser) {
            url = Constant.SERVER_URL + "/getuser/" + Constant.encode(username);
        }
        else {
            url = Constant.SERVER_URL + "/getfacebookuser/" + username;
        }

        HttpResponse response = MyHttpClient.get(url);
        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            return MyHttpClient.getJson(response);
        }

        return null;
    }

    public static void updateCurrentUser(String userJson) {
        List<User> serverUsers = MyGson.getInstance().fromJson(userJson,
                new TypeToken<List<User>>() {}.getType());

        if (serverUsers.size() != 1) {
            Log.e("asynctask/AsyncUtil", "Server returned " + serverUsers.size() + " users");
        }

        CurrentUser.setUser(serverUsers.get(0));
    }

    public static String getUsersPokemonJson(int usersId) {
        String url = Constant.SERVER_URL + "/userspokemon/users_id=" + usersId;
        HttpResponse response = MyHttpClient.get(url);

        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            return MyHttpClient.getJson(response);
        }

        return null;
    }
    
    public static void updateCurrentUsersPokemon(String usersPokemonJson) {
        if (usersPokemonJson == null || usersPokemonJson.isEmpty()) {
            Log.e("asynctask/AsyncUtil", "User " + CurrentUser.getUsername() + " does not have any UsersPokemon on the server");
            return;
        }

        List<UsersPokemon> allPokemon = MyGson.getInstance().fromJson(usersPokemonJson,
                new TypeToken<List<UsersPokemon>>() {}.getType());
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
                CurrentUser.getPokemonParty().add(pokemon.getSlotNum(), pokemon);
            } catch (PartyException e) {
                Log.e("asynctask/AsyncUtil", "Error when adding " + pokemon.getName() + " to user " + CurrentUser.getUsername() + "'s party");
                e.printStackTrace();
            }
        }
    }
}
