package com.tritonmon.asynctask.battle;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.global.util.ListUtil;
import com.tritonmon.global.singleton.MyHttpClient;
import com.tritonmon.model.UsersPokemon;

import org.apache.http.HttpResponse;

public class CaughtPokemonTask extends AsyncTask<Void, Void, Boolean>  {

    UsersPokemon caughtPokemon;
    int userId;

    public CaughtPokemonTask(UsersPokemon caughtPokemon, int userId) {
        this.caughtPokemon = caughtPokemon;
        this.userId = userId;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/CaughtPokemonTask", "STARTED ASYNC TASK");
        Log.d("asynctask/CaughtPokemonTask", "Sending user_id " + userId + "'s caught pokemon " + caughtPokemon.getName() + " to server");

        String url = Constant.SERVER_URL + "/addpokemon/caught"
                + "/" + userId
                + "/" + caughtPokemon.getPokemonId()
                + "/" + caughtPokemon.getSlotNum()
                + "/" + caughtPokemon.getNickname()
                + "/" + caughtPokemon.getLevel()
                + "/" + caughtPokemon.getXp()
                + "/" + caughtPokemon.getHealth()
                + "/moves=" + ListUtil.toCommaSeparatedString(caughtPokemon.getMoves())
                + "/pps=" + ListUtil.toCommaSeparatedString(caughtPokemon.getPps());

        HttpResponse response = MyHttpClient.post(url);
        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            return true;
        }

        Log.e("asynctask/CaughtPokemonTask", response.getStatusLine().toString());
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/CaughtPokemonTask", "FINISHED ASYNC TASK");
    }

}
