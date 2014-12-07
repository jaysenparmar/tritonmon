package com.tritonmon.activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.tritonmon.fragment.dialog.ConfirmTradeDialog;
import com.tritonmon.fragment.dialog.InvalidTradeDialog;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.util.ImageUtil;
import com.tritonmon.global.util.TradingUtil;
import com.tritonmon.model.TradingUser;
import com.tritonmon.model.UsersPokemon;

import java.util.ArrayList;
import java.util.List;

public class TradingView extends FragmentActivity implements ConfirmTradeDialog.NoticeDialogListener, InvalidTradeDialog.NoticeDialogListener {

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

    private int tradingUsersId;
    private TradingUser tradingUser;

    private View detailedPokemonFragment;
    boolean isDetailedDialogShown;

    private ImageView pokemonImageDetailed;
    private TextView pokemonInfoDetailed;

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

//        pokemonImageDetailed = (ImageView) findViewById(R.id.pokemonImageDetailed);
//        pokemonInfoDetailed = (TextView) findViewById(R.id.pokemonInfoDetailed);
//
//        isDetailedDialogShown = false;
//        detailedPokemonFragment = findViewById(R.id.detailedPokemonFragment);
//        detailedPokemonFragment.setVisibility(View.INVISIBLE);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("listingPokemon")) {
            Log.e("TradingView", "have listing pokemon! as should be..");
            listingPokemonList = getIntent().getExtras().getParcelableArrayList("listingPokemon");
        }
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("tradingUsersId")) {
            Log.e("TradingView", "have user to trade with! as should be..");
            tradingUsersId = getIntent().getExtras().getInt("tradingUsersId");
            Log.e("TradingView", "tradingusersid: " + tradingUsersId);
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

    public void showInvalidTradeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new InvalidTradeDialog();
        dialog.show(getFragmentManager(), "InvalidTradeDialog");
    }

    @Override
    public void onInvalidTradeDialogPositiveClick(DialogFragment dialog) {
    }

    public void showConfirmTradeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ConfirmTradeDialog();
        dialog.show(getFragmentManager(), "ConfirmTradeDialog");
    }

    @Override
    public void onConfirmTradeDialogPositiveClick(DialogFragment dialog) {
        new TradePlayer(CurrentUser.getUsersId(), myProposedPokemon, tradingUsersId, listingProposedPokemon).execute();
        Intent i = new Intent(getApplicationContext(), TradingListHandler.class);
        startActivity(i);
    }

    @Override
    public void onConfirmTradeDialogNegativeClick(DialogFragment dialog) {
        Log.e("tradingview", "trade averted");
    }

//    View.OnTouchListener touchListener = new View.OnTouchListener() {
//        public boolean onTouch(View v, MotionEvent event) {
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                if (isDetailedDialogShown) {
//                    isDetailedDialogShown = false;
//                    detailedPokemonFragment.setVisibility(View.INVISIBLE);
//                }
//            }
//            return v.onTouchEvent(event);
//        }
//    };

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
                    String movesKnown = "";
                    for (Integer ele : currPokemon.getMoves()) {
                        if (ele != null) {
                            movesKnown+= Constant.movesData.get(ele).getName()+"\n";
                        }
                    }

//                    myProposedPokemonImage.setOnClickListener(new ImageButton.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            isDetailedDialogShown = true;
//                            pokemonImageDetailed.setImageResource(ImageUtil.getPokemonFrontImageResource(getApplicationContext(), currPokemon.getPokemonId()));
//                            pokemonInfoDetailed.setText(TradingUtil.getDetailedPokemonInfo(currPokemon));
//                            detailedPokemonFragment.setVisibility(View.VISIBLE);
//                        }
//                    });

                    myProposedPokemonText.setText(TradingUtil.getDetailedPokemonInfo(currPokemon));
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
                    String movesKnown = "";
                    for (Integer ele : currPokemon.getMoves()) {
                        if (ele != null) {
                            movesKnown+= Constant.movesData.get(ele).getName()+"\n";
                        }
                    }
//                    listingProposedPokemonImage.setOnClickListener(new ImageButton.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            isDetailedDialogShown = true;
//                            pokemonImageDetailed.setImageResource(ImageUtil.getPokemonFrontImageResource(getApplicationContext(), currPokemon.getPokemonId()));
//                            pokemonInfoDetailed.setText(TradingUtil.getDetailedPokemonInfo(currPokemon));
//                            detailedPokemonFragment.setVisibility(View.VISIBLE);
//                        }
//                    });

                    listingProposedPokemonText.setText(currPokemon.getName() +" Level: " + Integer.toString(currPokemon.getLevel()) + "\nMoves known:\n" + movesKnown);
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
                showInvalidTradeDialog();
            } else {
                showConfirmTradeDialog();
            }
        }
    };

    @Override
    public void onBackPressed() {
        new GetTrades().execute();
        Intent i = new Intent(getApplicationContext(), TradingListHandler.class);
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
