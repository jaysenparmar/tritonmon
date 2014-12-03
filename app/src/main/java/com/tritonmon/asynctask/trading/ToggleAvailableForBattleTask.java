package com.tritonmon.asynctask.trading;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ListUtil;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.UsersPokemon;

import org.apache.http.HttpResponse;

public class ToggleAvailableForBattleTask extends AsyncTask<Void, Void, Boolean> {

    Boolean available;
    String username;

    public ToggleAvailableForBattleTask(boolean available, String username) {
        this.available = available;
        this.username = username;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/ToggleAvailableForBattleTask", "STARTED ASYNC TASK");

        String url = Constant.SERVER_URL + "/toggleavailable"
                + "/" + available.toString()
                + "/" + Constant.encode(username);

        HttpResponse response = MyHttpClient.post(url);
        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            return true;
        }
        Log.e("asynctask/ToggleAvailableForBattleTask", response.getStatusLine().toString());
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/UpdateAfterBattleTask", "FINISHED ASYNC TASK");
    }
}
