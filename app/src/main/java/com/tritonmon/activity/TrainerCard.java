package com.tritonmon.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.tritonmon.asynctask.CaughtPokemonTask;
import com.tritonmon.asynctask.ToggleAvailableForBattleTask;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ImageUtil;
import com.tritonmon.model.PokemonParty;

import java.util.ArrayList;
import java.util.List;

public class TrainerCard extends Activity {

    private TextView trainerName;
    private ImageView trainerImage;
    private List<ImageView> pokemonImages;

    private Switch availableForBattle;

    private Button pvpList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_card);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        trainerName = (TextView) findViewById(R.id.trainerName);
        trainerImage = (ImageView) findViewById(R.id.trainerImage);

        pokemonImages = new ArrayList<ImageView>();
        pokemonImages.add((ImageView) findViewById(R.id.pokemon1Image));
        pokemonImages.add((ImageView) findViewById(R.id.pokemon2Image));
        pokemonImages.add((ImageView) findViewById(R.id.pokemon3Image));
        pokemonImages.add((ImageView) findViewById(R.id.pokemon4Image));
        pokemonImages.add((ImageView) findViewById(R.id.pokemon5Image));
        pokemonImages.add((ImageView) findViewById(R.id.pokemon6Image));

        if (CurrentUser.isLoggedIn()) {
            trainerName.setText(Html.fromHtml("<font color=#ff0000>" + CurrentUser.getUsername() + "</font>"));
            trainerImage.setImageResource(ImageUtil.getImageResource(this, CurrentUser.getUser().getAvatar()));

            for (int i=0; i<PokemonParty.MAX_PARTY_SIZE; i++) {
                int pokemonId = 0;
                if (CurrentUser.getPokemonParty().getPokemon(i) != null) {
                    pokemonId = CurrentUser.getPokemonParty().getPokemon(i).getPokemonId();
                }
                pokemonImages.get(i).setImageResource(ImageUtil.getPokemonFrontImageResource(this, pokemonId));
            }
        }

        availableForBattle = (Switch) findViewById(R.id.availableForBattle);
        availableForBattle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                new ToggleAvailableForBattleTask(isChecked, CurrentUser.getUsername()).execute();
            }
        });

        pvpList = (Button) findViewById(R.id.pvpButton);
        pvpList.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PVPList.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trainer_card, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_trainer_card, container, false);
            return rootView;
        }
    }
}
