package com.tritonmon.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ListUtil;
import com.tritonmon.global.MyHttpClient;

import org.apache.http.HttpResponse;

import java.util.List;

public class SetViewedDecline extends AsyncTask<Void, Void, Boolean> {

    private String challengerUsername;

    public SetViewedDecline(String challengerUsername) {
        this.challengerUsername = challengerUsername;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/SetViewedDecline", "STARTED ASYNC TASK");
        Log.d("asynctask/SetViewedDecline", "Sending challenger " + challengerUsername + " to server");

        String url = Constant.SERVER_URL + "/setdeclinedchallenge/" + challengerUsername;
        HttpResponse response = MyHttpClient.post(url);
        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            url = Constant.SERVER_URL + "/removeseendeclinedchallenges";
            response = MyHttpClient.post(url);
            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                return true;
            }
        }
        Log.e("asynctask/SetViewedDecline", response.getStatusLine().toString());
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/SetViewedDecline", "FINISHED ASYNC TASK");
    }


}
