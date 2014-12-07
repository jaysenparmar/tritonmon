package com.tritonmon.asynctask.battle;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.global.util.ListUtil;
import com.tritonmon.global.singleton.MyHttpClient;
import com.tritonmon.model.UsersPokemon;

import org.apache.http.HttpResponse;

public class UpdateAfterBattleTask extends AsyncTask<Void, Void, Boolean> {

    private UsersPokemon pokemon;
    private String username;
    private int numPokeballs;

    public UpdateAfterBattleTask(UsersPokemon pokemon, String username, int numPokeballs) {
        this.pokemon = pokemon;
        this.username = username;
        this.numPokeballs = numPokeballs;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/UpdateAfterBattleTask", "STARTED ASYNC TASK");
        Log.d("asynctask/UpdateAfterBattleTask", "Sending user " + username + "'s updated " + pokemon.getName() + " info to server");

        String url = Constant.SERVER_URL + "/userspokemon/afterbattle"
                + "/" + pokemon.getUsersPokemonId()
                + "/" + pokemon.getPokemonId()
                + "/" + pokemon.getLevel()
                + "/" + pokemon.getXp()
                + "/" + pokemon.getHealth()
                + "/moves=" + ListUtil.toCommaSeparatedString(pokemon.getMoves())
                + "/pps=" + ListUtil.toCommaSeparatedString(pokemon.getPps())
                + "/" + Constant.encode(username)
                + "/" + numPokeballs;

        HttpResponse response = MyHttpClient.post(url);
        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            return true;
        }
        Log.e("asynctask/UpdateAfterBattleTask", response.getStatusLine().toString());
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/UpdateAfterBattleTask", "FINISHED ASYNC TASK");
    }
}
