package com.tritonmon.asynctask.user;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.asynctask.AsyncUtil;
import com.tritonmon.global.CurrentUser;

public class UpdateCurrentUserTask extends AsyncTask<Void, Void, Boolean> {

    private Activity activity;

    public UpdateCurrentUserTask(Activity activity) {
        this.activity = activity;
        activity.setProgressBarIndeterminateVisibility(true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/UpdateCurrentUserTask", "STARTED ASYNC TASK");

        if (CurrentUser.getUser() == null) {
            Log.e("asynctask/UpdateCurrentUserTask", "Called when no user logged in");
        }
        Log.d("asynctask/UpdateCurrentUserTask", "Getting user " + CurrentUser.getUsername() + "'s user info and Pokemon from server");

        String userJson = AsyncUtil.getUserJson(CurrentUser.getUsername(), CurrentUser.getUser().isFacebookUser());
        if (userJson == null) {
            Log.e("asynctask/UpdateCurrentUserTask", "user JSON from server was null");
            return false;
        }
        else if (userJson.isEmpty()) {
            Log.e("asynctask/UpdateCurrentUserTask", "user JSON from server was empty");
            return false;
        }
        AsyncUtil.updateCurrentUser(userJson);

        String usersPokemonJson = AsyncUtil.getUsersPokemonJson(CurrentUser.getUsersId());
        if (usersPokemonJson == null) {
            Log.e("asynctask/UpdateCurrentUserTask", "usersPokemon JSON from server was null");
            return false;
        }
        else if (usersPokemonJson.isEmpty()) {
            Log.e("asynctask/UpdateCurrentUserTask", "usersPokemon JSON from server was empty");
            return false;
        }
        AsyncUtil.updateCurrentUsersPokemon(usersPokemonJson);

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        activity.setProgressBarIndeterminateVisibility(false);
        Log.d("asynctask/UpdateCurrentUserTask", "FINISHED ASYNC TASK");
    }
}
