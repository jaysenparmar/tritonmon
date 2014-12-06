package com.tritonmon.fragment.tabs;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tritonmon.activity.R;
import com.tritonmon.asynctask.trades.SetViewedTrade;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ImageUtil;
import com.tritonmon.model.Trade;

import java.util.ArrayList;
import java.util.List;

public class OffersInTab extends Fragment {
    private ListView listView;
    private ArrayAdapter<Trade> adapter;

    private View detailedPokemonFragment;
    boolean isDetailedDialogShown;

    private List<Trade> offersIn;

    // TODO: add cancel button?
    // TODO: show moves when click on the pokemon
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_offers_in, container, false);
        rootView.setOnTouchListener(touchListener);

        listView = (ListView) rootView.findViewById(R.id.offersInListView);

        isDetailedDialogShown = false;
        detailedPokemonFragment = rootView.findViewById(R.id.detailedPokemonFragment);
        detailedPokemonFragment.setVisibility(View.INVISIBLE);

//        detailedPokemonRelativeLayout.setVisibility(View.INVISIBLE);
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
        for (Trade trade : CurrentUser.getTrades()) {
            if (CurrentUser.getUsersId() == trade.getListerUsersId() && !trade.isDeclined() && !trade.isAccepted()) {
                if (trade != null) {
                    Log.e("OffersInTab", "found a valid trade: " + trade.toString());
                    offersIn.add(trade);
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
        public TextView myInfoTextIn;
        public TextView theirInfoTextIn;
        public TextView tradeStatusTextIn;
        public Button declineInButton;
        public Button acceptInButton;
    }

    private class OffersInAdapter extends ArrayAdapter<Trade> {

        public OffersInAdapter(List<Trade> list) {
            // this 3rd arg is pretty useless lol..
            super(getActivity().getApplicationContext(), R.layout.offers_in_item_layout, R.id.theirInfoTextIn, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            if (v != convertView && v != null) {
                final ViewHolder holder = new ViewHolder();

                holder.myPokemonImageIn = (ImageButton) v.findViewById(R.id.myPokemonImageIn);
                holder.theirPokemonImageIn = (ImageButton) v.findViewById(R.id.theirPokemonImageIn);
                holder.myInfoTextIn = (TextView) v.findViewById(R.id.myInfoTextIn);
                holder.theirInfoTextIn = (TextView) v.findViewById(R.id.theirInfoTextIn);
                holder.tradeStatusTextIn = (TextView) v.findViewById(R.id.tradeStatusTextIn);
                holder.declineInButton = (Button) v.findViewById(R.id.declineInButton);
                holder.acceptInButton = (Button) v.findViewById(R.id.acceptInButton);
                v.setTag(holder);
            }

            final ViewHolder holder = (ViewHolder) v.getTag();
            final Trade trade = getItem(position);

            holder.myPokemonImageIn.setImageResource(ImageUtil.getPokemonFrontImageResource(getActivity(), trade.getOfferPokemonId()));
            holder.theirPokemonImageIn.setImageResource(ImageUtil.getPokemonFrontImageResource(getActivity(), trade.getListerPokemonId()));
            holder.myInfoTextIn.setText("Level " + Integer.toString(trade.getOfferLevel()) + " " + Constant.pokemonData.get(trade.getOfferPokemonId()).getName());
            holder.theirInfoTextIn.setText("Level " + Integer.toString(trade.getListerLevel()) + " " + Constant.pokemonData.get(trade.getListerPokemonId()).getName());
            holder.tradeStatusTextIn.setText(Constant.userData.get(trade.getOffererUsersId()).getUsername() + " is waiting on you");

            holder.myPokemonImageIn.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isDetailedDialogShown = true;
                    detailedPokemonFragment.setVisibility(View.VISIBLE);
//                    detailedPokemonFragment.findViewById(R.id.abc)
//                    Fragment fragment2 = new DetailedPokemonFragment();
//                    FragmentManager fragmentManager = getFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(android.R.id.content, fragment2);
//                    fragmentTransaction.commit();
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

}
