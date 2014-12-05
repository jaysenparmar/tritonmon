package com.tritonmon.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tritonmon.asynctask.user.UpdateCurrentUserTask;
import com.tritonmon.global.CurrentUser;


public class PokeCenter extends Activity {

    private MediaPlayer mp;
    private MediaPlayer looper;
    private MediaPlayer sfx;

    private Button heal;
    private Button restock;
    private Button change;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poke_center);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();

        }

        if(mp != null) {
            mp.release();
        }

        if(looper != null) {
            looper.release();
        }

        if(sfx != null) {
            sfx.release();
        }

        heal = (Button) findViewById(R.id.button);
        restock = (Button) findViewById(R.id.button2);
        change = (Button) findViewById(R.id.button3);

        sfx = MediaPlayer.create(getApplicationContext(), R.raw.choose);

        heal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sfx.start();
            }
        });

        restock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sfx.start();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sfx.start();
            }
        });

        mp = MediaPlayer.create(this, R.raw.pokemon_center_first_loop);
        looper = MediaPlayer.create(this, R.raw.pokemon_center_loop);
        looper.setLooping(true);
        mp.start();
        mp.setNextMediaPlayer(looper);

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
        looper.release();
        mp.release();
        Intent i = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(i);
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
            View rootView = inflater.inflate(R.layout.fragment_poke_center, container, false);
            return rootView;
        }
    }

    @Override
    protected void onDestroy() {
        if(null!=mp){
            mp.release();
        }
        if(null!=looper) {
            looper.release();
        }
        if(null!=sfx){
            sfx.release();
        }
        super.onDestroy();
    }

}
