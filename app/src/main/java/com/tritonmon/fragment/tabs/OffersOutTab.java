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

public class OffersOutTab extends Fragment {
    private ListView listView;
    private ArrayAdapter<Trade> adapter;

    private View detailedPokemonFragment;
    boolean isDetailedDialogShown;

    private List<Trade> offersOut;

    private ImageView pokemonImageDetailed;
    private TextView pokemonInfoDetailed;

    // offerer then lister
    private Map<Trade, List<UsersPokemon>> tradeToUsersPokemonList;

    // TODO: add cancel button?
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_offers_out, container, false);
        rootView.setOnTouchListener(touchListener);

        listView = (ListView) rootView.findViewById(R.id.offersOutListView);

        pokemonImageDetailed = (ImageView) rootView.findViewById(R.id.pokemonImageDetailed);
        pokemonInfoDetailed = (TextView) rootView.findViewById(R.id.pokemonInfoDetailed);

        isDetailedDialogShown = false;
        detailedPokemonFragment = rootView.findViewById(R.id.detailedPokemonFragment);
        detailedPokemonFragment.setVisibility(View.INVISIBLE);
        detailedPokemonFragment.setOnTouchListener(touchListener);

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
        offersOut = new ArrayList<Trade>();
        tradeToUsersPokemonList = new HashMap<Trade, List<UsersPokemon>>();
        for (Trade trade : CurrentUser.getTrades()) {
            if (CurrentUser.getUsersId() == trade.getOffererUsersId() && !trade.isDeclined() && !trade.isAccepted()) {
                if (trade != null) {
                    Log.e("OffersOutTab", "found a valid trade: " + trade.toString());
                    offersOut.add(trade);
                    new GetUsersPokemonByUsersPokemonId(trade).execute();
                }

            }
        }
//        new PopulateTradingUsers().execute();
        adapter = new OffersOutAdapter(offersOut);
        listView.setAdapter(adapter);
        return rootView;
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            Log.e("offersintab", "in touchlistener");
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                Log.e("offersintab", "in touchlistener");
                if (isDetailedDialogShown) {
                    isDetailedDialogShown = false;
                    detailedPokemonFragment.setVisibility(View.INVISIBLE);
                }
            }
            return v.onTouchEvent(event);
        }
    };

    private class ViewHolder {
        public ImageButton myPokemonImageOut;
        public ImageButton theirPokemonImageOut;
        public TextView tradeStatusTextOut;
    }

    private class OffersOutAdapter extends ArrayAdapter<Trade> {

        public OffersOutAdapter(List<Trade> list) {
            // this 3rd arg is pretty useless lol..
            super(getActivity().getApplicationContext(), R.layout.offers_out_item_layout, R.id.tradeStatusTextOut, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            if (v != convertView && v != null) {
                final ViewHolder holder = new ViewHolder();

                holder.myPokemonImageOut = (ImageButton) v.findViewById(R.id.myPokemonImageOut);
                holder.theirPokemonImageOut = (ImageButton) v.findViewById(R.id.theirPokemonImageOut);
                holder.tradeStatusTextOut = (TextView) v.findViewById(R.id.tradeStatusTextOut);
                v.setTag(holder);
            }

            final ViewHolder holder = (ViewHolder) v.getTag();
            final Trade trade = getItem(position);

            holder.myPokemonImageOut.setImageResource(ImageUtil.getPokemonFrontImageResource(getActivity(), trade.getOfferPokemonId()));
            holder.theirPokemonImageOut.setImageResource(ImageUtil.getPokemonFrontImageResource(getActivity(), trade.getListerPokemonId()));
            holder.tradeStatusTextOut.setText("Waiting on " + Constant.userData.get(trade.getListerUsersId()).getName());

            holder.myPokemonImageOut.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isDetailedDialogShown = true;
                    pokemonImageDetailed.setImageResource(ImageUtil.getPokemonFrontImageResource(getActivity(), trade.getOfferPokemonId()));
                    pokemonInfoDetailed.setText(TradingUtil.getDetailedPokemonInfo(tradeToUsersPokemonList.get(trade).get(0)));
                    detailedPokemonFragment.setVisibility(View.VISIBLE);
                }
            });

            holder.theirPokemonImageOut.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isDetailedDialogShown = true;
                    pokemonImageDetailed.setImageResource(ImageUtil.getPokemonFrontImageResource(getActivity(), trade.getListerPokemonId()));
                    pokemonInfoDetailed.setText(TradingUtil.getDetailedPokemonInfo(tradeToUsersPokemonList.get(trade).get(1)));
                    detailedPokemonFragment.setVisibility(View.VISIBLE);
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
