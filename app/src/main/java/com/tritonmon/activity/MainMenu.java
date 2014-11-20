package com.tritonmon.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tritonmon.battle.requestresponse.CatchResponse;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ListUtil;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.BattlingPokemon;

import org.apache.http.HttpResponse;

import java.util.Timer;
import java.util.TimerTask;


public class MainMenu extends Activity {

    private TextView statsTextView;

    private Button trainerCardButton;
    private Button viewMapButton;
    private Button pokemonCenterButton;

    // TODO for testing only
    private Button battle;
    private Button party;

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

        party = (Button) findViewById(R.id.partyButton);
        party.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Party.class);
                startActivity(i);
            }
        });

        MyTimerTask mytask;
        mytask = new MyTimerTask();
        timer = new Timer();
        timer.schedule(mytask, 0, 1000);

        if (getIntent().getExtras() != null) {
            if (!getIntent().getExtras().containsKey("caughtPokemon")) {
                BattlingPokemon pokemon1 = getIntent().getExtras().getParcelable("pokemon1");
                handleAfterBattle(pokemon1);
            } else {
                CatchResponse catchResponse = getIntent().getExtras().getParcelable("catchResponse");
                handleCaughtPokemon(catchResponse);
            }
        }

    }

    // prob will add more params later
    private void handleAfterBattle(BattlingPokemon pokemon) {

//        BattlingPokemon pokemon1 = getIntent().getExtras().getParcelable("pokemon1");
//            BattlingPokemon pokemon2 = getIntent().getExtras().getParcelable("pokemon2");
//            List<Integer> movesThatCanBeLearned = getIntent().getExtras().getIntegerArrayList("movesThatCanBeLearned");
//            boolean evolved = getIntent().getExtras().getBoolean("evolved");

        String movesString = ListUtil.convertMovesToString(pokemon.getMoves());
        String ppsString = ListUtil.convertPpsToString(pokemon.getPps());

        new UpdatePokemonAfterBattle().execute(
                Integer.toString(pokemon.getUsersPokemonId()),
                Integer.toString(pokemon.getPokemonId()),
                Integer.toString(pokemon.getLevel()),
                Integer.toString(pokemon.getXp()),
                Integer.toString(pokemon.getHealth()),
                movesString,
                ppsString);
    }

    // possibly optimize this. need to let user set nickname
    public void handleCaughtPokemon(CatchResponse catchResponse) {
        handleAfterBattle(catchResponse.getPokemon1());
        BattlingPokemon pokemon = catchResponse.getPokemon2();

        int slotNum = CurrentUser.getPokemonParty().size() != 6 ? CurrentUser.getPokemonParty().size() : -1;
        String nickname = "oneWithNature";

        String movesString = ListUtil.convertMovesToString(pokemon.getMoves());
        String ppsString = ListUtil.convertPpsToString(pokemon.getPps());

        new AddCaughtPokemon().execute(
                CurrentUser.getUsername(),
                Integer.toString(pokemon.getPokemonId()),
                Integer.toString(slotNum),
                nickname,
                Integer.toString(pokemon.getLevel()),
                Integer.toString(pokemon.getXp()),
                Integer.toString(pokemon.getHealth()),
                movesString,
                ppsString);
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


    private class AddCaughtPokemon extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String url = Constant.SERVER_URL + "/addpokemon/caught/";
            for (int i=0; i<params.length; i++) {
                if (i == 7) {
                    url += "moves=";
                }
                else if (i == 8) {
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
