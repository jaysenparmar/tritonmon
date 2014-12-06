package com.tritonmon.asynctask.user;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.global.singleton.MyHttpClient;
import com.tritonmon.model.UsersPokemon;

import org.apache.http.HttpResponse;

import java.util.List;

public class UpdatePartyTask extends AsyncTask<Void, Void, Boolean> {

    private List<UsersPokemon> pokemonList;

    public UpdatePartyTask(List<UsersPokemon> list) {
        super();
        pokemonList = list;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/UpdatePartyTask", "STARTED ASYNC TASK");
        Log.d("asynctask/UpdatePartyTask", "Updating CurrentUser's party and stash");

        String url = Constant.SERVER_URL + "/userspokemon/updateparty";
        String idUrl1 = "/users_pokemon_id=";
        String idUrl2 = "";
        String slotUrl1 = "/slot_num=";
        String slotUrl2 = "";

        for (UsersPokemon pokemon : pokemonList) {
            if (!idUrl2.isEmpty()) {
                idUrl2 += ",";
            }
            idUrl2 += pokemon.getUsersPokemonId();

            if (!slotUrl2.isEmpty()) {
                slotUrl2 += ",";
            }
            slotUrl2 += pokemon.getSlotNum();
        }
        url = url + idUrl1 + idUrl2 + slotUrl1 + slotUrl2;

        HttpResponse response = MyHttpClient.post(url);
        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            return true;
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/UpdatePartyTask", "FINISHED ASYNC TASK");
    }
}
