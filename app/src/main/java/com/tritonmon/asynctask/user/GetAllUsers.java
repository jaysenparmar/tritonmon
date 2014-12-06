package com.tritonmon.asynctask.user;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.tritonmon.asynctask.AsyncUtil;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.MyGson;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.Trade;
import com.tritonmon.model.User;

import org.apache.http.HttpResponse;

import java.util.List;

public class GetAllUsers extends AsyncTask<Void, Void, List<User>> {

    @Override
    protected List<User> doInBackground(Void... params) {
        Log.d("asynctask/GetAllUsers", "STARTED ASYNC TASK");
        Log.d("asynctask/GetAllUsers", "Getting user " + CurrentUser.getUsername() + "'s user info from server");
        HttpResponse response = MyHttpClient.get(Constant.SERVER_URL + "/getallusers");
        String json = MyHttpClient.getJson(response);
        return MyGson.getInstance().fromJson(json, new TypeToken<List<User>>() {
        }.getType());
    }

    @Override
    protected void onPostExecute(List<User> result) {
        for (User ele : result) {
            Constant.userData.put(ele.getUsersId(), ele);
        }
        Log.d("asynctask/GetAllUsers", "FINISHED ASYNC TASK");
    }
}
