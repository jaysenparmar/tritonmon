package com.tritonmon.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ImageUtil;


public class Battle extends Activity {

    ImageView myPokemonImage;
    TextView myPokemonName;

    Button attack1Button, attack2Button, attack3Button, attack4Button;
    Button throwPokeballButton, runButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        myPokemonImage = (ImageView) findViewById(R.id.myPokemonImage);
        myPokemonName = (TextView) findViewById(R.id.myPokemonName);

        attack1Button = (Button) findViewById(R.id.attack1Button);
        attack2Button = (Button) findViewById(R.id.attack2Button);
        attack3Button = (Button) findViewById(R.id.attack3Button);
        attack4Button = (Button) findViewById(R.id.attack4Button);

        throwPokeballButton = (Button) findViewById(R.id.throwPokeballButton);
        runButton = (Button) findViewById(R.id.runButton);

        if (CurrentUser.isLoggedIn()) {
            String pokemonInfo = CurrentUser.getParty().getPokemon(0).getNickname() + " the " + CurrentUser.getParty().getPokemon(0).getName();
            myPokemonName.setText(pokemonInfo);
            myPokemonImage.setImageResource(
                    ImageUtil.getPokemonBackImageResource(this, CurrentUser.getParty().getPokemon(0).getPokemonId()));
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
