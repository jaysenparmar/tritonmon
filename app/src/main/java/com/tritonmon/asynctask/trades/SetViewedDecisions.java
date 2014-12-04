package com.tritonmon.asynctask.trades;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ListUtil;
import com.tritonmon.global.MyHttpClient;

import org.apache.http.HttpResponse;

import java.util.List;

public class SetViewedDecisions extends AsyncTask<Void, Void, Boolean> {

    private String offererUsername;

    public SetViewedDecisions(String offererUsername) {
        this.offererUsername = offererUsername;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/SetViewedDecisions", "STARTED ASYNC TASK");
        Log.d("asynctask/SetViewedDecisions", "Sending offererUsersId " + offererUsername + " to server");

        String url = Constant.SERVER_URL + "/setseendecisions/" + offererUsername;
        HttpResponse response = MyHttpClient.post(url);
        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            url = Constant.SERVER_URL + "/removeseendecisions";
            response = MyHttpClient.post(url);
            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                return true;
            }
        }
        Log.e("asynctask/SetViewedDecisions", response.getStatusLine().toString());
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/SetViewedDecisions", "FINISHED ASYNC TASK");
    }


}
