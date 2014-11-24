package com.tritonmon.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ListUtil;
import com.tritonmon.global.MyGson;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.PVPUser;
import com.tritonmon.model.UsersPokemon;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetChallenges extends AsyncTask<Void, Void, Boolean> {

    private String username;

    public GetChallenges() {
        this.username = CurrentUser.getUsername();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        Log.d("asynctask/GetChallengers", "STARTED ASYNC TASK");
        List<String> tmp = null;

        CurrentUser.setUsersChallengers(populateChallengeInfo(Constant.SERVER_URL + "/getchallengers" + "/" + Constant.encode(username)));
        CurrentUser.setUsersChallenging(populateChallengeInfo(Constant.SERVER_URL + "/getchallengings" + "/" + Constant.encode(username)));
        CurrentUser.setUnseenUsersChallengers(populateChallengeInfo(Constant.SERVER_URL + "/getunseenchallengers" + "/" + Constant.encode(username)));
        CurrentUser.setUnseenDeclinedUsersChallengers(populateChallengeInfo(Constant.SERVER_URL + "/getunseendeclinedchallengers" + "/" + Constant.encode(username)));
        return true;
    }

    public List<String> populateChallengeInfo(String url) {
        Log.e("PopulateChallengeInfo", url);
        HttpResponse response = MyHttpClient.get(url);
        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            String json = MyHttpClient.getJson(response);
            List<String> challengeInfo = MyGson.getInstance().fromJson(json, new TypeToken<List<String>>() {
            }.getType());

            if (challengeInfo != null && !challengeInfo.isEmpty()) {
                Log.e("challengeinfo is not null", challengeInfo.toString());
                return challengeInfo;
            }
        }
        return new ArrayList<String>();

    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/GetChallengers", "FINISHED ASYNC TASK");
    }

}
