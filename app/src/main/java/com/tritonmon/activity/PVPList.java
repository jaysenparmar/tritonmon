package com.tritonmon.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.tritonmon.asynctask.ToggleAvailableForBattleTask;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ImageUtil;
import com.tritonmon.global.MyGson;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.PVPUser;
import com.tritonmon.model.PokemonParty;
import com.tritonmon.model.User;
import com.tritonmon.model.UsersPokemon;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

public class PVPList extends Activity {

    private TextView pvpUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pvp_list);

        pvpUsers = (TextView) findViewById(R.id.pvpUsers);

        new PopulatePvpUsers().execute();
    }

    private class PopulatePvpUsers extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String url = Constant.SERVER_URL + "/getavailableforpvp/";

            HttpResponse response = MyHttpClient.get(url);
            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                String json = MyHttpClient.getJson(response);

                List<User> users = MyGson.getInstance().fromJson(json, new TypeToken<List<User>>() {}.getType());

//                Log.e("hihi", users.toString());

                if (!users.isEmpty()) {
                    for (User ele : users) {
                        url = Constant.SERVER_URL + "/getbestpokemoninfo/" + ele.getUsername();
                        response = MyHttpClient.get(url);
                        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                            json = MyHttpClient.getJson(response);
                            List<UsersPokemon> usersPokemon = MyGson.getInstance().fromJson(json, new TypeToken<List<UsersPokemon>>() {
                            }.getType());


                            PVPUser pvpUser = new PVPUser(ele, calculateMaxLevel(usersPokemon), calculateAverageLevel(usersPokemon));
                            Log.e("pvplist", pvpUser.toString());
                        }

                    }
                    return true;
                }
            }

            return false;
        }

        private int calculateAverageLevel(List<UsersPokemon> usersPokemon) {
            int sum = 0;
            for (UsersPokemon ele : usersPokemon) {
                sum += ele.getLevel();
            }
            return (int)((float)sum/(float)usersPokemon.size());
        }

        private int calculateMaxLevel(List<UsersPokemon> usersPokemon) {
            int maxLevel = 0;
            for (UsersPokemon ele : usersPokemon) {
                if (ele.getLevel() > maxLevel) {
                    maxLevel = ele.getLevel();
                }
            }
            return maxLevel;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                pvpUsers.setText("No users currently available!");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pvp_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
