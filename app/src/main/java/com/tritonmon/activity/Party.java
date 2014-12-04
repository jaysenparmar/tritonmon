package com.tritonmon.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ProgressBarUtil;
import com.tritonmon.global.StaticData;
import com.tritonmon.toast.TritonmonToast;
import com.tritonmon.model.PokemonParty;
import com.tritonmon.model.UsersPokemon;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Party extends Activity {

    private DragSortListView listView;
    private PartyAdapter adapter;

    private List<UsersPokemon> pokemonList;
    private ColorMatrixColorFilter grayScaleFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);

        try {
            StaticData.load(getAssets());
        } catch (ParseException e) {
            Log.e("Party", "Failed to load static assets");
            e.printStackTrace();
            TritonmonToast.makeText(this, "Failed to load static assets", Toast.LENGTH_LONG);
        }

        // create grayscale color filter
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0); // grayscale color matrix
        grayScaleFilter = new ColorMatrixColorFilter(matrix);

        listView = (DragSortListView) findViewById(R.id.partyListView);
        listView.setDropListener(onDrop);
        listView.setRemoveListener(onRemove);

        pokemonList = new ArrayList<UsersPokemon>();
        for (UsersPokemon partyPokemon : CurrentUser.getPokemonParty().getPokemonList()) {
            pokemonList.add(partyPokemon);
        }
        if (!CurrentUser.getPokemonStash().isEmpty()) {
            while (pokemonList.size() != PokemonParty.MAX_PARTY_SIZE) {
                pokemonList.add(null);
            }
            for (UsersPokemon stashedPokemon : CurrentUser.getPokemonStash()) {
                pokemonList.add(stashedPokemon);
            }
        }

        adapter = new PartyAdapter(pokemonList);
        listView.setAdapter(adapter);

        DragSortController controller = new PartyDragController(listView);
        controller.setDragHandleId(R.id.drag_content);
        controller.setRemoveEnabled(false);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_DRAG);

        listView.setFloatViewManager(controller);
        listView.setOnTouchListener(controller);
        listView.setDragEnabled(true);
    }

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (from != to) {
                UsersPokemon item = adapter.getItem(from);
                adapter.remove(item);
                adapter.insert(item, to);
            }
        }
    };

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
        @Override
        public void remove(int which) {
            adapter.remove(adapter.getItem(which));
        }
    };

    private class ViewHolder {
        public ImageView pokemonImageView;
        public TextView nameTextView;
        public TextView levelTextView;
        public TextView healthTextView;
        public ProgressBar healthBar;
        public ProgressBar xpBar;
    }

    private class PartyAdapter extends ArrayAdapter<UsersPokemon> {
        public PartyAdapter(List<UsersPokemon> list) {
            super(Party.this, R.layout.party_dslv_item_layout, R.id.dslv_nameText, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            if (v != convertView && v != null) {
                ViewHolder holder = new ViewHolder();
                holder.pokemonImageView = (ImageView) v.findViewById(R.id.dslv_pokemonImage);
                holder.nameTextView = (TextView) v.findViewById(R.id.dslv_nameText);
                holder.levelTextView = (TextView) v.findViewById(R.id.dslv_levelText);
                holder.healthTextView = (TextView) v.findViewById(R.id.dslv_healthText);
                holder.healthBar = (ProgressBar) v.findViewById(R.id.dslv_healthBar);
                holder.xpBar = (ProgressBar) v.findViewById(R.id.dslv_xpBar);
                v.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) v.getTag();
            UsersPokemon pokemon = getItem(position);
            holder.pokemonImageView.setImageResource(pokemon.getFrontImageResource(getApplicationContext()));
            holder.nameTextView.setText(pokemon.getName());
            holder.levelTextView.setText("Lvl " + pokemon.getLevel());
            holder.healthTextView.setText(pokemon.getHealth() + " / " + pokemon.getMaxHealth() + " HP");

            ProgressBarUtil.updateHealthBar(getApplicationContext(), holder.healthBar, pokemon.getHealth(), pokemon.getMaxHealth());
            holder.xpBar.setProgress(ProgressBarUtil.getPercentage(pokemon.getCurrentXPBar(), pokemon.getTotalXPBar()));

            if (position > PokemonParty.MAX_PARTY_SIZE - 1) {
                holder.pokemonImageView.setColorFilter(grayScaleFilter);
                v.setBackgroundColor(Color.LTGRAY);
            }
            else {
                holder.pokemonImageView.clearColorFilter();
                v.setBackgroundColor(Color.TRANSPARENT);
            }

            return v;
        }
    }

    private class PartyDragController extends DragSortController {

        DragSortListView dragSortListView;

        public PartyDragController(DragSortListView dslv) {
            super(dslv);
            setDragHandleId(R.id.drag_handle);
            dragSortListView = dslv;
        }

        @Override
        public View onCreateFloatView(int position) {
            View v = adapter.getView(position, null, dragSortListView);
            v.setBackgroundColor(Color.YELLOW);
            return v;
        }

        @Override
        public void onDestroyFloatView(View floatView) {
            //do nothing; block super from crashing
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_party, menu);
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

    @Override
    public void onBackPressed() {
        List<UsersPokemon> party = new ArrayList<UsersPokemon>();
        List<UsersPokemon> stash = new ArrayList<UsersPokemon>();
        int slotNum = 0;
        for (UsersPokemon usersPokemon : pokemonList) {
            if (slotNum < PokemonParty.MAX_PARTY_SIZE) {
                party.add(usersPokemon);
            }
            else {
                stash.add(usersPokemon);
            }
            slotNum++;
        }

        CurrentUser.setPokemonParty(party);
        CurrentUser.setPokemonStash(stash);
        Intent i = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(i);
    }
}
