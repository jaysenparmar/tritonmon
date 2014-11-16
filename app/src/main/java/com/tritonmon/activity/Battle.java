package com.tritonmon.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.tritonmon.global.CurrentUser;


public class Battle extends Activity {

    ImageView myPokemonImage;
    TextView myPokemonInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        myPokemonImage = (ImageView) findViewById(R.id.myPokemonImage);
        myPokemonInfo = (TextView) findViewById(R.id.myPokemonInfo);

        if (CurrentUser.isLoggedIn()) {
            String pokemonInfo = CurrentUser.getParty().getPokemon(0).getNickname() + " the " + CurrentUser.getParty().getPokemon(0).getName();
            myPokemonInfo.setText(pokemonInfo);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.battle, menu);
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
}
