package com.tritonmon.activity;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tritonmon.global.CurrentUser;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private final LatLng UCSD = new LatLng(32.88006, -117.234013);
    private final int zoomLevel = 14;
    private final long minTime = 5000; // Minimum time between location updates in ms
    private final float minDistance = 20; // Minimum distance between location updates in meters
    private final Context myContext = this;
    static String currentCity = "";
    static Location currentLocation;

    private LocationManager locationManager;
    private LocationListener locationListener;

    // Coordinates for gyms...format: [xmin, xmax, ymin, ymax, xcenter, ycenter]
    private static final double[] erc_coordinates = {32.88383620000000, 32.88715170000000, -117.24372250000000, -117.24077200000000, 32.8856775, -117.2431516};
    private static final double[] geisel_coordinates = {32.88082500000000, 32.88130900000000, -117.23772510000000,  -117.23737960000000, 32.8809428, -117.2375672};
    private static final double[] marshall_coordinates = {32.88111470000000, 32.88372810000000, -117.24372880000000, -117.24068620000000, 32.8817312, -117.2405123};
    private static final double[] matthews_quad_coordinates = {32.87841450000000, 32.8793886, -117.23594630000000, -117.2348797, 32.878884, -117.2352981};
    private static final double[] muir_coordinates = {32.8781519, 32.8796116, -117.2435398, -117.2393556, 32.8785754, -117.2405035};
    private static final double[] muir_parking_coordinates = {32.87975770000000, 32.88096210000000, -117.24361910000000, -117.24084500000000, 32.88065, -117.2420359};
    private static final double[] price_center_coordinates = {32.87916090000000, 32.88012500000000, -117.23744060000000, -117.23561670000000, 32.8792534, -117.236607};
    private static final double[] revelle_coordinates = {32.87356870000000, 32.87634240000000, -117.24344660000000, -117.24078330000000, 32.8751537, -117.2420252};
    private static final double[] revelle_parking_coordinates = {32.87153220000000, 32.87362280000000, -117.24340370000000, -117.24134380000000, 32.8731173, -117.2430552};
    private static final double[] rimac_coordinates = {32.88500040000000, 32.88582930000000, 32.88582930000000, -117.23874020000000, 32.8852181, -117.2392035};
    private static final double[] sixth_apartment_coordinates = {32.87708990000000, 32.87929750000000, -117.23179960000000, -117.22907440000000, 32.878767, -117.2309745};
    private static final double[] sixth_res_hall_coordinates = {32.8771848, 32.8786655, -117.2340676, -117.2325427, 32.8779176, -117.2328753};
    private static final double[] va_hospital_coordinates = {32.87184760000000, 32.87670440000000, -117.23369410000000, -117.22977810000000, 32.8746942, -117.2316182};
    private static final double[] village_coordinates = {32.88383620000000, 32.88880940000000, -117.24077200000000, -117.24082570000000, 32.8885155, -117.2423148};
    private static final double[] warren_coordinates = {32.88216430000000, 32.88504740000000, -117.23338620000000, -117.23163740000000, 32.8825601, -117.2336781};
    private static final double[] warren_field_coordinates = {32.87927040000000, 32.88124370000000, -117.23189610000000, -117.22914950000000, 32.8800642, -117.2305882};
    private static final double[] warren_mall_coordinates = {32.88075420000000, 32.88216430000000, -117.23630980000000, -117.23338620000000, 32.8811544, -117.2348583};
    // Array for gyms...order: warren field, matthews quad, village, warren mall, sixth apartment, rimac, price center, erc, sixth res halls, va hospital,
    // revelle parking, warren, muir, marshall, revelle, muir parking, geisel
    private static final int[] gyms = {R.drawable.location_azalea_gym, R.drawable.location_celadon_gym, R.drawable.location_cerulean_gym, R.drawable.location_clanwood_gym,
            R.drawable.location_fuchsia_gym, R.drawable.location_gen1_gym, R.drawable.location_sinnoh_gym, R.drawable.location_goldenrod_gym, R.drawable.location_viridian_gym,
            R.drawable.location_kanto_gym, R.drawable.location_pewter_gym, R.drawable.location_pokecenter, R.drawable.location_saffron_gym, R.drawable.location_gen1_gym2,
            R.drawable.location_vermilion_gym, R.drawable.location_violet_gym, R.drawable.location_johto_gym};

    private static final Map<String, double[]> locations;
    static
    {
        locations = new HashMap<String, double[]>();
        locations.put("ERC", erc_coordinates);
        locations.put("Geisel", geisel_coordinates);
        locations.put("Marshall", marshall_coordinates);
        locations.put("Matthews Quad", matthews_quad_coordinates);
        locations.put("Muir", muir_coordinates);
        locations.put("Muir Parking", muir_parking_coordinates);
        locations.put("Price Center", price_center_coordinates);
        locations.put("Revelle", revelle_coordinates);
        locations.put("Revelle Parking", revelle_parking_coordinates);
        locations.put("Rimac", rimac_coordinates);
        locations.put("Sixth Apartment", sixth_apartment_coordinates);
        locations.put("Sixth Res Halls", sixth_res_hall_coordinates);
        locations.put("VA Hospital", va_hospital_coordinates);
        locations.put("Village", village_coordinates);
        locations.put("Warren", warren_coordinates);
        locations.put("Warren Field", warren_field_coordinates);
        locations.put("Warren Mall", warren_mall_coordinates);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        int count = 0;

        for (Map.Entry<String, double[]> entry : locations.entrySet()) {
            String key = entry.getKey();
            double[] value = entry.getValue();
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(gyms[count]))
                    .position(new LatLng(value[4], value[5])).title(key));
            count++;
        }
    }

    /**
     * Determine the current location. If the location is new, update with a Toast message
     */
    private void setLocation(Location location) {
        currentLocation = location;

        for (Map.Entry<String, double[]> entry : locations.entrySet()) {
            String key = entry.getKey();
            double[] value = entry.getValue();

            if (location.getLatitude() > value[0] && location.getLatitude() < value[1]
                    && location.getLongitude() > value[2] && location.getLongitude() < value[3]) {
                if (!currentCity.equals(key)) {
                    currentCity = key;
                    CurrentUser.setCurrentCity(currentCity);
                    Toast.makeText(myContext, "Now Entering: " + currentCity, Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }
    }
}
