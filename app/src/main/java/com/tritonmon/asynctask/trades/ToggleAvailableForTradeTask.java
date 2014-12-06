package com.tritonmon.asynctask.trades;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.global.singleton.MyHttpClient;

import org.apache.http.HttpResponse;

public class ToggleAvailableForTradeTask extends AsyncTask<Void, Void, Boolean> {

    Boolean available;
    int users_id;

    public ToggleAvailableForTradeTask(boolean available, int users_id) {
        this.available = available;
        this.users_id = users_id;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/ToggleAvailableForTradeTask", "STARTED ASYNC TASK");

        String url = Constant.SERVER_URL + "/toggleavailable"
                + "/" + available.toString()
                + "/" + Integer.toString(users_id);

        HttpResponse response = MyHttpClient.post(url);
        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            return true;
        }
        Log.e("asynctask/ToggleAvailableForTradeTask", response.getStatusLine().toString());
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/UpdateAfterTradeTask", "FINISHED ASYNC TASK");
    }
}
