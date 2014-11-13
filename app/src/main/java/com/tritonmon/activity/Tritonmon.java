package com.tritonmon.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.tritonmon.global.Constant;
import com.tritonmon.global.MyGson;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.global.StaticData;
import com.tritonmon.model.DamageClasses;
import com.tritonmon.model.LevelUpXp;
import com.tritonmon.model.MoveMetaAilments;
import com.tritonmon.model.MoveMetaStatChanges;
import com.tritonmon.model.Moves;
import com.tritonmon.model.Pokemon;
import com.tritonmon.model.PokemonMoves;
import com.tritonmon.model.PokemonStats;
import com.tritonmon.model.PokemonTypes;
import com.tritonmon.model.Stats;
import com.tritonmon.model.TypeEfficacy;
import com.tritonmon.model.Types;

import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class Tritonmon extends Activity {

    private Button fbLogin;
    private Button loginButton;
    private Button registerButton;

    private TextView jsonText;

    private void init() {
        fbLogin = (Button) findViewById(R.id.fb_login_button);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);

        jsonText = (TextView) findViewById(R.id.json_text_view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tritonmon);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        init();

        fbLogin.setOnClickListener(new OnClickListener(){
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), FacebookLogin.class);
                startActivity(i);
            }
        });

        loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }
        });

        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Register.class);
                startActivity(i);
            }
        });

        try {
            StaticData.load(getAssets());
        } catch (ParseException e) {
            Log.e("Tritonmon", "error reading static files");
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tritonmon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
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
            View rootView = inflater.inflate(R.layout.fragment_tritonmon, container, false);
            return rootView;
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "add exit app functionality here", Toast.LENGTH_LONG).show();
    }

}
