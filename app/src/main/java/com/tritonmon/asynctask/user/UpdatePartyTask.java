package com.tritonmon.asynctask.user;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.model.UsersPokemon;

import java.util.List;

public class UpdatePartyTask extends AsyncTask<List<UsersPokemon>, Void, Boolean> {
    @Override
    protected Boolean doInBackground(List<UsersPokemon>... lists) {
        return null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/UpdatePartyTask", "FINISHED ASYNC TASK");
    }
}
