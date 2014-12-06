package com.tritonmon.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tritonmon.global.Audio;


public class PokeCenter extends ActionBarActivity {

    private MediaPlayer mp;
    private MediaPlayer looper;
    private MediaPlayer healing;


    private Button heal;
    private Button restock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                            mp.start();
                        }
                    });
                }
            }
        });

        restock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Audio.isAudioEnabled()) {
                    Audio.sfx.start();
                }
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
        }
        if (mp != null) {
            mp.release();
        }
        Intent i = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        if (mp != null){
            mp.release();
        }
        if (looper != null) {
            looper.release();
        }

        super.onDestroy();
    }

}
