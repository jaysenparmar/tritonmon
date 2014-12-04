package com.tritonmon.asynctask.trades;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.global.ListUtil;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.UsersPokemon;

import org.apache.http.HttpResponse;

import java.util.List;

public class TradePlayer extends AsyncTask<Void, Void, Boolean> {

    String offererUsername;
    int offerUsersPokemonId;
    int offerPokemonId;
    int offerLevel;
    String listerUsername;
    int listerUsersPokemonId;
    int listerPokemonId;
    int listerLevel;

    public TradePlayer(String offererUsername, UsersPokemon offerPokemon, String listerUsername, UsersPokemon listerPokemon) {
        this.offererUsername = offererUsername;
        this.offerUsersPokemonId = offerPokemon.getUsersPokemonId();
        this.offerPokemonId = offerPokemon.getPokemonId();
        this.offerLevel = offerPokemon.getLevel();
        this.listerUsername = listerUsername;
        this.listerUsersPokemonId = listerPokemon.getUsersPokemonId();
        this.listerPokemonId = listerPokemon.getPokemonId();
        this.listerLevel = listerPokemon.getLevel();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/TradePlayer", "STARTED ASYNC TASK");
        Log.d("asynctask/TradePlayer", "Sending user " + offererUsername + " lister " + listerUsername + " to server");

        String url = Constant.SERVER_URL + "/trade"
                + "/" + Constant.encode(offererUsername)
                + "/" + Integer.toString(offerUsersPokemonId)
                + "/" + Integer.toString(offerPokemonId)
                + "/" + Integer.toString(offerLevel)
                + "/" + Constant.encode(listerUsername)
                + "/" + Integer.toString(listerUsersPokemonId)
                + "/" + Integer.toString(listerPokemonId)
                + "/" + Integer.toString(listerLevel);

        HttpResponse response = MyHttpClient.post(url);
        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            return true;
        }

        Log.e("asynctask/TradePlayer", response.getStatusLine().toString());
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/TradePlayer", "FINISHED ASYNC TASK");
    }

}
