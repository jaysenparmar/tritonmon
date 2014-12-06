package com.tritonmon.asynctask.trades;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.Trade;
import com.tritonmon.model.UsersPokemon;

import org.apache.http.HttpResponse;

import java.util.List;

public class TradePlayer extends AsyncTask<Void, Void, Boolean> {

    int offererUsersId;
    int offerUsersPokemonId;
    int offerPokemonId;
    int offerLevel;
    int listerUsersId;
    int listerUsersPokemonId;
    int listerPokemonId;
    int listerLevel;

    public TradePlayer(int offererUsersId, UsersPokemon offerPokemon, int listerUsersId, UsersPokemon listerPokemon) {
        this.offererUsersId = offererUsersId;
        this.offerUsersPokemonId = offerPokemon.getUsersPokemonId();
        this.offerPokemonId = offerPokemon.getPokemonId();
        this.offerLevel = offerPokemon.getLevel();
        this.listerUsersId = listerUsersId;
        this.listerUsersPokemonId = listerPokemon.getUsersPokemonId();
        this.listerPokemonId = listerPokemon.getPokemonId();
        this.listerLevel = listerPokemon.getLevel();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/TradePlayer", "STARTED ASYNC TASK");
        Log.d("asynctask/TradePlayer", "Sending user " + offererUsersId + " listerUsersId " + listerUsersId + " to server");

        String url = Constant.SERVER_URL + "/trade"
                + "/" + Integer.toString(offererUsersId)
                + "/" + Integer.toString(offerUsersPokemonId)
                + "/" + Integer.toString(offerPokemonId)
                + "/" + Integer.toString(offerLevel)
                + "/" + Integer.toString(listerUsersId)
                + "/" + Integer.toString(listerUsersPokemonId)
                + "/" + Integer.toString(listerPokemonId)
                + "/" + Integer.toString(listerLevel);

        HttpResponse response = MyHttpClient.post(url);
        makeNewTrade();
        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            return true;
        }

        Log.e("asynctask/TradePlayer", response.getStatusLine().toString());
        return false;
    }

    private void makeNewTrade() {
        Trade trade = new Trade(offererUsersId, offerUsersPokemonId, offerPokemonId, offerLevel,
        listerUsersId, listerUsersPokemonId, listerPokemonId, listerLevel);
        List<Trade> tmp = CurrentUser.getTrades();
        tmp.add(trade);
        CurrentUser.setTrades(tmp);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/TradePlayer", "FINISHED ASYNC TASK");
    }

}
