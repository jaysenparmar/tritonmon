package com.tritonmon.activity;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tritonmon.asynctask.user.UpdateCurrentUserTask;
import com.tritonmon.global.Audio;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.StaticData;
import com.tritonmon.global.singleton.MyApplication;
import com.tritonmon.global.util.ImageUtil;
import com.tritonmon.model.UsersPokemon;
import com.tritonmon.toast.TritonmonToast;

import java.text.ParseException;
import java.util.Map;

public class MainMenu extends ActionBarActivity {

    private TextView pokeText;
    private ImageView pokeImage;

    private ImageView trainerCardButton;
    private ImageView viewMapButton;
    private ImageView pokemonCenterButton;

    private ImageView battleButton;
    private ImageView partyButton;

    private boolean backButtonPressed;
    private Handler handler;

    private static LocationManager locationManager;
    private static LocationListener locationListener;

    private static final long MIN_TIME = 1000; // Minimum time between location updates in ms
    private static final float MIN_DISTANCE = 0; // Minimum distance between location updates in meters
    private static final double[] ucsdBounds = {32.8702698, 32.8914615,-117.2433421, -117.2208545}; // {xmin,xmax,ymin,ymax}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Constant.DISABLE_ACTION_BAR) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        pokeText = (TextView) findViewById(R.id.pokeText);
        updatePokeText();
        pokeImage = (ImageView) findViewById(R.id.pokeImage);
        pokeImage.setImageResource(ImageUtil.getImageResource(getApplicationContext(), CurrentUser.getUser().getAvatar()));

        trainerCardButton = (ImageView) findViewById(R.id.trainerCardButton);
        viewMapButton = (ImageView) findViewById(R.id.viewMapButton);
        pokemonCenterButton = (ImageView) findViewById(R.id.pokeCenterButton);

        pokeImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (Audio.isAudioEnabled()) {
                    Audio.sfx.start();
                }
                Intent i = new Intent(getApplicationContext(), AvatarSelection.class);
                startActivity(i);
            }
        });

        trainerCardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                trainerCardButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "trainercard_en"));
                if (Audio.isAudioEnabled()) {
                    Audio.sfx.start();
                }
                Intent i = new Intent(getApplicationContext(), TrainerCard.class);
                startActivity(i);
            }
        });

        viewMapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                viewMapButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "viewmap_en"));
                if (Audio.isAudioEnabled()) {
                    Audio.sfx.start();
                }
                setProgressBarIndeterminateVisibility(true);
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
            }
        });

        pokemonCenterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                pokemonCenterButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "pokecenter_sel"));
                if (Audio.isAudioEnabled()) {
                    Audio.sfx.start();
                }
                Intent i = new Intent(getApplicationContext(), PokeCenter.class);
                startActivity(i);
            }
        });

        battleButton = (ImageView) findViewById(R.id.battleButton);
        battleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                battleButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "battle_en"));
                if (Audio.isAudioEnabled()) {
                    Audio.sfx.start();
                }
                Intent i = new Intent(getApplicationContext(), Battle.class);
                i.putExtra("selectedPokemonIndex", chooseNextPokemon());
                startActivity(i);
            }
        });

        partyButton = (ImageView) findViewById(R.id.partyButton);
        partyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                partyButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "viewparty_en"));
                if (Audio.isAudioEnabled()) {
                    Audio.sfx.start();
                }
                Intent i = new Intent(getApplicationContext(), Party.class);
                startActivity(i);
            }
        });

        resetButtons();

        backButtonPressed = false;
        handler = new Handler();
        handler.post(battleButtonRunnable);

        // make sure static data is loaded
        try {
            if (Constant.pokemonData == null) {
                StaticData.load(getAssets());
            }
        }
        catch (ParseException e) {
            TritonmonToast.makeText(getApplicationContext(), "ERROR: Failed to load static data", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        // Map stuff

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                // TritonmonToast.makeText(getApplicationContext(), String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude()), Toast.LENGTH_LONG).show();
                Log.d("MainMenu", "onLocationChanged listener invoked");
                if (CurrentUser.isLoggedIn()) {
                    setLocation(location);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);

        new UpdateCurrentUserTask(this).execute();

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

    private void resetButtons() {
        trainerCardButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "trainercard_dis"));
        viewMapButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "viewmap_dis"));
        pokemonCenterButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "pokecenter_dis"));
        battleButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "battle_dis"));
        partyButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "viewparty_dis"));

        if (chooseNextPokemon() == null) {
            battleButton.setEnabled(false);
            battleButton.setColorFilter(Constant.DISABLE_COLOR);
        }
        else {
            battleButton.setEnabled(true);
            battleButton.clearColorFilter();
        }

        if (!CurrentUser.getCurrentCity().equals("Price Center")) {
            pokemonCenterButton.setEnabled(false);
            pokemonCenterButton.setColorFilter(Constant.DISABLE_COLOR);
        }
        else {
            pokemonCenterButton.setEnabled(true);
            pokemonCenterButton.clearColorFilter();
        }
    }

    private Runnable battleButtonRunnable = new Runnable() {
        @Override
        public void run() {
            if (chooseNextPokemon() == null) {
                battleButton.setEnabled(false);
                battleButton.setColorFilter(Constant.DISABLE_COLOR);
            }
            else {
                battleButton.setEnabled(true);
                battleButton.clearColorFilter();
            }

            handler.postDelayed(this, 1000);
        }
    };

    private void updatePokeText() {
        int typeId;
        String typeName;
        String location = CurrentUser.getCurrentCity();

        if (location.equals("UCSD") || location.isEmpty()) {
            typeName = "all";

            if (CurrentUser.getCurrentCity().isEmpty()) {
                location = "World";
            }
        }
        else {
            typeId = Constant.locationDataMap.get(CurrentUser.getCurrentCity());
            typeName = Constant.typesData.get(typeId).getName();
        }

        String pokeTextString = "Trainer<br />" + Constant.redText(CurrentUser.getName())
                + "<br /><br />" + "Location<br />" + Constant.redText(location)
                + "<br /><br />" + "Type<br />" + Constant.redText(typeName);
        pokeText.setText(Html.fromHtml(pokeTextString));
    }

    @Override
    protected void onResume() {
        super.onResume();
        new UpdateCurrentUserTask(this).execute();
        resetButtons();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main_menu;
    }

    @Override
    protected int getMenuResourceId() {
        return R.menu.logged_in_menu;
    }

    @Override
    public void onBackPressed() {
        if (!backButtonPressed) {
            backButtonPressed = true;
            TritonmonToast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            handler.postDelayed(backButtonRunnable, 2000);
        }
        else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /**
     * Determine the current location. If the location is new, update with a Toast message
     */
    private void setLocation(Location location) {
        CurrentUser.currentLocation = location;
        boolean inZone = false;

        for (Map.Entry<String, double[]> entry : MapsActivity.locations.entrySet()) {
            String key = entry.getKey();
            double[] value = entry.getValue();
            if (location.getLatitude() > value[0] && location.getLatitude() < value[1]
                    && location.getLongitude() > value[2] && location.getLongitude() < value[3]) {
                inZone = true;
                if (!CurrentUser.currentCity.equals(key)) {
                    CurrentUser.currentCity = key;
                    TritonmonToast.makeText(getApplicationContext(), "Now entering " + CurrentUser.currentCity, Toast.LENGTH_LONG).show();
                    Log.d("MainMenu", "location changed, city:" + key);
                    break;
                }
            }
        }
        if (!inZone) {
            if (location.getLatitude() > ucsdBounds[0] && location.getLatitude() < ucsdBounds[1]
                    && location.getLongitude() > ucsdBounds[2] && location.getLongitude() < ucsdBounds[3]) {
                CurrentUser.currentCity = "UCSD";
            } else {
                CurrentUser.currentCity = "";
            }
        }

        updatePokeText();
        resetButtons();
    }

    private Runnable backButtonRunnable = new Runnable() {
        public void run() {
            backButtonPressed = false;
        }
    };

    private Integer chooseNextPokemon() {
        for (int i = 0; i < CurrentUser.getPokemonParty().size(); i++) {
            UsersPokemon pokemon = CurrentUser.getPokemonParty().getPokemon(i);
            if (pokemon.getHealth() > 0) {
                return i;
            }
        }

        return null;
    }
}
