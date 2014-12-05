package com.tritonmon.asynctask.trades;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.MyGson;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.Trade;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

public class GetTrades extends AsyncTask<Void, Void, Boolean> {

    private String username;
    private Integer usersId;

    public GetTrades() {
        if (CurrentUser.isLoggedIn()) {
            this.username = CurrentUser.getUsername();
            this.usersId = CurrentUser.getUsersId();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        if (CurrentUser.isLoggedIn()) {

            Log.d("asynctask/GetTrades", "STARTED ASYNC TASK");
            List<Trade> tmp = new ArrayList<Trade>();
            List<Trade> trades = null;
            HttpResponse response = MyHttpClient.get(Constant.SERVER_URL + "/getalltradingin" + "/" + Integer.toString(usersId));
            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                String json = MyHttpClient.getJson(response);
                trades = MyGson.getInstance().fromJson(json, new TypeToken<List<Trade>>() {
                }.getType());

                if (trades != null) {
                    tmp.addAll(trades);
                }
            }
            trades = null;
            response = MyHttpClient.get(Constant.SERVER_URL + "/getalloffersout" + "/" + Integer.toString(usersId));
            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                String json = MyHttpClient.getJson(response);
                trades = MyGson.getInstance().fromJson(json, new TypeToken<List<Trade>>() {
                }.getType());

                if (trades != null) {
                    tmp.addAll(trades);
                }
            }
            CurrentUser.setTrades(tmp);

        }
//        CurrentUser.setUsersTradingIn(populateTradeInfo(Constant.SERVER_URL + "/gettradingin" + "/" + Constant.encode(username)));
//        CurrentUser.setUsersOfferingOut(populateTradeInfo(Constant.SERVER_URL + "/getofferingout" + "/" + Constant.encode(username)));
//        CurrentUser.setUnseenUsersTradingIn(populateTradeInfo(Constant.SERVER_URL + "/getunseentradingin" + "/" + Constant.encode(username)));
//        CurrentUser.setUnseenDeclinedUsersTradingIn(populateTradeInfo(Constant.SERVER_URL + "/getunseendeclinedtradingin" + "/" + Constant.encode(username)));
        return true;
    }

//    public List<String> populateTradeInfo(String url) {
//        Log.e("PopulateTradeInfo", url);
//        HttpResponse response = MyHttpClient.get(url);
//        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
//            String json = MyHttpClient.getJson(response);
//            List<String> tradeInfo = MyGson.getInstance().fromJson(json, new TypeToken<List<String>>() {
//            }.getType());
//
//            if (tradeInfo != null && !tradeInfo.isEmpty()) {
//                Log.e("tradeinfo is not null", tradeInfo.toString());
//                return tradeInfo;
//            }
//        }
//        return new ArrayList<String>();
//
//    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/GetTrades", "FINISHED ASYNC TASK");
    }

}
