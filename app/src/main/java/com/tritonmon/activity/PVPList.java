package com.tritonmon.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.tritonmon.asynctask.ChallengePlayer;
import com.tritonmon.asynctask.GetChallenges;
import com.tritonmon.asynctask.ToggleAvailableForBattleTask;
import com.tritonmon.asynctask.UnchallengePlayer;
import com.tritonmon.fragment.ChallengeDialog;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ImageUtil;
import com.tritonmon.global.MyGson;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.PVPUser;
import com.tritonmon.model.PokemonParty;
import com.tritonmon.model.User;
import com.tritonmon.model.UsersPokemon;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

public class PVPList extends FragmentActivity implements ChallengeDialog.NoticeDialogListener {
    private ListView listView;
    private ArrayAdapter<PVPUser> adapter;
    private List<PVPUser> pvpUsersList;

    private int perUnseenChallenge;

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
        new PopulatePVPUsers().execute();
        Log.e("pvplist", Integer.toString(CurrentUser.getUsersChallengers().size()));
        while (perUnseenChallenge <= CurrentUser.getUnseenUsersChallengers().size()-1) {
            showChallengeDialog(CurrentUser.getUnseenUsersChallengers().get(perUnseenChallenge));
            perUnseenChallenge++;
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                Log.e("PVPList", "caught interrupted exception");
//                e.printStackTrace();
//            }
        }

    }

    public String getUnseenChallenger() {
        perUnseenChallenge++;
        return CurrentUser.getUnseenUsersChallengers().get(perUnseenChallenge);
    }

    public void showChallengeDialog(String challenger) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ChallengeDialog();
        Bundle bundle = new Bundle();
        bundle.putString("challenger", challenger);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "ChallengeDialog");
    }

//    @Override
//    public Dialog onCreateDialog(DialogFragment dialog, Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
////        PVPList pvpList = (PVPList) getActivity();
//        builder.setMessage(getUnseenChallenger());
//        return builder.create();
//    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Log.e("pvplist", "challenge ACCEPTED");
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        Log.e("pvplist", "challenge declined");
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
                        if (ele.getUsername() != CurrentUser.getUsername()) {
                            url = Constant.SERVER_URL + "/getbestpokemoninfo/" + ele.getUsername();
                            response = MyHttpClient.get(url);
                            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                                json = MyHttpClient.getJson(response);
                                List<UsersPokemon> usersPokemon = MyGson.getInstance().fromJson(json, new TypeToken<List<UsersPokemon>>() {
                                }.getType());

                                PVPUser pvpUser = new PVPUser(ele, calculateMaxLevel(usersPokemon), calculateAverageLevel(usersPokemon));
                                temp.add(pvpUser);
                            }
                        }

                    }
                    return temp;
                }
            }

            return null;
        }

        private int calculateAverageLevel(List<UsersPokemon> usersPokemon) {
            int sum = 0;
            for (UsersPokemon ele : usersPokemon) {
                sum += ele.getLevel();
            }
            return (int)((float)sum/(float)usersPokemon.size());
        }

        private int calculateMaxLevel(List<UsersPokemon> usersPokemon) {
            int maxLevel = 0;
            for (UsersPokemon ele : usersPokemon) {
                if (ele.getLevel() > maxLevel) {
                    maxLevel = ele.getLevel();
                }
            }
            return maxLevel;
        }

        @Override
        protected void onPostExecute(List<PVPUser> result) {
            if (result != null) {
                pvpUsersList = result;
                adapter = new PVPListAdapter(pvpUsersList);
                listView.setAdapter(adapter);
            }
        }
    }

    private class ViewHolder {
        public ImageView pvpUserImage;
        public TextView pvpUsername;
        public TextView pvpHometown;
        public TextView pvpWinsLosses;
        public TextView pvpMaxPokemonLevel;
        public TextView pvpAveragePokemonLevel;
        public CheckBox pvpCheckBox;
    }

    private class PVPListAdapter extends ArrayAdapter<PVPUser> {
        public PVPListAdapter(List<PVPUser> list) {
            super(PVPList.this, R.layout.pvp_user_item_layout, R.id.pvpUsername, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            if (v != convertView && v != null) {
                ViewHolder holder = new ViewHolder();

                holder.pvpUserImage = (ImageView) v.findViewById(R.id.pvpUserImage);
                holder.pvpUsername = (TextView) v.findViewById(R.id.pvpUsername);
                holder.pvpHometown = (TextView) v.findViewById(R.id.pvpHometown);
                holder.pvpWinsLosses = (TextView) v.findViewById(R.id.pvpWinsLosses);
                holder.pvpMaxPokemonLevel = (TextView) v.findViewById(R.id.pvpMaxPokemonLevel);
                holder.pvpAveragePokemonLevel = (TextView) v.findViewById(R.id.pvpAveragePokemonLevel);
                holder.pvpCheckBox = (CheckBox) v.findViewById(R.id.pvpCheckBox);
                v.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) v.getTag();
            final PVPUser pvpUser = getItem(position);

            holder.pvpUserImage.setImageResource(ImageUtil.getImageResource(getApplicationContext(), pvpUser.getAvatar()));
            holder.pvpUsername.setText(pvpUser.getUsername());
            holder.pvpHometown.setText(pvpUser.getHometown());
            holder.pvpWinsLosses.setText("Wins/losses: " + Integer.toString(pvpUser.getWins()) + "/" + Integer.toString(pvpUser.getLosses()));
            holder.pvpMaxPokemonLevel.setText("Max level: " + Integer.toString(pvpUser.getMaxLevelPokemon()));
            holder.pvpAveragePokemonLevel.setText("Average level: " + Integer.toString(pvpUser.getAverageLevelOfTopSixPokemon()));
//            Log.e("pvplist users challengers", CurrentUser.getUsersChallengers().toString());
//            Log.e("pvplist users challenging", CurrentUser.getUsersChallenging().toString());
            if (CurrentUser.getUsersChallenging().contains(pvpUser.getUsername())) {
                holder.pvpCheckBox.setChecked(true);
            }
            holder.pvpCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        new ChallengePlayer(CurrentUser.getUsername(), pvpUser.getUsername()).execute();
                    } else {
                        new UnchallengePlayer(CurrentUser.getUsername(), pvpUser.getUsername()).execute();
                    }

                }

            });

            return v;
        }
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
