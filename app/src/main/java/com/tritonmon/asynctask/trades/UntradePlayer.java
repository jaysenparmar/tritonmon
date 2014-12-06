package com.tritonmon.asynctask.trades;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.global.ListUtil;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.UsersPokemon;

import org.apache.http.HttpResponse;


// DEPRECATED. MIGHT USE IN FUTURE
public class UntradePlayer extends AsyncTask<Void, Void, Boolean> {

    String offererUsername;
    String listerUsername;

    public UntradePlayer(String offererUsername, String listerUsername) {
        this.offererUsername = offererUsername;
        this.listerUsername = listerUsername;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/UntradePlayer", "STARTED ASYNC TASK");
        Log.d("asynctask/UntradePlayer", "Sending user " + offererUsername + " listerUsersId " + listerUsername + " to server");

        String url = Constant.SERVER_URL + "/untrade"
                + "/" + Constant.encode(offererUsername)
                + "/" + Constant.encode(listerUsername);

        HttpResponse response = MyHttpClient.post(url);
        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            return true;
        }

        Log.e("asynctask/UntradePlayer", response.getStatusLine().toString());
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/UntradePlayer", "FINISHED ASYNC TASK");
    }

}
