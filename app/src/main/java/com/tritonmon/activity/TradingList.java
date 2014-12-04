package com.tritonmon.activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.tritonmon.asynctask.trades.GetTrades;
import com.tritonmon.asynctask.trades.SetViewedDecisions;
import com.tritonmon.asynctask.trades.SetViewedTrade;
import com.tritonmon.fragment.ViewAcceptanceDialog;
import com.tritonmon.fragment.ViewTradeDialog;
import com.tritonmon.fragment.ViewDeclineDialog;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ImageUtil;
import com.tritonmon.global.MyGson;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.Trade;
import com.tritonmon.model.TradingUser;
import com.tritonmon.model.User;
import com.tritonmon.model.UsersPokemon;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TradingList extends FragmentActivity implements ViewAcceptanceDialog.NoticeDialogListener, ViewTradeDialog.NoticeDialogListener, ViewDeclineDialog.NoticeDialogListener{
    private ListView listView;
    private ArrayAdapter<TradingUser> adapter;
    private List<TradingUser> TradingUsersList;

    private int perUnseenTrade;
    private int perUnseenDeclinedTrade;
    private int perUnseenAcceptanceTrade;
    private int perDialogBox;
    private Set<String> usersTradingWith = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_list);

        listView = (ListView) findViewById(R.id.tradingUserListView);
        TradingUsersList = new ArrayList<TradingUser>();
//        TradingUsersList.add(new TradingUser(CurrentUser.getUser(), 5, 10));

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TradingUser TradingUser = (TradingUser) listView.getItemAtPosition(position);
                adapter.notifyDataSetChanged();

                Toast.makeText(
                        getApplicationContext(),
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
//        adapter = new tradinglistAdapter(TradingUsersList);
//        listView.setAdapter(adapter);

        for (Trade trade : CurrentUser.getTrades()) {
            if (trade.getLister().equals(CurrentUser.getUsername()) && !trade.isSeenOffer()) {
                showViewTradeDialog(trade);
                perUnseenTrade++;
            }
            usersTradingWith.add(trade.getOfferer());
            usersTradingWith.add(trade.getLister());
        }
        for (Trade trade : CurrentUser.getTrades()) {
            if (trade.getOfferer().equals(CurrentUser.getUsername()) && trade.isDeclined() && !trade.isSeenDecline()) {
                showViewDeclineDialog(trade.getLister());
                perUnseenDeclinedTrade++;
            }
        }
        for (Trade trade : CurrentUser.getTrades()) {
            if (trade.getOfferer().equals(CurrentUser.getUsername()) && trade.isAccepted() && !trade.isSeenAcceptance()) {
                Log.d("trading list", "in the if");
                showViewAcceptanceDialog(trade.getLister());
                perUnseenAcceptanceTrade++;
            }
        }

//        saving a server call!
        if (CurrentUser.getTrades() != null && !CurrentUser.getTrades().isEmpty()) {
            new SetViewedDecisions(CurrentUser.getUsername()).execute();
        }
    }

    public void showViewTradeDialog(Trade trade) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ViewTradeDialog();
        Bundle bundle = new Bundle();
        bundle.putString("offerer", trade.getOfferer());
        bundle.putString("offererPokemonId", Integer.toString(trade.getOfferPokemonId()));
        bundle.putString("offerLevel", Integer.toString(trade.getOfferLevel()));
        bundle.putString("lister", trade.getLister());
        bundle.putString("listerPokemonId", Integer.toString(trade.getListerPokemonId()));
        bundle.putString("listerLevel", Integer.toString(trade.getListerLevel()));
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "ViewTradeDialog");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onViewTradeDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Log.e("tradinglist", "trade ACCEPTED");
        new SetViewedTrade(CurrentUser.getTrades().get(perDialogBox), SetViewedTrade.Choices.ACCEPTED).execute();
        perDialogBox++;
    }

    @Override
    public void onViewTradeDialogNeutralClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Log.e("tradinglist", "trade POSTPONED");
        new SetViewedTrade(CurrentUser.getTrades().get(perDialogBox), SetViewedTrade.Choices.NEUTRAL).execute();
        perDialogBox++;
    }

    @Override
    public void onViewTradeDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        Log.e("tradinglist", "trade declined");
        new SetViewedTrade(CurrentUser.getTrades().get(perDialogBox), SetViewedTrade.Choices.DECLINED).execute();
        perDialogBox++;
    }

    public void showViewDeclineDialog(String lister) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ViewDeclineDialog();
        Bundle bundle = new Bundle();
        bundle.putString("lister", lister);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "ViewDeclineDialog");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onViewDeclineDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Log.e("tradinglist", "decline RECOGNIZED");
//        new SetViewedDecisions(CurrentUser.getUsername()).execute();
        perDialogBox++;
    }

    public void showViewAcceptanceDialog(String lister) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ViewAcceptanceDialog();
        Bundle bundle = new Bundle();
        bundle.putString("lister", lister);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "ViewAcceptanceDialog");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onViewAcceptanceDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Log.e("tradinglist", "acceptance RECOGNIZED");
//        new SetViewedDecisions(CurrentUser.getUsername()).execute();
        perDialogBox++;
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

//                Log.e("hihi", users.toString());
                if (!users.isEmpty()) {
                    for (User ele : users) {
                        if (!(ele.getUsername().equals(CurrentUser.getUsername()))) {
                            url = Constant.SERVER_URL + "/userspokemon/users_id=" + ele.getUsersId();
                            response = MyHttpClient.get(url);
                            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                                json = MyHttpClient.getJson(response);
                                List<UsersPokemon> usersPokemon = MyGson.getInstance().fromJson(json, new TypeToken<List<UsersPokemon>>() {
                                }.getType());

                                TradingUser tradingUser = new TradingUser(ele.getUsername(), ele.getHometown(), ele.getAvatar(), usersPokemon);
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
                TradingUsersList = result;
                adapter = new TradingListAdapter(TradingUsersList);
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
            super(TradingList.this, R.layout.trading_user_item_layout, R.id.tradingUsername, list);
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

            holder.tradingUserImageButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), tradingUser.getAvatar()));
            holder.tradingUserImageButton.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), TradingView.class);
//                    i.putExtra("tradingUser", tradingUser);
                    i.putParcelableArrayListExtra("listingPokemon", (ArrayList<UsersPokemon>)tradingUser.getUsersPokemon());
                    i.putExtra("tradingUsername", tradingUser.getUsername());
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

    @Override
    public void onBackPressed() {
        new GetTrades().execute();
        Intent i = new Intent(getApplicationContext(), TrainerCard.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trading_list, menu);
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
