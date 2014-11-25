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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.tritonmon.asynctask.ChallengePlayer;
import com.tritonmon.asynctask.GetChallenges;
import com.tritonmon.asynctask.SetViewedChallenge;
import com.tritonmon.asynctask.SetViewedDecline;
import com.tritonmon.asynctask.UnchallengePlayer;
import com.tritonmon.fragment.ViewChallengeDialog;
import com.tritonmon.fragment.ViewDeclineDialog;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ImageUtil;
import com.tritonmon.global.MyGson;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.PVPUser;
import com.tritonmon.model.User;
import com.tritonmon.model.UsersPokemon;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

public class TradingList extends FragmentActivity implements ViewChallengeDialog.NoticeDialogListener, ViewDeclineDialog.NoticeDialogListener{
    private ListView listView;
    private ArrayAdapter<PVPUser> adapter;
    private List<PVPUser> pvpUsersList;

    private int perUnseenChallenge;
    private int perUnseenDeclinedChallenge;
    private int perDialogBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pvp_list);

        listView = (ListView) findViewById(R.id.pvpUserListView);
        pvpUsersList = new ArrayList<PVPUser>();
//        pvpUsersList.add(new PVPUser(CurrentUser.getUser(), 5, 10));

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PVPUser pvpUser = (PVPUser) listView.getItemAtPosition(position);
                adapter.notifyDataSetChanged();

                Toast.makeText(
                        getApplicationContext(),
                        "selected " + pvpUser.getUsername(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        perUnseenChallenge = 0;
        perDialogBox = 0;
        perUnseenDeclinedChallenge = 0;
        new PopulatePVPUsers().execute();
//        adapter = new PVPListAdapter(pvpUsersList);
//        listView.setAdapter(adapter);
        Log.e("pvplist unseen challenges", Integer.toString(CurrentUser.getUsersChallengers().size()));
        while (perUnseenChallenge <= CurrentUser.getUnseenUsersChallengers().size()-1) {
            showViewChallengeDialog(CurrentUser.getUnseenUsersChallengers().get(perUnseenChallenge));
            perUnseenChallenge++;
        }
        Log.e("pvplist unseen declined challenges", Integer.toString(CurrentUser.getUnseenDeclinedUsersChallengers().size()));
        while (perUnseenDeclinedChallenge <= CurrentUser.getUnseenDeclinedUsersChallengers().size()-1) {
            showViewDeclineDialog(CurrentUser.getUnseenDeclinedUsersChallengers().get(perUnseenDeclinedChallenge));
            perUnseenDeclinedChallenge++;
        }
        new SetViewedDecline(CurrentUser.getUsername()).execute();
    }

    public void showViewChallengeDialog(String challenger) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ViewChallengeDialog();
        Bundle bundle = new Bundle();
        bundle.putString("challenger", challenger);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "ViewChallengeDialog");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onViewChallengeDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Log.e("pvplist", "challenge ACCEPTED");
        perDialogBox++;
    }

    @Override
    public void onViewChallengeDialogNeutralClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Log.e("pvplist", "challenge POSTPONED");
        new SetViewedChallenge(CurrentUser.getUnseenUsersChallengers().get(perDialogBox), CurrentUser.getUsername(), SetViewedChallenge.Choices.NEUTRAL).execute();
        perDialogBox++;
}

    @Override
    public void onViewChallengeDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        Log.e("pvplist", "challenge declined");
        new SetViewedChallenge(CurrentUser.getUnseenUsersChallengers().get(perDialogBox), CurrentUser.getUsername(), SetViewedChallenge.Choices.DECLINED).execute();
        perDialogBox++;
    }

    public void showViewDeclineDialog(String challenged) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ViewDeclineDialog();
        Bundle bundle = new Bundle();
        bundle.putString("challenged", challenged);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "ViewDeclineDialog");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onViewDeclineDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Log.e("pvplist", "decline RECOGNIZED");
        perDialogBox++;
    }

    private class PopulatePVPUsers extends AsyncTask<String, Void, List<PVPUser>> {

        @Override
        protected List<PVPUser> doInBackground(String... params) {
            List<PVPUser> temp = new ArrayList<PVPUser>();
            String url = Constant.SERVER_URL + "/getavailableforpvp/";

            HttpResponse response = MyHttpClient.get(url);
            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                String json = MyHttpClient.getJson(response);

                List<User> users = MyGson.getInstance().fromJson(json, new TypeToken<List<User>>() {}.getType());

//                Log.e("hihi", users.toString());
                if (!users.isEmpty()) {
                    for (User ele : users) {
                        if (!(ele.getUsername().equals(CurrentUser.getUsername()))) {
                            url = Constant.SERVER_URL + "/userspokemon/" + ele.getUsername();
                            response = MyHttpClient.get(url);
                            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                                json = MyHttpClient.getJson(response);
                                List<UsersPokemon> usersPokemon = MyGson.getInstance().fromJson(json, new TypeToken<List<UsersPokemon>>() {
                                }.getType());

                                PVPUser pvpUser = new PVPUser(ele.getUsername(), ele.getHometown(), ele.getAvatar(), usersPokemon);
                                temp.add(pvpUser);
                            }
                        }

                    }
                    return temp;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<PVPUser> result) {
            if (result != null) {
                pvpUsersList = result;
                adapter = new TradingListAdapter(pvpUsersList);
                listView.setAdapter(adapter);
            }
        }
    }

    private class ViewHolder {
        public ImageView tradingUserImage;
        public TextView tradingUsername;
        public TextView tradingHometown;
        public CheckBox tradingCheckBox;
    }

    private class TradingListAdapter extends ArrayAdapter<PVPUser> {
        public TradingListAdapter(List<PVPUser> list) {
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

                holder.tradingUserImage = (ImageView) v.findViewById(R.id.tradingUserImage);
                holder.tradingUsername = (TextView) v.findViewById(R.id.tradingUsername);
                holder.tradingHometown = (TextView) v.findViewById(R.id.tradingHometown);
                holder.tradingCheckBox = (CheckBox) v.findViewById(R.id.trading);
                v.setTag(holder);
            }

            final ViewHolder holder = (ViewHolder) v.getTag();
            final PVPUser pvpUser = getItem(position);

            holder.tradingUserImage.setImageResource(ImageUtil.getImageResource(getApplicationContext(), pvpUser.getAvatar()));
            holder.tradingUsername.setText(pvpUser.getUsername());
            holder.tradingHometown.setText(pvpUser.getHometown());
            Log.e("pvplist users challengers", CurrentUser.getUsersChallengers().toString());
            Log.e("pvplist users challenging", CurrentUser.getUsersChallenging().toString());
            if (CurrentUser.getUsersChallenging().contains(pvpUser.getUsername())) {
                mChecked.set(position, true);
            } else {
                mChecked.set(position, false);
            }
            holder.tradingCheckBox.setOnClickListener(new CompoundButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.tradingCheckBox.isChecked()) {
                        new ChallengePlayer(CurrentUser.getUsername(), pvpUser.getUsername()).execute();
                    } else {
                        new UnchallengePlayer(CurrentUser.getUsername(), pvpUser.getUsername()).execute();
                    }
                }
            });
            holder.tradingCheckBox.setChecked(mChecked.get(position));
            return v;
        }
    }

    @Override
    public void onBackPressed() {
        new GetChallenges().execute();
        Intent i = new Intent(getApplicationContext(), TrainerCard.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pvp_list, menu);
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
