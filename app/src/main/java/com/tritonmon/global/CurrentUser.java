package com.tritonmon.global;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.tritonmon.exception.PartyException;
import com.tritonmon.model.Party;
import com.tritonmon.model.User;
import com.tritonmon.model.UsersPokemon;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

public class CurrentUser {
    private static User user = null;
    private static Party party = null;
    private static List<UsersPokemon> stashedPokemon = null;

    public CurrentUser() {

    }

    public static void setUser(User u) {
        user = u;
        party = new Party();
        stashedPokemon = new ArrayList<UsersPokemon>();
        new UpdatePokemon().execute(u.getUsername());
    }

    public static User getUser() {
        return user;
    }

    public static boolean isLoggedIn() {
        return user != null;
    }

    public static void logout() {
        user = null;
        party = null;
        stashedPokemon = null;
    }

    public static Party getParty() {
        return party;
    }

    public static List<UsersPokemon> getStashedPokemon() {
        return stashedPokemon;
    }

    private static class UpdatePokemon extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String url = Constant.SERVER_URL + "/userspokemon/" + Constant.encode(params[0]);
            HttpResponse response = MyHttpClient.get(url);

            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                return MyHttpClient.getJson(response);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null || result.isEmpty()) {
                Log.e("CurrentUser", user.getUsername() + " does not have any Pokemon");
                return;
            }

            Log.d("CurrentUser", "Updating CurrentUser " + user.getUsername() + "'s party and stashedPokemon");

            List<UsersPokemon> allPokemon = MyGson.getInstance().fromJson(result, new TypeToken<List<UsersPokemon>>() {}.getType());
            for (UsersPokemon pokemon : allPokemon) {
                if (pokemon.getSlotNum() >= 0) {
                    try {
                        party.add(pokemon.getSlotNum(), pokemon);
                    }
                    catch (PartyException e) {
                        Log.e("CurrentUser", "Error when adding " + user.getUsername() + "'s Pokemon to party");
                        e.printStackTrace();
                    }
                }
                else {
                    stashedPokemon.add(pokemon);
                }
            }
        }
    }
}
