package com.tritonmon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tritonmon.asynctask.trades.GetTrades;
import com.tritonmon.asynctask.trades.TradePlayer;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.model.TradingUser;
import com.tritonmon.model.UsersPokemon;

import java.util.ArrayList;
import java.util.List;

public class TradingView extends Activity {

//    private ListView myProposedListView;
//    private ArrayAdapter<UsersPokemon> myProposedAdapter;
//    private List<UsersPokemon> myProposedPokemonList;
//
//    private ListView listingProposedListView;
//    private ArrayAdapter<UsersPokemon> listingProposedAdapter;
//    private List<UsersPokemon> listingProposedPokemonList;

    private Button submitOfferButton;

    private ImageView myProposedPokemonImage;
    private TextView myProposedPokemonText;
    private UsersPokemon myProposedPokemon;

    private ImageView listingProposedPokemonImage;
    private TextView listingProposedPokemonText;
    private UsersPokemon listingProposedPokemon;

    private ListView myListView;
    private ArrayAdapter<UsersPokemon> myAdapter;
    private List<UsersPokemon> myPokemonList;

    private ListView listingListView;
    private ArrayAdapter<UsersPokemon> listingAdapter;
    private List<UsersPokemon> listingPokemonList;

    private String tradingUsername;
    private TradingUser tradingUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_view);

        myProposedPokemonImage = (ImageView)findViewById(R.id.myProposedPokemonImage);
        myProposedPokemonText = (TextView)findViewById(R.id.myProposedPokemonText);
        listingProposedPokemonImage = (ImageView)findViewById(R.id.listingProposedPokemonImage);
        listingProposedPokemonText = (TextView)findViewById(R.id.listingProposedPokemonText);

        submitOfferButton = (Button)findViewById(R.id.submitOfferButton);
        submitOfferButton.setOnClickListener(submitOffer);

        myPokemonList = new ArrayList<UsersPokemon>();
        listingPokemonList = new ArrayList<UsersPokemon>();

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("listingPokemon")) {
            Log.e("TradingView", "have listing pokemon! as should be..");
            listingPokemonList = getIntent().getExtras().getParcelableArrayList("listingPokemon");
        }
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("tradingUsername")) {
            Log.e("TradingView", "have user to trade with! as should be..");
            tradingUsername = getIntent().getExtras().getString("tradingUsername");
        }

//        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("tradingUser")) {
//            Log.e("TradingView", "have user to trade with! as should be..");
//            tradingUser = (TradingUser)(getIntent().getExtras().get("tradingUser"));
//            tradingUsername = tradingUser.getUsername();
//            listingPokemonList = tradingUser.getUsersPokemon();
//        }

        myListView = (ListView) findViewById(R.id.offerPokemonListView);
        myListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UsersPokemon myPokemon = (UsersPokemon) myListView.getItemAtPosition(position);
                myAdapter.notifyDataSetChanged();

                Toast.makeText(
                        getApplicationContext(),
                        "selected " + myPokemon.getName(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        List<UsersPokemon> allUsersPokemon = CurrentUser.getPokemonParty().getPokemonList();
        allUsersPokemon.addAll(CurrentUser.getPokemonStash());
        myAdapter = new MyTradingViewAdapter(allUsersPokemon);
        myListView.setAdapter(myAdapter);

        listingListView = (ListView) findViewById(R.id.listingPokemonListView);
        listingListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UsersPokemon listingPokemon = (UsersPokemon) listingListView.getItemAtPosition(position);
                listingAdapter.notifyDataSetChanged();

                Toast.makeText(
                        getApplicationContext(),
                        "selected " + listingPokemon.getName(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        listingAdapter = new ListingTradingViewAdapter(listingPokemonList);
        listingListView.setAdapter(listingAdapter);
    }

    private class ViewHolder {
        public ImageButton tradingPokemonImageButton;
        public TextView tradingPokemonLevel;
    }

    private class MyTradingViewAdapter extends ArrayAdapter<UsersPokemon> {
        public MyTradingViewAdapter(List<UsersPokemon> list) {
            // wtf do i put as the 3rd arg
            super(TradingView.this, R.layout.trading_view_item_layout, R.id.tradingPokemonLevel, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            if (v != convertView && v != null) {
                final ViewHolder holder = new ViewHolder();

                holder.tradingPokemonImageButton = (ImageButton) v.findViewById(R.id.tradingPokemonImageButton);
                holder.tradingPokemonLevel = (TextView) v.findViewById(R.id.tradingPokemonLevel);
                v.setTag(holder);
            }

            final ViewHolder holder = (ViewHolder) v.getTag();
            final UsersPokemon currPokemon = getItem(position);

            holder.tradingPokemonImageButton.setImageResource(currPokemon.getFrontImageResource(getApplicationContext()));
            holder.tradingPokemonImageButton.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myProposedPokemon = currPokemon;
                    myProposedPokemonImage.setImageResource(currPokemon.getFrontImageResource(getApplicationContext()));
                    myProposedPokemonText.setText(currPokemon.getName());
                }
            });

            holder.tradingPokemonLevel.setText(Integer.toString(currPokemon.getLevel()));
            return v;
        }
    }

    private class ListingTradingViewAdapter extends ArrayAdapter<UsersPokemon> {
        public ListingTradingViewAdapter(List<UsersPokemon> list) {
            // wtf do i put as the 3rd arg
            super(TradingView.this, R.layout.trading_view_item_layout, R.id.tradingPokemonLevel, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            if (v != convertView && v != null) {
                final ViewHolder holder = new ViewHolder();

                holder.tradingPokemonImageButton = (ImageButton) v.findViewById(R.id.tradingPokemonImageButton);
                holder.tradingPokemonLevel = (TextView) v.findViewById(R.id.tradingPokemonLevel);
                v.setTag(holder);
            }

            final ViewHolder holder = (ViewHolder) v.getTag();
            final UsersPokemon currPokemon = getItem(position);

            holder.tradingPokemonImageButton.setImageResource(currPokemon.getFrontImageResource(getApplicationContext()));
            holder.tradingPokemonImageButton.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listingProposedPokemon = currPokemon;
                    listingProposedPokemonImage.setImageResource(currPokemon.getFrontImageResource(getApplicationContext()));
                    listingProposedPokemonText.setText(currPokemon.getName());
                }
            });

            holder.tradingPokemonLevel.setText(Integer.toString(currPokemon.getLevel()));
            return v;
        }
    }

    View.OnClickListener submitOffer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (myProposedPokemon == null || listingProposedPokemon == null) {
                // TODO: create dialog box saying yo wtf its empty
            } else {
                // TODO: create dialog box saying yo you sure bro?
                new TradePlayer(CurrentUser.getUsername(), myProposedPokemon, tradingUsername, listingProposedPokemon).execute();
                Intent i = new Intent(getApplicationContext(), TrainerCard.class);
                startActivity(i);
            }
//            Intent i = new Intent(getApplicationContext(), BattleParty.class);
//            i.putExtra("selectedPokemonIndex", selectedPokemonIndex);
//            startActivityForResult(i, Constant.REQUEST_CODE_BATTLE_PARTY);
        }
    };

    @Override
    public void onBackPressed() {
        new GetTrades().execute();
        Intent i = new Intent(getApplicationContext(), TradingList.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trading_view, menu);
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
