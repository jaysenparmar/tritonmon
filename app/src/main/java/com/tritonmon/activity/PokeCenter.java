package com.tritonmon.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.tritonmon.global.Audio;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.singleton.MyHttpClient;
import com.tritonmon.model.UsersPokemon;
import com.tritonmon.staticmodel.Moves;
import com.tritonmon.toast.TritonmonToast;
import com.tritonmon.toast.TritonmonToastMd;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PokeCenter extends ActionBarActivity {

    private MediaPlayer mp;
    private MediaPlayer looper;
    private MediaPlayer healing;


    private Button heal;
    private Button restock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Constant.DISABLE_ACTION_BAR) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        if(mp != null) {
            mp.release();
            mp = null;
        }
        if(looper != null) {
            looper.release();
            looper = null;
        }

        if(healing != null) {
            healing.release();
            healing = null;
        }

        mp = MediaPlayer.create(this, R.raw.pokemon_center_first_loop);
        looper = MediaPlayer.create(this, R.raw.pokemon_center_loop);
        healing = MediaPlayer.create(this, R.raw.pokecenter_healing);
        looper.setLooping(true);
        mp.setNextMediaPlayer(looper);
        Audio.setBackgroundMusic(mp);

        if (Audio.isAudioEnabled()) {
            mp.start();
        }

        heal = (Button) findViewById(R.id.healButton);
        restock = (Button) findViewById(R.id.restockButton);

        heal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Audio.isAudioEnabled()) {
                    Audio.sfx.start();
                    mp.pause();
                    healing.start();
                    healing.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            if (mp != null) {
                                mp.start();
                            }
                        }
                    });
                }

                List<UsersPokemon> pokemonList = new ArrayList<UsersPokemon>();
                pokemonList.addAll(CurrentUser.getPokemonParty().getPokemonList());
                pokemonList.addAll(CurrentUser.getPokemonStash());
                new HealPokemonTask(pokemonList).execute();
            }
        });

        restock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Audio.isAudioEnabled()) {
                    Audio.sfx.start();
                }

                new RestockPokeballsTask().execute(CurrentUser.getUsersId());
            }
        });

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_poke_center;
    }

    @Override
    protected int getMenuResourceId() {
        return R.menu.logged_in_menu;
    }

    @Override
    public void onBackPressed() {
        if (looper != null) {
            looper.release();
            looper = null;
        }
        if (mp != null) {
            mp.release();
            mp = null;
        }

        Intent i = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        if (mp != null) {
            mp.release();
        }
        if (looper != null) {
            looper.release();
        }

        super.onDestroy();
    }

    private class RestockPokeballsTask extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... params) {
            Log.d("PokeCenter/RestockPokeballsTask", "STARTED ASYNC TASK");
            Log.d("PokeCenter/RestockPokeballsTask", "Healing CurrentUser's pokemon");

            if (params.length != 1) {
                Log.e("PokeCenter/RestockPokeballsTask", "Expected 1 users_id but found " + Arrays.toString(params));
                return false;
            }

            String url = Constant.SERVER_URL + "/pokeballs/users_id=" + params[0];

            HttpResponse response = MyHttpClient.post(url);
            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                return true;
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            TritonmonToast.makeText(getApplicationContext(), "You now have 10 Pokeballs", Toast.LENGTH_LONG).show();
            Log.d("PokeCenter/RestockPokeballsTask", "FINISHED ASYNC TASK");
        }
    }

    private class HealPokemonTask extends AsyncTask<Void, Void, Boolean> {

        private List<UsersPokemon> pokemonList;

        public HealPokemonTask(List<UsersPokemon> list) {
            this.pokemonList = list;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d("PokeCenter/HealCurrentUserPokemonTask", "STARTED ASYNC TASK");
            Log.d("PokeCenter/HealCurrentUserPokemonTask", "Healing CurrentUser's pokemon");

            String url = Constant.SERVER_URL + "/userspokemon/heal";
            String idUrl1 = "/users_pokemon_id=";
            String idUrl2 = "";
            String healthUrl1 = "/health=";
            String healthUrl2 = "";
            String ppUrl1 = "/pp=";
            String ppUrl2 = "";

            for (UsersPokemon pokemon : pokemonList) {
                if (!idUrl2.isEmpty()) {
                    idUrl2 += ",";
                }
                idUrl2 += pokemon.getUsersPokemonId();

                if (!healthUrl2.isEmpty()) {
                    healthUrl2 += ",";
                }
                healthUrl2 += pokemon.getMaxHealth();
                pokemon.setHealth(pokemon.getMaxHealth());

                for (int i=0; i<4; i++) {
                    if (!ppUrl2.isEmpty()) {
                        ppUrl2 += ",";
                    }

                    List<Integer> moves = pokemon.getMoves();
                    if (moves.get(i) != null) {
                        ppUrl2 += Moves.getMaxPp(moves.get(i));
                        pokemon.getPps().set(i, Moves.getMaxPp(moves.get(i)));
                    }
                    else {
                        ppUrl2 += "null";
                    }
                }
            }
            url = url + idUrl1 + idUrl2 + healthUrl1 + healthUrl2 + ppUrl1 + ppUrl2;

            HttpResponse response = MyHttpClient.post(url);
            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                return true;
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            TritonmonToastMd.makeText(getApplicationContext(), "We've restored your Pokemon to full health!", Toast.LENGTH_LONG).show();
            CurrentUser.setPokemon(pokemonList);
            Log.d("PokeCenter/HealCurrentUserPokemonTask", "FINISHED ASYNC TASK");
        }
    }
}
