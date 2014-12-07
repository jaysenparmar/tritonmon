package com.tritonmon.fragment.tabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.google.gson.reflect.TypeToken;
import com.tritonmon.activity.R;
import com.tritonmon.asynctask.trades.SetViewedTrade;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.singleton.MyGson;
import com.tritonmon.global.singleton.MyHttpClient;
import com.tritonmon.global.util.ImageUtil;
import com.tritonmon.global.util.TradingUtil;
import com.tritonmon.model.Trade;
import com.tritonmon.model.UsersPokemon;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OffersInTab extends Fragment {
    private ListView listView;
    private ArrayAdapter<Trade> adapter;

    private View detailedPokemonFragment;
    boolean isDetailedDialogShown;

    private List<Trade> offersIn;

    private ImageView pokemonImageDetailed;
    private TextView pokemonInfoDetailed;

    // offerer then lister
    private Map<Trade, List<UsersPokemon>> tradeToUsersPokemonList;

    // TODO: add cancel button?
    // TODO: show moves when click on the pokemon
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_offers_in, container, false);
        rootView.setOnTouchListener(touchListener);

        listView = (ListView) rootView.findViewById(R.id.offersInListView);

        pokemonImageDetailed = (ImageView) rootView.findViewById(R.id.pokemonImageDetailed);
        pokemonInfoDetailed = (TextView) rootView.findViewById(R.id.pokemonInfoDetailed);

        isDetailedDialogShown = false;
        detailedPokemonFragment = rootView.findViewById(R.id.detailedPokemonFragment);
        detailedPokemonFragment.setVisibility(View.INVISIBLE);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trade trade = (Trade) listView.getItemAtPosition(position);
                adapter.notifyDataSetChanged();

                Toast.makeText(
                        getActivity(),
                        "selected something",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        offersIn = new ArrayList<Trade>();
        tradeToUsersPokemonList = new HashMap<Trade, List<UsersPokemon>>();
        for (Trade trade : CurrentUser.getTrades()) {
            if (CurrentUser.getUsersId() == trade.getListerUsersId() && !trade.isDeclined() && !trade.isAccepted()) {
                if (trade != null) {
                    Log.e("OffersInTab", "found a valid trade: " + trade.toString());
                    offersIn.add(trade);
                    new GetUsersPokemonByUsersPokemonId(trade).execute();
                }
            }
        }
//        new PopulateTradingUsers().execute();
        adapter = new OffersInAdapter(offersIn);
        listView.setAdapter(adapter);
        return rootView;
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (isDetailedDialogShown) {
                    isDetailedDialogShown = false;
                    detailedPokemonFragment.setVisibility(View.INVISIBLE);
                }
            }
            return v.onTouchEvent(event);
        }
    };

    private class ViewHolder {
        public ImageButton myPokemonImageIn;
        public ImageButton theirPokemonImageIn;
        public TextView tradeStatusTextIn;
        public Button declineInButton;
        public Button acceptInButton;
    }

    // lazy way
    private class OffersInAdapter extends ArrayAdapter<Trade> {

        public OffersInAdapter(List<Trade> list) {
            // this 3rd arg is pretty useless lol..
            super(getActivity().getApplicationContext(), R.layout.offers_in_item_layout, R.id.tradeStatusTextIn, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            if (v != convertView && v != null) {
                final ViewHolder holder = new ViewHolder();

                holder.myPokemonImageIn = (ImageButton) v.findViewById(R.id.myPokemonImageIn);
                holder.theirPokemonImageIn = (ImageButton) v.findViewById(R.id.theirPokemonImageIn);
                holder.tradeStatusTextIn = (TextView) v.findViewById(R.id.tradeStatusTextIn);
                holder.declineInButton = (Button) v.findViewById(R.id.declineInButton);
                holder.acceptInButton = (Button) v.findViewById(R.id.acceptInButton);
                v.setTag(holder);
            }

            final ViewHolder holder = (ViewHolder) v.getTag();
            final Trade trade = getItem(position);
//            new GetUsersPokemonByUsersPokemonId(trade);
            holder.myPokemonImageIn.setImageResource(ImageUtil.getPokemonFrontImageResource(getActivity(), trade.getOfferPokemonId()));
            holder.theirPokemonImageIn.setImageResource(ImageUtil.getPokemonFrontImageResource(getActivity(), trade.getListerPokemonId()));
            holder.tradeStatusTextIn.setText(Constant.userData.get(trade.getOffererUsersId()).getUsername() + " is waiting on you");

            holder.myPokemonImageIn.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isDetailedDialogShown = true;
                    pokemonImageDetailed.setImageResource(ImageUtil.getPokemonFrontImageResource(getActivity(), trade.getOfferPokemonId()));
                    pokemonInfoDetailed.setText(TradingUtil.getDetailedPokemonInfo(tradeToUsersPokemonList.get(trade).get(0)));
                    detailedPokemonFragment.setVisibility(View.VISIBLE);
                }
            });

            holder.theirPokemonImageIn.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isDetailedDialogShown = true;
                    pokemonImageDetailed.setImageResource(ImageUtil.getPokemonFrontImageResource(getActivity(), trade.getListerPokemonId()));
                    pokemonInfoDetailed.setText(TradingUtil.getDetailedPokemonInfo(tradeToUsersPokemonList.get(trade).get(1)));
                    detailedPokemonFragment.setVisibility(View.VISIBLE);
                }
            });

            holder.declineInButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("tradinglisthandler", "trade DECLINED");
                    new SetViewedTrade(trade, SetViewedTrade.Choices.DECLINED).execute();
                }
            });

            holder.acceptInButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("tradinglisthandler", "trade ACCEPTED");
                    new SetViewedTrade(trade, SetViewedTrade.Choices.ACCEPTED).execute();
                }
            });

            return v;
        }
    }

    private class GetUsersPokemonByUsersPokemonId extends AsyncTask<Void, Void, List<UsersPokemon>> {

        private Trade trade;

        public GetUsersPokemonByUsersPokemonId(Trade trade) {
            this.trade = trade;
        }

        @Override
        protected List<UsersPokemon> doInBackground(Void... params) {

            if (CurrentUser.isLoggedIn()) {
                List<UsersPokemon> pokemon1 = null;
                List<UsersPokemon> pokemon2 = null;
                // lazy way since not sure if can order json results
                Log.d("asynctask/GetUsersPokemonByUsersPokemonId", "STARTED ASYNC TASK");
                HttpResponse response = MyHttpClient.get(Constant.SERVER_URL + "/userspokemon/users_pokemon_id=" + Integer.toString(trade.getOfferUsersPokemonId()));
                if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                    String json = MyHttpClient.getJson(response);
                    pokemon1 = MyGson.getInstance().fromJson(json, new TypeToken<List<UsersPokemon>>() {
                    }.getType());
                }
                response = MyHttpClient.get(Constant.SERVER_URL + "/userspokemon/users_pokemon_id=" + Integer.toString(trade.getListerUsersPokemonId()));
                if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                    String json = MyHttpClient.getJson(response);
                    pokemon2 = MyGson.getInstance().fromJson(json, new TypeToken<List<UsersPokemon>>() {
                    }.getType());
                }
                return Arrays.asList(pokemon1.get(0), pokemon2.get(0));
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<UsersPokemon> result) {
            Log.d("asynctask/GetUsersPokemonByUsersPokemonId", "FINISHED ASYNC TASK");
            tradeToUsersPokemonList.put(trade, result);
        }
    }
}
