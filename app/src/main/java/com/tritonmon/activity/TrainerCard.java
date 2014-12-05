package com.tritonmon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tritonmon.asynctask.trades.GetTrades;
import com.tritonmon.asynctask.trades.ToggleAvailableForTradeTask;
import com.tritonmon.asynctask.user.UpdateCurrentUserTask;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ImageUtil;
import com.tritonmon.model.PokemonParty;
import com.tritonmon.toast.TritonmonToast;

import java.util.ArrayList;
import java.util.List;

public class TrainerCard extends Activity {

    private TextView trainerName;
    private ImageView trainerImage;
    private List<ImageView> pokemonImages;

    private Switch availableForBattle;

    private Button tradingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_card);

        if (CurrentUser.isLoggedIn()) {
            new GetTrades().execute();

            trainerName = (TextView) findViewById(R.id.trainerName);
            trainerImage = (ImageView) findViewById(R.id.trainerImage);

            pokemonImages = new ArrayList<ImageView>();
            pokemonImages.add((ImageView) findViewById(R.id.pokemon1Image));
            pokemonImages.add((ImageView) findViewById(R.id.pokemon2Image));
            pokemonImages.add((ImageView) findViewById(R.id.pokemon3Image));
            pokemonImages.add((ImageView) findViewById(R.id.pokemon4Image));
            pokemonImages.add((ImageView) findViewById(R.id.pokemon5Image));
            pokemonImages.add((ImageView) findViewById(R.id.pokemon6Image));

            trainerName.setText(Html.fromHtml("<font color=#ff0000>" + CurrentUser.getName() + "</font>"));
            trainerImage.setImageResource(ImageUtil.getImageResource(this, CurrentUser.getUser().getAvatar()));

            for (int i = 0; i < PokemonParty.MAX_PARTY_SIZE; i++) {
                int pokemonId = 0;
                if (CurrentUser.getPokemonParty().getPokemon(i) != null) {
                    pokemonId = CurrentUser.getPokemonParty().getPokemon(i).getPokemonId();
                }
                pokemonImages.get(i).setImageResource(ImageUtil.getPokemonFrontImageResource(this, pokemonId));
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
                    Intent i = new Intent(getApplicationContext(), TradingListHandler.class);
                    startActivity(i);
                }
            });
        }
        else {
            TritonmonToast.makeText(getApplicationContext(), "hihi", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
            Intent i = new Intent(getApplicationContext(), Settings.class);
            startActivity(i);
            return true;
        }
        else if(id == R.id.logout) {
            CurrentUser.logout();
            Intent i = new Intent(getApplicationContext(), Tritonmon.class);
            startActivity(i);
            return true;
        }
        else if(id == R.id.refresh) {
            new UpdateCurrentUserTask().execute();
        }
        return super.onOptionsItemSelected(item);
    }
}
