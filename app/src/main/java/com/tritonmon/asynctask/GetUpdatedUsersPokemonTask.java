package com.tritonmon.asynctask;

import android.os.AsyncTask;
import android.util.Log;

public class GetUpdatedUsersPokemonTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        Log.d("asynctask/GetUpdatedUsersPokemonTask", "STARTED ASYNC TASK");
        Log.d("asynctask/GetUpdatedUsersPokemonTask", "Getting user " + params[0] + "'s Pokemon from server");
        return AsyncUtil.getUsersPokemonJson(params[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            Log.e("asynctask/GetUpdatedUsersPokemonTask", "usersPokemon JSON from server was null");
            return;
        }
        else if (result.isEmpty()) {
            Log.e("asynctask/GetUpdatedUsersPokemonTask", "usersPokemon JSON from server was empty");
            return;
        }

        AsyncUtil.updateCurrentUsersPokemon(result);
        Log.d("asynctask/GetUpdatedUsersPokemonTask", "FINISHED ASYNC TASK");
    }
}
