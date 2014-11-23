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
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.StaticData;
import com.tritonmon.model.PartyingPokemon;
import com.tritonmon.model.PokemonParty;
import com.tritonmon.model.UsersPokemon;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Party extends Activity {

    private Timer timer;
    private List<PartyingPokemon> pokemonList;

    private TextView out;
    private DragSortListView listView;
    private PartyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);

        try {
            StaticData.load(getAssets());
        } catch (ParseException e) {
            Log.e("Party", "Failed to load static assets");
            e.printStackTrace();
        }

        listView = (DragSortListView) findViewById(R.id.partyListView);

        listView.setDropListener(onDrop);
        listView.setRemoveListener(onRemove);

        pokemonList = new ArrayList<PartyingPokemon>();
        for (UsersPokemon partyPokemon : CurrentUser.getPokemonParty().getPokemonList()) {
            pokemonList.add(new PartyingPokemon(partyPokemon));
        }
        if (!CurrentUser.getPokemonStash().isEmpty()) {
            while (pokemonList.size() != PokemonParty.MAX_PARTY_SIZE) {
                pokemonList.add(null);
            }
            for (UsersPokemon stashedPokemon : CurrentUser.getPokemonStash()) {
                pokemonList.add(new PartyingPokemon(stashedPokemon));
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

        // TODO remove - timer to update debug statement
        out = (TextView) findViewById(R.id.out);
        MyTimerTask mytask;
        mytask = new MyTimerTask();
        timer = new Timer();
        timer.schedule(mytask, 0, 1000);
    }

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (from != to) {
                PartyingPokemon item = adapter.getItem(from);
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

    // TODO remove - timer task to update debug statement
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    out.setText(pokemonList.toString());
                }
            });

        }
    }

    private class ViewHolder {
        public ImageView pokemonImageView;
        public TextView healthBar;
        public TextView healthTextView;
    }

    private class PartyAdapter extends ArrayAdapter<PartyingPokemon> {
        public PartyAdapter(List<PartyingPokemon> list) {
            super(Party.this, R.layout.party_dslv_item_layout, R.id.dslv_nameText, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            if (v != convertView && v != null) {
                ViewHolder holder = new ViewHolder();
                holder.pokemonImageView = (ImageView) v.findViewById(R.id.dslv_pokemonImage);
                holder.healthBar = (TextView) v.findViewById(R.id.dslv_healthBar);
                holder.healthTextView = (TextView) v.findViewById(R.id.dslv_healthText);
                v.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) v.getTag();
            PartyingPokemon pokemon = getItem(position);
            holder.pokemonImageView.setImageResource(pokemon.getFrontImageResource(Party.this));
            holder.healthBar.setText("[=== health bar ===]");
            holder.healthTextView.setText(pokemon.getHealth() + " / " + pokemon.getMaxHealth() + " HP");

            if (position > PokemonParty.MAX_PARTY_SIZE - 1) {
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0); // grayscale color matrix
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                holder.pokemonImageView.setColorFilter(filter);
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
        for (PartyingPokemon partyingPokemon : pokemonList) {
            UsersPokemon usersPokemon = new UsersPokemon(partyingPokemon);
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
