package com.tritonmon.fragment.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tritonmon.activity.R;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ImageUtil;
import com.tritonmon.model.Trade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OffersOutTab extends Fragment {
    private ListView listView;
    private ArrayAdapter<Trade> adapter;

    private int perUnseenTrade;
    private int perUnseenDeclinedTrade;
    private int perUnseenAcceptanceTrade;
    private int perDialogBox;
    private Set<String> usersTradingWith = new HashSet<String>();
    private List<Trade> offersOut;

    // TODO: add cancel button?
    // TODO: show moves when click on the pokemon
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_offers_out, container, false);

        listView = (ListView) rootView.findViewById(R.id.offersOutListView);

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
        for (Trade trade : CurrentUser.getTrades()) {
            if (CurrentUser.getUsername().equals(trade.getOffererUsersId()) && !trade.isDeclined() && !trade.isAccepted()) {
                if (trade != null) {
                    Log.e("OffersOutTab", "found a valid trade: " + trade.toString());
                    offersOut.add(trade);
                }
            }
        }
        adapter = new OffersOutAdapter(offersOut);
        listView.setAdapter(adapter);
        return rootView;
    }

    private class ViewHolder {
        public ImageButton myPokemonImageOut;
        public ImageButton theirPokemonImageOut;
        public TextView myInfoTextOut;
        public TextView theirInfoTextOut;
        public TextView tradeStatusTextOut;
    }

    private class OffersOutAdapter extends ArrayAdapter<Trade> {
        public OffersOutAdapter(List<Trade> list) {
            // this 3rd arg is pretty useless lol..
            super(getActivity().getApplicationContext(), R.layout.offers_out_item_layout, R.id.tradingUsername, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            for (Trade trade : offersOut) {
                if (trade == null) {
                    Log.e("OffersOut", "nulltrade");
                } else {
                    Log.e("Offersout", "non null trade: " + offersOut.toString());
                }
            }
            View v = super.getView(position, convertView, parent);

            if (v != convertView && v != null) {
                final ViewHolder holder = new ViewHolder();

                holder.myPokemonImageOut = (ImageButton) v.findViewById(R.id.myPokemonImageOut);
                holder.theirPokemonImageOut = (ImageButton) v.findViewById(R.id.theirPokemonImageOut);
                holder.myInfoTextOut = (TextView) v.findViewById(R.id.myInfoTextOut);
                holder.theirInfoTextOut = (TextView) v.findViewById(R.id.theirInfoTextOut);
                holder.tradeStatusTextOut = (TextView) v.findViewById(R.id.tradeStatusTextOut);
                v.setTag(holder);
            }

            final ViewHolder holder = (ViewHolder) v.getTag();
            final Trade trade = getItem(position);

            holder.myPokemonImageOut.setImageResource(ImageUtil.getPokemonFrontImageResource(getActivity(), trade.getOfferPokemonId()));
            holder.theirPokemonImageOut.setImageResource(ImageUtil.getPokemonFrontImageResource(getActivity(), trade.getListerPokemonId()));
            holder.myInfoTextOut.setText("Level " + Integer.toString(trade.getOfferLevel()) + " " + Constant.pokemonData.get(trade.getOfferPokemonId()).getName());
            holder.theirInfoTextOut.setText("Level " + Integer.toString(trade.getListerLevel()) + " " + Constant.pokemonData.get(trade.getListerPokemonId()).getName());
            holder.tradeStatusTextOut.setText("TODO");

            return v;
        }
    }

}
