package com.tritonmon.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.tritonmon.asynctask.user.UpdateCurrentUserTask;
import com.tritonmon.global.Audio;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.StaticData;
import com.tritonmon.toast.TritonmonToast;

import java.text.ParseException;


public class MainMenu extends ActionBarActivity {

    private Button trainerCardButton;
    private ImageView viewMapButton;
    private ImageView pokemonCenterButton;

    private Button battle;
    private ImageView party;

    private boolean backButtonPressed;
    private Handler backButtonHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        CurrentUser.setSoundGuy((AudioManager)getSystemService(Context.AUDIO_SERVICE));

        trainerCardButton = (Button) findViewById(R.id.trainerCardButton);
        viewMapButton = (ImageView) findViewById(R.id.viewMapButton);
        pokemonCenterButton = (ImageView) findViewById(R.id.pokeCenterButton);

        trainerCardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (Audio.isAudioEnabled()) {
                    Audio.sfx.start();
                }
                Intent i = new Intent(getApplicationContext(), TrainerCard.class);
                startActivity(i);
            }
        });

        viewMapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (Audio.isAudioEnabled()) {
                    Audio.sfx.start();
                }
                setProgressBarIndeterminateVisibility(true);
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
            }
        });

        pokemonCenterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (Audio.isAudioEnabled()) {
                    Audio.sfx.start();
                }
                Intent i = new Intent(getApplicationContext(), PokeCenter.class);
                startActivity(i);
            }
        });

        battle = (Button) findViewById(R.id.battleButton);
        battle.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                if (Audio.isAudioEnabled()) {
                    Audio.sfx.start();
                }
                Intent i = new Intent(getApplicationContext(), Battle.class);
                startActivity(i);
            }
        });

        party = (ImageView) findViewById(R.id.partyButton);
        party.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                if (Audio.isAudioEnabled()) {
                    Audio.sfx.start();
                }
                Intent i = new Intent(getApplicationContext(), Party.class);
                startActivity(i);
            }
        });

        backButtonPressed = false;
        backButtonHandler = new Handler();

        // make sure static data is loaded
        try {
            if (Constant.pokemonData == null) {
                StaticData.load(getAssets());
            }
        }
        catch (ParseException e) {
            TritonmonToast.makeText(getApplicationContext(), "ERROR: Failed to load static data", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        new UpdateCurrentUserTask(this).execute();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main_menu;
    }

    @Override
    protected int getMenuResourceId() {
        return R.menu.logged_in_menu;
    }

    @Override
    public void onBackPressed() {
        if (!backButtonPressed) {
            backButtonPressed = true;
            TritonmonToast.makeText(getApplicationContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
            backButtonHandler.postDelayed(backButtonRunnable, 2000);
        }
        else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private Runnable backButtonRunnable = new Runnable() {
        public void run() {
            backButtonPressed = false;
        }
    };

}
