package com.tritonmon.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.tritonmon.asynctask.battle.CaughtPokemonTask;
import com.tritonmon.asynctask.battle.UpdateAfterBattleTask;
import com.tritonmon.asynctask.user.UpdateCurrentUserTask;
import com.tritonmon.battle.requestresponse.CatchResponse;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.model.BattlingPokemon;
import com.tritonmon.model.PokemonParty;


public class MainMenu extends Activity {

    private Button trainerCardButton;
    private Button viewMapButton;
    private Button pokemonCenterButton;

    private MediaPlayer mp;
    private MediaPlayer sfx;

    private Button battle;
    private Button party;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if(mp != null) {
            mp.release();
        }

        if(sfx != null) {
            sfx.release();
        }

        trainerCardButton = (Button) findViewById(R.id.trainerCardButton);
        viewMapButton = (Button) findViewById(R.id.viewMapButton);
        pokemonCenterButton = (Button) findViewById(R.id.pokeCenterButton);

        CurrentUser.setSoundGuy((AudioManager)getSystemService(Context.AUDIO_SERVICE));
        sfx = MediaPlayer.create(getApplicationContext(), R.raw.choose);

        trainerCardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sfx.start();
                mp.release();
                Intent i = new Intent(getApplicationContext(), TrainerCard.class);
                startActivity(i);
            }
        });

        viewMapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sfx.start();
                mp.release();
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
            }
        });

        pokemonCenterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sfx.start();
                mp.release();
                Intent i = new Intent(getApplicationContext(), PokeCenter.class);
                startActivity(i);
            }
        });

        battle = (Button) findViewById(R.id.battleButton);
        battle.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                sfx.start();
                mp.release();
                Intent i = new Intent(getApplicationContext(), Battle.class);
                startActivity(i);
            }
        });

        party = (Button) findViewById(R.id.partyButton);
        party.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                sfx.start();
                mp.release();
                Intent i = new Intent(getApplicationContext(), Party.class);
                startActivity(i);
            }
        });


        mp = MediaPlayer.create(this, R.raw.main_menu);
        mp.setLooping(true);
        mp.start();

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("caughtPokemon")) {
                CatchResponse catchResponse = getIntent().getExtras().getParcelable("catchResponse");
                handleCaughtPokemon(catchResponse);
            }
            else if (getIntent().getExtras().containsKey("wonBattle") ||
                    getIntent().getExtras().containsKey("lostBattle") ||
                    getIntent().getExtras().containsKey("ranFromBattle")) {

                BattlingPokemon pokemon1 = getIntent().getExtras().getParcelable("pokemon1");
                int numPokeballs = getIntent().getExtras().getInt("numPokeballs");
                handleAfterBattle(pokemon1, numPokeballs);
            }
            else if (getIntent().getExtras().containsKey("updatedParty")) {

            }

            new UpdateCurrentUserTask().execute();
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
            mp.release();
            CurrentUser.logout();
            Intent i = new Intent(getApplicationContext(), Tritonmon.class);
            startActivity(i);
            return true;
        }
        else if(id == R.id.refresh) {
            new UpdateCurrentUserTask().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (CurrentUser.isLoggedIn()) {
            CurrentUser.logout();
        }
        mp.release();
        Intent i = new Intent(getApplicationContext(), Tritonmon.class);
        startActivity(i);
    }

    private void handleAfterBattle(BattlingPokemon pokemon, int numPokeballs) {
        new UpdateAfterBattleTask(
                pokemon.toUsersPokemon(),
                CurrentUser.getUsername(),
                numPokeballs
        ).execute();
    }

    // TODO reduce to 1 server call
    private void handleCaughtPokemon(CatchResponse catchResponse) {
        // server call 1 - update current user's pokemon
        handleAfterBattle(catchResponse.getOldPokemon(), catchResponse.getNumPokeballs());

        // server call 2 - add caught pokemon to user's pokmeon
        BattlingPokemon caughtPokemon = catchResponse.getNewPokemon();
        int slotNum = (CurrentUser.getPokemonParty().size() != PokemonParty.MAX_PARTY_SIZE) ? CurrentUser.getPokemonParty().size() : -1;
        caughtPokemon.setSlotNum(slotNum);
        caughtPokemon.setNickname("oneWithNature");

        new CaughtPokemonTask(caughtPokemon, CurrentUser.getUsername()).execute();
    }

}
