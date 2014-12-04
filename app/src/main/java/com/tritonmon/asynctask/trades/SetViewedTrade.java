package com.tritonmon.asynctask.trades;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ListUtil;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.Trade;

import org.apache.http.HttpResponse;

import java.util.List;

public class SetViewedTrade extends AsyncTask<Void, Void, Boolean> {

    public enum Choices {
        DECLINED,
        NEUTRAL,
        ACCEPTED
    }

    String offererUsername;
    int offererPokemon;
    String listerUsername;
    int listerPokemon;
    Choices choice;

    public SetViewedTrade(Trade trade, Choices choice) {
        offererUsername = trade.getOfferer();
        offererPokemon = trade.getOfferUsersPokemonId();
        listerUsername = trade.getLister();
        listerPokemon = trade.getListerUsersPokemonId();
        this.choice = choice;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/SetViewedTrade", "STARTED ASYNC TASK");
        Log.d("asynctask/SetViewedTrade", "Sending offerer " + offererUsername + ", Sending lister " + listerUsername + ", choice: " + choice + " to server");
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

        url+= "/" + Constant.encode(offererUsername)
            + "/" + Integer.toString(offererPokemon)
            + "/" + Constant.encode(listerUsername)
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
