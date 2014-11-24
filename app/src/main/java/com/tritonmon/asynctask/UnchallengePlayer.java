package com.tritonmon.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.global.ListUtil;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.UsersPokemon;

import org.apache.http.HttpResponse;

public class UnchallengePlayer extends AsyncTask<Void, Void, Boolean> {

    String challengerUsername;
    String challengedUsername;

    public UnchallengePlayer(String challengerUsername, String challengedUsername) {
        this.challengerUsername = challengerUsername;
        this.challengedUsername = challengedUsername;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/UnchallengePlayer", "STARTED ASYNC TASK");
        Log.d("asynctask/UnchallengePlayer", "Sending user " + challengerUsername + " challenged " + challengedUsername + " to server");

        String url = Constant.SERVER_URL + "/unchallenge"
                + "/" + Constant.encode(challengerUsername)
                + "/" + Constant.encode(challengedUsername);

        HttpResponse response = MyHttpClient.post(url);
        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            return true;
        }

        Log.e("asynctask/UnchallengePlayer", response.getStatusLine().toString());
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/ChallengePlayer", "FINISHED ASYNC TASK");
    }

}
