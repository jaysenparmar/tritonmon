package com.tritonmon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tritonmon.asynctask.trades.GetTrades;
import com.tritonmon.asynctask.trades.ToggleAvailableForTradeTask;
import com.tritonmon.asynctask.user.GetAllUsers;
import com.tritonmon.asynctask.user.UpdateCurrentUserTask;
import com.tritonmon.global.Audio;
import com.tritonmon.global.CurrentUser;
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

    private Switch availableForBattle;

    private Button tradingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

            trainerName.setText(Html.fromHtml("<font color=#ff0000>" + CurrentUser.getName() + "</font>"));
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

            availableForBattle = (Switch) findViewById(R.id.availableForBattle);
            if (CurrentUser.getUser().isAvailableForTrading()) {
                availableForBattle.setChecked(true);
            } else {
                availableForBattle.setChecked(false);
            }
            availableForBattle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    new ToggleAvailableForTradeTask(isChecked, CurrentUser.getUsersId()).execute();
                }
            });

            tradingList = (Button) findViewById(R.id.tradeButton);
            tradingList.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (availableForBattle.isChecked()) {
                        Audio.sfx.start();
                        Intent i = new Intent(getApplicationContext(), TradingListHandler.class);
                        startActivity(i);
                    }
                }
            });
        }
        else {
            TritonmonToast.makeText(getApplicationContext(), "hihi", Toast.LENGTH_LONG).show();
        }
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

}
