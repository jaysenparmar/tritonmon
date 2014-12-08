package com.tritonmon.asynctask.user;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.model.Trade;
import com.tritonmon.global.singleton.MyHttpClient;

import org.apache.http.HttpResponse;

import java.util.List;

public class UpdateAvatar extends AsyncTask<Void, Void, Boolean> {

    private String avatar;

    public UpdateAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/UpdateAvatar", "STARTED ASYNC TASK");
        String url = Constant.SERVER_URL + "/changeavatar/users_id="+Integer.toString(CurrentUser.getUsersId())+"/avatar="+avatar;
        HttpResponse response = MyHttpClient.post(url);
        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/UpdateAvatar", "FINISHED ASYNC TASK");
        if (!result) {
            Log.d("asynctask/UpdateAvatar", "ERROR");
        }
    }


}
