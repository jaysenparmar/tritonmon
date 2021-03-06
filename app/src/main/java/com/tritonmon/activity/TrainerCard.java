package com.tritonmon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tritonmon.asynctask.trades.GetTrades;
import com.tritonmon.asynctask.trades.ToggleAvailableForTradeTask;
import com.tritonmon.asynctask.user.GetAllUsers;
import com.tritonmon.global.Audio;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.singleton.MyApplication;
import com.tritonmon.global.util.ImageUtil;
import com.tritonmon.model.PokemonParty;
import com.tritonmon.toast.TritonmonToast;

import java.util.ArrayList;
import java.util.List;

public class TrainerCard extends ActionBarActivity {

    private TextView trainerName;
    private ImageView trainerImage;
    private ImageView collegeImage;
    private List<ImageView> pokemonImages;

    private ImageView tradeButton;
    private Switch availableForTrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Constant.DISABLE_ACTION_BAR) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        if (CurrentUser.isLoggedIn()) {
            new GetAllUsers().execute();
            new GetTrades().execute();

            trainerName = (TextView) findViewById(R.id.trainerName);
            trainerImage = (ImageView) findViewById(R.id.trainerImage);
            collegeImage = (ImageView) findViewById(R.id.collegeImage);

            pokemonImages = new ArrayList<ImageView>();
            pokemonImages.add((ImageView) findViewById(R.id.pokemon1Image));
            pokemonImages.add((ImageView) findViewById(R.id.pokemon2Image));
            pokemonImages.add((ImageView) findViewById(R.id.pokemon3Image));
            pokemonImages.add((ImageView) findViewById(R.id.pokemon4Image));
            pokemonImages.add((ImageView) findViewById(R.id.pokemon5Image));
            pokemonImages.add((ImageView) findViewById(R.id.pokemon6Image));

            trainerName.setText(Html.fromHtml(CurrentUser.getName()));
            trainerImage.setImageResource(ImageUtil.getImageResource(this, CurrentUser.getUser().getAvatar()));

            if (CurrentUser.getUser().getHometown() == null) {
                collegeImage.setVisibility(View.INVISIBLE);
            }
            else {
                int collegeImageResId = ImageUtil.getImageResource(getApplicationContext(), CurrentUser.getUser().getHometown().toLowerCase());
                collegeImage.setImageResource(collegeImageResId);
            }

            for (int i = 0; i < PokemonParty.MAX_PARTY_SIZE; i++) {
                if (CurrentUser.getPokemonParty().getPokemon(i) != null) {
                    int pokemonId = CurrentUser.getPokemonParty().getPokemon(i).getPokemonId();
                    pokemonImages.get(i).setImageResource(ImageUtil.getPokemonFrontImageResource(this, pokemonId));
                }
                else {
                    pokemonImages.get(i).setVisibility(View.INVISIBLE);
                }
            }

            availableForTrade = (Switch) findViewById(R.id.availableForBattle);
            availableForTrade.setChecked(CurrentUser.getUser().isAvailableForTrading());
            availableForTrade.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updateTradeButton(isChecked);
                    new ToggleAvailableForTradeTask(isChecked, CurrentUser.getUsersId()).execute();
                }
            });

            tradeButton = (ImageView) findViewById(R.id.tradeButton);
            updateTradeButton(CurrentUser.getUser().isAvailableForTrading());
            tradeButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (Audio.isAudioEnabled()) {
                        Audio.sfx.start();
                    }
                    tradeButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "trade_sel"));
                    Intent i = new Intent(getApplicationContext(), TradingListHandler.class);
                    startActivity(i);
                }
            });
        }
        else {
            TritonmonToast.makeText(getApplicationContext(), "Trying to open Trainer Card when not logged in!", Toast.LENGTH_LONG).show();
        }

        Tracker t = ((MyApplication) getApplication()).getTracker(
                MyApplication.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_trainer_card;
    }

    @Override
    protected int getMenuResourceId() {
        return R.menu.logged_in_menu;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(i);
    }

    private void updateTradeButton(boolean isTradeOn) {
        if (isTradeOn) {
            tradeButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "trade_en"));
            tradeButton.setEnabled(true);
        }
        else {
            tradeButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "trade_dis"));
            tradeButton.setEnabled(false);
        }
    }
}
