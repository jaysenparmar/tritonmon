package com.tritonmon.fragment.tabs;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.tritonmon.activity.R;
import com.tritonmon.activity.TradingView;
import com.tritonmon.asynctask.trades.SetViewedDecisions;
import com.tritonmon.fragment.dialog.ViewAcceptanceDialog;
import com.tritonmon.fragment.dialog.ViewTradeDialog;
import com.tritonmon.fragment.dialog.ViewDeclineDialog;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.util.ImageUtil;
import com.tritonmon.global.singleton.MyGson;
import com.tritonmon.global.singleton.MyHttpClient;
import com.tritonmon.model.Trade;
import com.tritonmon.model.TradingUser;
import com.tritonmon.model.User;
import com.tritonmon.model.UsersPokemon;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TradingListTab extends Fragment {
    private ListView listView;
    private ArrayAdapter<TradingUser> adapter;
    private List<TradingUser> tradingUsersList;

    private int perUnseenTrade;
    private int perUnseenDeclinedTrade;
    private int perUnseenAcceptanceTrade;
    private int perDialogBox;
    private Set<Integer> usersTradingWith = new HashSet<Integer>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_trading_list, container, false);

        listView = (ListView) rootView.findViewById(R.id.tradingUserListView);
        tradingUsersList = new ArrayList<TradingUser>();
//        tradingUsersList.add(new TradingUser(CurrentUser.getUser(), 5, 10));

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TradingUser TradingUser = (TradingUser) listView.getItemAtPosition(position);
                adapter.notifyDataSetChanged();

                Toast.makeText(
                        getActivity(),
                        "selected " + TradingUser.getUsername(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        perUnseenTrade = 0;
        perDialogBox = 0;
        perUnseenDeclinedTrade = 0;
        perUnseenAcceptanceTrade = 0;

        // maybe move this line
        new PopulateTradingUsers().execute();
//        adapter = new tradinglistAdapter(tradingUsersList);
//        listView.setAdapter(adapter);

        for (Trade trade : CurrentUser.getTrades()) {
            if (trade.getListerUsersId() == CurrentUser.getUsersId() && !trade.isSeenOffer()) {
                showViewTradeDialog(trade);
            }
            usersTradingWith.add(trade.getOffererUsersId());
            usersTradingWith.add(trade.getListerUsersId());
        }
        for (Trade trade : CurrentUser.getTrades()) {
            if (trade.getOffererUsersId() == CurrentUser.getUsersId() && trade.isDeclined() && !trade.isSeenDecline()) {
                showViewDeclineDialog(Integer.toString(trade.getListerUsersId()));
                perUnseenDeclinedTrade++;
            }
        }
        for (Trade trade : CurrentUser.getTrades()) {
            if (trade.getOffererUsersId() == CurrentUser.getUsersId() && trade.isAccepted() && !trade.isSeenAcceptance()) {
                Log.d("trading list", "in the if");
                showViewAcceptanceDialog(Integer.toString(trade.getListerUsersId()));
                perUnseenAcceptanceTrade++;
            }
        }

//        saving a server call!
        if (CurrentUser.getTrades() != null && !CurrentUser.getTrades().isEmpty()) {
            new SetViewedDecisions(CurrentUser.getUsersId()).execute();
        }

        return rootView;
    }

    public void showViewTradeDialog(Trade trade) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ViewTradeDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("trade", trade);
//        bundle.putString("offererUsersId", trade.getOffererUsersId());
//        bundle.putString("offererPokemonId", Integer.toString(trade.getOfferPokemonId()));
//        bundle.putString("offerLevel", Integer.toString(trade.getOfferLevel()));
//        bundle.putString("listerUsersId", trade.getListerUsersId());
//        bundle.putString("listerPokemonId", Integer.toString(trade.getListerPokemonId()));
//        bundle.putString("listerLevel", Integer.toString(trade.getListerLevel()));
        dialog.setArguments(bundle);
        dialog.show(getActivity().getFragmentManager(), "ViewTradeDialog");
    }

    public void showViewDeclineDialog(String lister) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ViewDeclineDialog();
        Bundle bundle = new Bundle();
        bundle.putString("listerUsersId", lister);
        dialog.setArguments(bundle);
        dialog.show(getActivity().getFragmentManager(), "ViewDeclineDialog");
    }

    public void showViewAcceptanceDialog(String lister) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ViewAcceptanceDialog();
        Bundle bundle = new Bundle();
        bundle.putString("listerUsersId", lister);
        dialog.setArguments(bundle);
        dialog.show(getActivity().getFragmentManager(), "ViewAcceptanceDialog");
    }

    private class PopulateTradingUsers extends AsyncTask<String, Void, List<TradingUser>> {

        @Override
        protected List<TradingUser> doInBackground(String... params) {
            List<TradingUser> temp = new ArrayList<TradingUser>();
            String url = Constant.SERVER_URL + "/getavailablefortrading/";

            HttpResponse response = MyHttpClient.get(url);
            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                String json = MyHttpClient.getJson(response);

                List<User> users = MyGson.getInstance().fromJson(json, new TypeToken<List<User>>() {}.getType());

                if (!users.isEmpty()) {
                    for (User ele : users) {
                        if  (!(ele.getUsername().equals(CurrentUser.getUsername()))) {
                            url = Constant.SERVER_URL + "/userspokemon/users_id=" + ele.getUsersId();
                            response = MyHttpClient.get(url);
                            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                                json = MyHttpClient.getJson(response);
                                List<UsersPokemon> usersPokemon = MyGson.getInstance().fromJson(json, new TypeToken<List<UsersPokemon>>() {
                                }.getType());
                                TradingUser tradingUser = new TradingUser(ele.getUsersId(), ele.getUsername(), ele.getHometown(), ele.getAvatar(), usersPokemon);
                                temp.add(tradingUser);
                            }
                        }

                    }
                    return temp;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<TradingUser> result) {
            if (result != null) {
                tradingUsersList = result;
                Log.e("ANURAG", tradingUsersList.toString());
                adapter = new TradingListAdapter(tradingUsersList);
                listView.setAdapter(adapter);
            }
        }
    }

    private class ViewHolder {
        public ImageButton tradingUserImageButton;
        public TextView tradingUsername;
        public TextView tradingHometown;
        public CheckBox tradingCheckBox;
    }

    private class TradingListAdapter extends ArrayAdapter<TradingUser> {
        public TradingListAdapter(List<TradingUser> list) {
            super(getActivity().getApplicationContext(), R.layout.trading_user_item_layout, R.id.tradingUsername, list);
        }

        private ArrayList<Boolean> mChecked;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            mChecked = new ArrayList<Boolean>();
            for (int i = 0; i < this.getCount(); i++) {
                mChecked.add(i, false);
            }

            if (v != convertView && v != null) {
                final ViewHolder holder = new ViewHolder();

                holder.tradingUserImageButton = (ImageButton) v.findViewById(R.id.tradingUserImageButton);
                holder.tradingUsername = (TextView) v.findViewById(R.id.tradingUsername);
                holder.tradingHometown = (TextView) v.findViewById(R.id.tradingHometown);
                holder.tradingCheckBox = (CheckBox) v.findViewById(R.id.trading);
                v.setTag(holder);
            }

            final ViewHolder holder = (ViewHolder) v.getTag();
            final TradingUser tradingUser = getItem(position);

            holder.tradingUserImageButton.setImageResource(ImageUtil.getImageResource(getActivity(), tradingUser.getAvatar()));
            holder.tradingUserImageButton.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), TradingView.class);
//                    i.putExtra("tradingUser", tradingUser);
                    i.putParcelableArrayListExtra("listingPokemon", (ArrayList<UsersPokemon>)tradingUser.getUsersPokemon());
                    i.putExtra("tradingUsersId", tradingUser.getUsersId());
                    startActivity(i);
                }
            });

            holder.tradingUsername.setText(tradingUser.getUsername());
            holder.tradingHometown.setText(tradingUser.getHometown());
//            Log.e("tradinglist users offerers", CurrentUser.getUsersTradingIn().toString());
//            Log.e("tradinglist users my offers", CurrentUser.getUsersOfferingOut().toString());

            if (usersTradingWith.contains(tradingUser.getUsername())) {
                mChecked.set(position, true);
            } else {
                mChecked.set(position, false);
            }
//            holder.tradingCheckBox.setOnClickListener(new CompoundButton.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (holder.tradingCheckBox.isChecked()) {
//                        new TradePlayer(CurrentUser.getUsername(), tradingUser.getUsername()).execute();
//                    } else {
//                        new UntradePlayer(CurrentUser.getUsername(), tradingUser.getUsername()).execute();
//                    }
//                }
//            });
            holder.tradingCheckBox.setChecked(mChecked.get(position));
            return v;
        }
    }

}
