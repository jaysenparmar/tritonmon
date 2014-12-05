package com.tritonmon.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.location.Geofence;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.widget.Toast;
import android.content.Context;

import java.util.Map;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private final LatLng UCSD = new LatLng(32.88006, -117.234013);
    private final int zoomLevel = 14;
    private final long minTime = 5000; // Minimum time between location updates in ms
    private final float minDistance = 20; // Minimum distance between location updates in meters
    private String currentCity = "";
    private Location currentLocation;
    private final Context myContext = this;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private static final Map<String, double[]> locations;
    private static final double[] erc_coordinates = {32.88383620000000, 32.88715170000000, -117.24372250000000, -117.24077200000000};
    private static final double[] geisel_coordinates = {32.88082500000000, 32.88130900000000, -117.23772510000000,  -117.23737960000000};
    private static final double[] price_center_coordinates = {32.87916090000000, 32.88012500000000, -117.23744060000000, -117.23561670000000};
    static
    {
        locations = new HashMap<String, double[]>();
        locations.put("ERC", erc_coordinates);
        locations.put("Geisel", geisel_coordinates);
        locations.put("Price Center", price_center_coordinates);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Toast.makeText(myContext, String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude()), Toast.LENGTH_LONG).show();
                setLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onDestroy() {
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UCSD, zoomLevel));
                mMap.setMyLocationEnabled(true);
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        for (Map.Entry<String, double[]> entry : locations.entrySet()) {
            String key = entry.getKey();
            double[] value = entry.getValue();
            Log.d("key", key);
            Log.d("xmin", String.valueOf(value[0]));
            Log.d("ymax", String.valueOf(value[3]));
            if (key.equals("Price Center")) {
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_pokecenter))
                        .position(new LatLng(value[0], value[3])).title(key));
            }
            else if (key.equals("ERC")) {
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_azalea_gym))
                        .position(new LatLng(value[0], value[3])).title(key));
            }
            else if (key.equals("Geisel")) {
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_violet_gym))
                        .position(new LatLng(value[0], value[3])).title(key));
            }
        }
    }

    /**
     * Determine the current location
     */
    private void setLocation(Location location) {
        currentLocation = location;
        if (location.getLatitude() > geisel_coordinates[0] && location.getLatitude() < geisel_coordinates[1]
                && location.getLongitude() > geisel_coordinates[2] && location.getLongitude() < geisel_coordinates[3]) {
            if (!currentCity.equals("Geisel")) {
                Toast.makeText(myContext, "The current city is: " + currentCity, Toast.LENGTH_LONG).show();
                currentCity = "Geisel";
            }
        }
        else if (location.getLatitude() > erc_coordinates[0] && location.getLatitude() < erc_coordinates[1]
                && location.getLongitude() > erc_coordinates[2] && location.getLongitude() < erc_coordinates[3]) {
            if (!currentCity.equals("ERC")) {
                Toast.makeText(myContext, "The current city is: " + currentCity, Toast.LENGTH_LONG).show();
                currentCity = "ERC";
            }
        }
        else if (location.getLatitude() > price_center_coordinates[0] && location.getLatitude() < price_center_coordinates[1]
                && location.getLongitude() > price_center_coordinates[2] && location.getLongitude() < price_center_coordinates[3]) {
            if (!currentCity.equals("Price Center")) {
                Toast.makeText(myContext, "The current city is: " + currentCity, Toast.LENGTH_LONG).show();
                currentCity = "Price Center";
            }
        }
    }
}
