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

import com.tritonmon.asynctask.user.UpdateCurrentUserTask;
import com.tritonmon.global.CurrentUser;


public class MainMenu extends Activity {

    private Button trainerCardButton;
    private Button viewMapButton;
    private Button pokemonCenterButton;

    private MediaPlayer sfx;

    private Button battle;
    private Button party;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if(sfx != null) {
            sfx.release();
        }

        CurrentUser.setSoundGuy((AudioManager)getSystemService(Context.AUDIO_SERVICE));
        sfx = MediaPlayer.create(getApplicationContext(), R.raw.choose);

        trainerCardButton = (Button) findViewById(R.id.trainerCardButton);
        viewMapButton = (Button) findViewById(R.id.viewMapButton);
        pokemonCenterButton = (Button) findViewById(R.id.pokeCenterButton);

        trainerCardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sfx.start();
                Intent i = new Intent(getApplicationContext(), TrainerCard.class);
                startActivity(i);
            }
        });

        viewMapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sfx.start();
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
            }
        });

        pokemonCenterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sfx.start();
                Intent i = new Intent(getApplicationContext(), PokeCenter.class);
                startActivity(i);
            }
        });

        battle = (Button) findViewById(R.id.battleButton);
        battle.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                sfx.start();
                Intent i = new Intent(getApplicationContext(), Battle.class);
                startActivity(i);
            }
        });

        party = (Button) findViewById(R.id.partyButton);
        party.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                sfx.start();
                Intent i = new Intent(getApplicationContext(), Party.class);
                startActivity(i);
            }
        });

        new UpdateCurrentUserTask().execute();
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
        Intent i = new Intent(getApplicationContext(), Tritonmon.class);
        startActivity(i);
    }

}
