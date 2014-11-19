package com.tritonmon.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.MyGson;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.BattlingPokemon;
import com.tritonmon.model.User;
import com.tritonmon.staticmodel.Pokemon;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainMenu extends Activity {

    private TextView statsTextView;

    private Button trainerCardButton;
    private Button viewMapButton;
    private Button pokemonCenterButton;
    private Button battle; // TODO for testing only

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }


//        statsTextView = (TextView) findViewById(R.id.statsTextView);

        trainerCardButton = (Button) findViewById(R.id.trainerCardButton);
        viewMapButton = (Button) findViewById(R.id.viewMapButton);
        pokemonCenterButton = (Button) findViewById(R.id.pokeCenterButton);

        trainerCardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), TrainerCard.class);
                startActivity(i);
            }
        });

        viewMapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MapView.class);
                startActivity(i);
            }
        });

        pokemonCenterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PokeCenter.class);
                startActivity(i);
            }
        });

        // TODO for testing only
        battle = (Button) findViewById(R.id.battleButton);
        battle.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Battle.class);
                startActivity(i);
            }
        });

        MyTimerTask mytask;
        mytask = new MyTimerTask();
        timer = new Timer();
        timer.schedule(mytask, 0, 1000);

        if (getIntent().getExtras() != null) {
            BattlingPokemon pokemon1 = getIntent().getExtras().getParcelable("pokemon1");
//            BattlingPokemon pokemon2 = getIntent().getExtras().getParcelable("pokemon2");
//            List<Integer> movesThatCanBeLearned = getIntent().getExtras().getIntegerArrayList("movesThatCanBeLearned");
//            boolean evolved = getIntent().getExtras().getBoolean("evolved");

            Log.e("ANURAG", "moves : " + Arrays.toString(pokemon1.getMoves().toArray()));
            String movesString = "";
            for (Integer move : pokemon1.getMoves()) {
                if (!movesString.isEmpty()) {
                    movesString += ",";
                }
                if (move == null) {
                    movesString+="null";
                }
                else {
                    movesString += move.toString();
                }
            }

            Log.e("ANURAG", "pps : " + pokemon1.getPps().toString());
            String ppsString = "";
            for (Integer pp : pokemon1.getPps()) {
                if (!ppsString.isEmpty()) {
                    ppsString += ",";
                }
                if (pp == null) {
                    ppsString += "null";
                }
                else {
                    ppsString += pp.toString();
                }
            }

            new UpdatePokemonAfterBattle().execute(
                    new Integer(pokemon1.getUsersPokemonId()).toString(),
                    new Integer(pokemon1.getPokemonId()).toString(),
                    new Integer(pokemon1.getLevel()).toString(),
                    new Integer(pokemon1.getXp()).toString(),
                    new Integer(pokemon1.getHealth()).toString(),
                    movesString,
                    ppsString
                    );

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), Settings.class);
            startActivity(i);
            return true;
        }
        else if(id == R.id.logout) {
            CurrentUser.logout();
            Intent i = new Intent(getApplicationContext(), Tritonmon.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);
            return rootView;
        }
    }

    @Override
    public void onBackPressed() {
        if (CurrentUser.isLoggedIn()) {
            timer.cancel();
            CurrentUser.logout();
        }
        Intent i = new Intent(getApplicationContext(), Tritonmon.class);
        startActivity(i);
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (CurrentUser.isLoggedIn()) {
//                        statsTextView.setText(CurrentUser.getUser().getUsername() + "\n" + CurrentUser.getParty().toString());
                    }
                }
            });

        }
    }

    private class UpdatePokemonAfterBattle extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String url = Constant.SERVER_URL + "/userspokemon/afterbattle/";
            for (int i=0; i<params.length; i++) {
                if (i == 5) {
                    url += "moves=";
                }
                else if (i == 6) {
                    url += "pps=";
                }
                url += params[i] + "/";
            }

            HttpResponse response = MyHttpClient.post(url);
            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                return true;
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(getApplicationContext(), "success",
                        Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "failed",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}
