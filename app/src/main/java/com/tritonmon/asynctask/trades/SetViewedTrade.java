package com.tritonmon.asynctask.trades;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.battle.handler.XPHandler;
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
            if (choice == Choices.ACCEPTED) {
                int evolvedId = XPHandler.shouldBeEvolved(trade.getListerPokemonId());
                Log.e("setviewedtrade", "lister evolvedid: " + Integer.toString(evolvedId));
                if (trade.getListerPokemonId() != evolvedId) {
                    Log.e("setviewedtrade", "lister");
                    url = Constant.SERVER_URL + "/userspokemon/evolve_pokemon/users_pokemon_id="+Integer.toString(trade.getListerUsersPokemonId())+"/pokemon_id="+Integer.toString(evolvedId);
                    response = MyHttpClient.post(url);
                    if (MyHttpClient.getStatusCode(response) != Constant.STATUS_CODE_SUCCESS) {
                        return false;
                    }

//                    url = Constant.SERVER_URL + "/updatelisterpokemonid";
//                    url+= "/" + Integer.toString(trade.getOffererUsersId())
//                            + "/" + Integer.toString(trade.getOfferUsersPokemonId())
//                            + "/" + Integer.toString(trade.getListerUsersId())
//                            + "/" + Integer.toString(trade.getListerUsersPokemonId())
//                            + "/" + Integer.toString(evolvedId);
//                    response = MyHttpClient.post(url);
//                    if (MyHttpClient.getStatusCode(response) != Constant.STATUS_CODE_SUCCESS) {
//                        return false;
//                    }
                }
                evolvedId = XPHandler.shouldBeEvolved(trade.getOfferPokemonId());
                Log.e("setviewedtrade", "offer evolvedid: " + Integer.toString(evolvedId));
                if (trade.getOfferPokemonId() != evolvedId) {
                    Log.e("setviewedtrade", "offer");
                    url = Constant.SERVER_URL + "/userspokemon/evolve_pokemon/users_pokemon_id="+Integer.toString(trade.getOfferUsersPokemonId())+"/pokemon_id="+Integer.toString(evolvedId);
                    response = MyHttpClient.post(url);
                    if (MyHttpClient.getStatusCode(response) != Constant.STATUS_CODE_SUCCESS) {
                        return false;
                    }

//                    url = Constant.SERVER_URL + "/updateofferpokemonid";
//                    url+= "/" + Integer.toString(trade.getOffererUsersId())
//                            + "/" + Integer.toString(trade.getOfferUsersPokemonId())
//                            + "/" + Integer.toString(trade.getListerUsersId())
//                            + "/" + Integer.toString(trade.getListerUsersPokemonId())
//                            + "/" + Integer.toString(evolvedId);
//                    response = MyHttpClient.post(url);
//                    if (MyHttpClient.getStatusCode(response) != Constant.STATUS_CODE_SUCCESS) {
//                        return false;
//                    }
                }
                return true;
            }
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
