package com.tritonmon.asynctask;


import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.singleton.MyGson;
import com.tritonmon.global.singleton.MyHttpClient;
import com.tritonmon.model.User;
import com.tritonmon.model.UsersPokemon;

import org.apache.http.HttpResponse;

import java.util.List;

public class AsyncUtil {

    public static String getUserJson(String username, boolean isFacebookUser) {
        String url;
        if (isFacebookUser) {
            url = Constant.SERVER_URL + "/getfacebookuser/" + username;
        }
        else {
            url = Constant.SERVER_URL + "/getuser/" + Constant.encode(username);
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
        CurrentUser.setPokemon(allPokemon);
    }
}
