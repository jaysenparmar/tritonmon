package com.tritonmon.asynctask.trades;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.global.singleton.MyHttpClient;
import com.tritonmon.model.Trade;

import org.apache.http.HttpResponse;

public class SetViewedTrade extends AsyncTask<Void, Void, Boolean> {

    public enum Choices {
        DECLINED,
        NEUTRAL,
        ACCEPTED
    }

    int offererUsersId;
    int offererPokemon;
    int listerUsersId;
    int listerPokemon;
    Choices choice;

    public SetViewedTrade(Trade trade, Choices choice) {
        offererUsersId = trade.getOffererUsersId();
        offererPokemon = trade.getOfferUsersPokemonId();
        listerUsersId = trade.getListerUsersId();
        listerPokemon = trade.getListerUsersPokemonId();
        this.choice = choice;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/SetViewedTrade", "STARTED ASYNC TASK");
        Log.d("asynctask/SetViewedTrade", "Sending offererUsersId " + offererUsersId + ", Sending listerUsersId " + listerUsersId + ", choice: " + choice + " to server");
        String url = "";
        switch (choice) {
            case DECLINED:
                url = Constant.SERVER_URL + "/setdeclinetrade";
                break;
            case NEUTRAL:
                url = Constant.SERVER_URL + "/setseentrade";
                break;
            case ACCEPTED:
                url = Constant.SERVER_URL + "/setaccepttrade";
                break;
            default:
                return false;
        }

        url+= "/" + Integer.toString(offererUsersId)
            + "/" + Integer.toString(offererPokemon)
            + "/" + Integer.toString(listerUsersId)
            + "/" + Integer.toString(listerPokemon);
        HttpResponse response = MyHttpClient.post(url);
        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            return true;
        }

        Log.e("asynctask/SetViewedTrade", response.getStatusLine().toString());
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/SetViewedTrade", "FINISHED ASYNC TASK");
    }


}
