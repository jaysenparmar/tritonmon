package com.tritonmon.asynctask.trades;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.singleton.MyHttpClient;
import com.tritonmon.model.Trade;

import org.apache.http.HttpResponse;

import java.util.List;

public class SetViewedTrade extends AsyncTask<Void, Void, Boolean> {

    public enum Choices {
        DECLINED,
        NEUTRAL,
        ACCEPTED
    }

    Choices choice;

    Trade trade;

    public SetViewedTrade(Trade trade, Choices choice) {
        this.choice = choice;
        this.trade = trade;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/SetViewedTrade", "STARTED ASYNC TASK");
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

        url+= "/" + Integer.toString(trade.getOffererUsersId())
            + "/" + Integer.toString(trade.getOfferUsersPokemonId())
            + "/" + Integer.toString(trade.getListerUsersId())
            + "/" + Integer.toString(trade.getListerUsersPokemonId());
        HttpResponse response = MyHttpClient.post(url);
        setTradeStatus();
        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            return true;
        }

        Log.e("asynctask/SetViewedTrade", response.getStatusLine().toString());
        return false;
    }

    private void setTradeStatus() {
        List<Trade> tmp = CurrentUser.getTrades();
        for (Trade ele : tmp) {
            if (ele.equals(trade)) {
                switch (choice) {
                    case DECLINED:
                        ele.setDeclined(true);
                        ele.setSeenOffer(true);
                        break;
                    case NEUTRAL:
                        ele.setSeenOffer(true);
                        break;
                    case ACCEPTED:
                        ele.setAccepted(true);
                        ele.setSeenOffer(true);
                        break;
                    default:
                        break;
                }
            }
        }
        CurrentUser.setTrades(tmp);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/SetViewedTrade", "FINISHED ASYNC TASK");
    }


}
