package com.tritonmon.asynctask.user;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.asynctask.AsyncUtil;
import com.tritonmon.global.CurrentUser;

public class GetUpdatedUserTask extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... params) {
        Log.d("asynctask/GetUpdatedUserTask", "STARTED ASYNC TASK");
        Log.d("asynctask/GetUpdatedUserTask", "Getting user " + CurrentUser.getUsername() + "'s user info from server");
        return AsyncUtil.getUserJson(CurrentUser.getUsername(), CurrentUser.isFacebookUser());
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            Log.e("asynctask/GetUpdatedUserTask", "user JSON from server was null");
            return;
        }
        else if (result.isEmpty()) {
            Log.e("asynctask/GetUpdatedUserTask", "user JSON from server was empty");
            return;
        }

        AsyncUtil.updateCurrentUser(result);
        Log.d("asynctask/GetUpdatedUserTask", "FINISHED ASYNC TASK");
    }
}
