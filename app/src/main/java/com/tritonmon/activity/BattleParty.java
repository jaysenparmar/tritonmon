package com.tritonmon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ProgressBarUtil;
import com.tritonmon.global.TritonmonToast;
import com.tritonmon.model.UsersPokemon;

import java.util.ArrayList;
import java.util.List;

public class BattleParty extends Activity {

    private ListView listView;

    private ArrayAdapter<UsersPokemon> adapter;
    private int selectedPokemonIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_party);


        List<UsersPokemon> pokemon = new ArrayList<UsersPokemon>(CurrentUser.getPokemonParty().getPokemonList());

        listView = (ListView) findViewById(R.id.battlePartyListView);

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("selectedPokemonIndex")) {
                selectedPokemonIndex = getIntent().getExtras().getInt("selectedPokemonIndex");
            } else {
                selectedPokemonIndex = 0;
            }
        }
        else {
            selectedPokemonIndex = 0;
        }

        adapter = new BattlePartyAdapter(pokemon);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(itemClickListener);

    }

    ListView.OnItemClickListener itemClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            UsersPokemon selectedPokemon = (UsersPokemon) listView.getItemAtPosition(position);

            if (selectedPokemon.getHealth() <= 0) {
                TritonmonToast.makeText(getApplicationContext(), "That pokemon has already fainted!", Toast.LENGTH_SHORT).show();
            }
            else {
                selectedPokemonIndex = position;
                adapter.notifyDataSetChanged();

                Intent i = new Intent();
                i.putExtra("selectedPokemonIndex", selectedPokemonIndex);
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        }
    };

    private class ViewHolder {
        public ImageView pokemonImageView;
        public TextView nameText;
        public TextView levelText;
        public TextView healthTextView;
        public ProgressBar healthBar;
        public ProgressBar xpBar;
        public ImageView pokeballImage;
    }

    private class BattlePartyAdapter extends ArrayAdapter<UsersPokemon> {
        public BattlePartyAdapter(List<UsersPokemon> list) {
            super(BattleParty.this, R.layout.battle_party_item_layout, R.id.nameText, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            if (v != convertView && v != null) {
                ViewHolder holder = new ViewHolder();
                holder.pokemonImageView = (ImageView) v.findViewById(R.id.pokemonImage);
                holder.nameText = (TextView) v.findViewById(R.id.nameText);
                holder.levelText = (TextView) v.findViewById(R.id.levelText);
                holder.healthTextView = (TextView) v.findViewById(R.id.healthText);
                holder.healthBar = (ProgressBar) v.findViewById(R.id.healthBar);
                holder.xpBar = (ProgressBar) v.findViewById(R.id.xpBar);
                holder.pokeballImage = (ImageView) v.findViewById(R.id.pokeballImage);
                v.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) v.getTag();
            UsersPokemon pokemon = getItem(position);

            holder.nameText.setText(pokemon.getName());
            holder.levelText.setText("Lvl " + pokemon.getLevel());
            holder.pokemonImageView.setImageResource(pokemon.getFrontImageResource(BattleParty.this));
            holder.healthTextView.setText(pokemon.getHealth() + " / " + pokemon.getMaxHealth() + " HP");

            ProgressBarUtil.updateHealthBar(getApplicationContext(), holder.healthBar, pokemon.getHealth(), pokemon.getMaxHealth());
            holder.xpBar.setProgress(ProgressBarUtil.getPercentage(pokemon.getCurrentXPBar(), pokemon.getTotalXPBar()));

            if (position == selectedPokemonIndex) {
                holder.pokeballImage.setVisibility(View.VISIBLE);
            }
            else {
                holder.pokeballImage.setVisibility(View.INVISIBLE);
            }

            return v;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_battle_party, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
