package com.tritonmon.asynctask;

import android.os.AsyncTask;
import android.util.Log;

public class GetUpdatedUserTask extends AsyncTask<String, Void, Boolean> {

    @Override
    protected Boolean doInBackground(String... params) {
        Log.d("asynctask/GetUpdatedUserTask", "STARTED ASYNC TASK");
        Log.d("asynctask/GetUpdatedUserTask", "Getting user " + params[0] + "'s user info and Pokemon from server");

        String userJson = AsyncUtil.getUserJson(params[0]);
        if (userJson == null) {
            Log.e("asynctask/GetUpdatedUserTask", "user JSON from server was null");
            return false;
        }
        else if (userJson.isEmpty()) {
            Log.e("asynctask/GetUpdatedUserTask", "user JSON from server was empty");
            return false;
        }
        AsyncUtil.updateCurrentUser(userJson);

        String usersPokemonJson = AsyncUtil.getUsersPokemonJson(params[0]);
        if (usersPokemonJson == null) {
            Log.e("asynctask/GetUpdatedUserTask", "usersPokemon JSON from server was null");
            return false;
        }
        else if (usersPokemonJson.isEmpty()) {
            Log.e("asynctask/GetUpdatedUserTask", "usersPokemon JSON from server was empty");
            return false;
        }
        AsyncUtil.updateCurrentUsersPokemon(usersPokemonJson);

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/GetUpdatedUserTask", "FINISHED ASYNC TASK");
    }
}
