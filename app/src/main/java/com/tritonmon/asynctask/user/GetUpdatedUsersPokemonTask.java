package com.tritonmon.asynctask.user;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.asynctask.AsyncUtil;
import com.tritonmon.global.CurrentUser;

public class GetUpdatedUsersPokemonTask extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... params) {
        Log.d("asynctask/GetUpdatedUsersPokemonTask", "STARTED ASYNC TASK");
        Log.d("asynctask/GetUpdatedUsersPokemonTask", "Getting user " + CurrentUser.getUsername() + "'s Pokemon from server");
        return AsyncUtil.getUsersPokemonJson(CurrentUser.getUsersId());
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
