package com.tritonmon.asynctask;

import android.os.AsyncTask;
import android.util.Log;

public class GetUpdatedUserOnlyTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        Log.d("asynctask/GetUpdatedUserOnlyTask", "STARTED ASYNC TASK");
        Log.d("asynctask/GetUpdatedUserOnlyTask", "Getting user " + params[0] + "'s user info from server");
        return AsyncUtil.getUserJson(params[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            Log.e("asynctask/GetUpdatedUserOnlyTask", "user JSON from server was null");
            return;
        }
        else if (result.isEmpty()) {
            Log.e("asynctask/GetUpdatedUserOnlyTask", "user JSON from server was empty");
            return;
        }

        AsyncUtil.updateCurrentUser(result);
        Log.d("asynctask/GetUpdatedUserOnlyTask", "FINISHED ASYNC TASK");
    }
}
