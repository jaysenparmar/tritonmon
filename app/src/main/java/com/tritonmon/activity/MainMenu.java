package com.tritonmon.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tritonmon.asynctask.user.UpdateCurrentUserTask;
import com.tritonmon.global.Audio;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.StaticData;
import com.tritonmon.global.util.ImageUtil;
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
    private Handler backButtonHandler;

    // Location variables
    private final Context myContext = this;

    static LocationManager locationManager;
    static LocationListener locationListener;

    private final long MIN_TIME = 1000; // Minimum time between location updates in ms
    private final float MIN_DISTANCE = 20; // Minimum distance between location updates in meters
    private final double[] ucsdBounds = {32.8702698, 32.8914615,-117.2433421, -117.2208545}; // {xmin,xmax,ymin,ymax}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pokeText = (TextView) findViewById(R.id.pokeText);
        pokeText.setText(CurrentUser.getName() + "\n\n" + "Current Location");
        pokeImage = (ImageView) findViewById(R.id.pokeImage);
        pokeImage.setImageResource(ImageUtil.getImageResource(getApplicationContext(), CurrentUser.getUser().getAvatar()));

        trainerCardButton = (ImageView) findViewById(R.id.trainerCardButton);
        viewMapButton = (ImageView) findViewById(R.id.viewMapButton);
        pokemonCenterButton = (ImageView) findViewById(R.id.pokeCenterButton);

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

        backButtonPressed = false;
        backButtonHandler = new Handler();

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
                Toast.makeText(myContext, String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude()), Toast.LENGTH_LONG).show();
                Log.d("inside location listener", "");
                setLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);


        new UpdateCurrentUserTask(this).execute();
    }

    private void resetButtons() {
        trainerCardButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "trainercard_dis"));
        viewMapButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "viewmap_dis"));
        pokemonCenterButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "pokecenter_dis"));
        battleButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "battle_dis"));
        partyButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "viewparty_dis"));
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            backButtonHandler.postDelayed(backButtonRunnable, 2000);
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
            Log.d("inside for loop", "");
            if (location.getLatitude() > value[0] && location.getLatitude() < value[1]
                    && location.getLongitude() > value[2] && location.getLongitude() < value[3]) {
                Log.d("inside if loop","");
                if (!CurrentUser.currentCity.equals(key)) {
                    CurrentUser.currentCity = key;
                    Toast.makeText(myContext, "Now Entering: " + CurrentUser.currentCity, Toast.LENGTH_LONG).show();
                    Log.d("location changed, city:", key);
                    inZone = true;
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
    }

    private Runnable backButtonRunnable = new Runnable() {
        public void run() {
            backButtonPressed = false;
        }
    };

}
