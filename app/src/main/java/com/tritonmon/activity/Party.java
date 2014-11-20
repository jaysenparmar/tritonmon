package com.tritonmon.activity;

import android.app.Activity;
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
import com.tritonmon.global.Constant;
import com.tritonmon.global.ImageUtil;
import com.tritonmon.global.StaticData;
import com.tritonmon.staticmodel.Pokemon;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Party extends Activity {

    private TextView out;
    private Timer timer;
    private List<Pokemon> list;

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

        listView = (DragSortListView) findViewById(R.id.listview);

        listView.setDropListener(onDrop);
        listView.setRemoveListener(onRemove);

        list = new ArrayList<Pokemon>();
        for (Map.Entry<Integer,Pokemon> entry : Constant.pokemonData.entrySet()) {
            if (entry.getValue().getPokemonId() < 10) {
                list.add(entry.getValue());
            }
        }

        adapter = new PartyAdapter(list);
        listView.setAdapter(adapter);

        DragSortController controller = new DragSortController(listView);
        controller.setDragHandleId(R.id.drag_handle);
        //controller.setClickRemoveId(R.id.);
        controller.setRemoveEnabled(false);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_DRAG);
        //controller.setRemoveMode(removeMode);

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
                Pokemon item = adapter.getItem(from);
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
                    out.setText(list.toString());
                }
            });

        }
    }

    private class ViewHolder {
        public ImageView pokemonImageView;
        public TextView healthBar;
        public TextView healthTextView;
    }

    private class PartyAdapter extends ArrayAdapter<Pokemon> {
        public PartyAdapter(List<Pokemon> list) {
            super(Party.this, R.layout.item_layout, R.id.dslv_nameText, list);
        }

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
            Pokemon pokemon = getItem(position);
            holder.pokemonImageView.setImageResource(ImageUtil.getPokemonFrontImageResource(Party.this, pokemon.getPokemonId()));
            holder.healthBar.setText("Health Bar");
            holder.healthTextView.setText("0 / 0 HP");

            if (position > Constant.MAX_PARTY_SIZE - 1) {
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
}
