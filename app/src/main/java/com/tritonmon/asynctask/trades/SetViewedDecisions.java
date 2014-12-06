package com.tritonmon.asynctask.trades;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.global.singleton.MyHttpClient;

import org.apache.http.HttpResponse;

public class SetViewedDecisions extends AsyncTask<Void, Void, Boolean> {

    private int offererUsersId;

    public SetViewedDecisions(int offererUsersId) {
        this.offererUsersId = offererUsersId;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/SetViewedDecisions", "STARTED ASYNC TASK");
        Log.d("asynctask/SetViewedDecisions", "Sending offererUsersId " + offererUsersId + " to server");

        String url = Constant.SERVER_URL + "/setseendecisions/" + offererUsersId;
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
