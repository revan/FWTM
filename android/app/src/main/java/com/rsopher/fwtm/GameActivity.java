package com.rsopher.fwtm;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import retrofit.RestAdapter;

public class GameActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private Map<String, Marker> playerMarkerMap = new HashMap<>();
    private Map<String, Marker> blockMarkerMap = new HashMap<>();
    private String playerId;
    private Location last_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setUpMapIfNeeded();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        playerId = "1";
        //TODO get from server

        //TEMP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("http://7cbc869b.ngrok.com")
                .build();
        final ServerClient service = restAdapter.create(ServerClient.class);


        final Handler handler = new Handler();

        final Runnable poll = new Runnable() {
            @Override
            public void run() {
                ServerStatus status = service.getStatus();

                Player[] players = status.players;
                updatePlayerMarkers(players);

                Map<String, Block> blocks = status.blocks;
                updateBlockMarkers(blocks);

                updateBounds();

                if (last_location != null)
                    service.updateLocation(""+last_location.getLatitude(), ""+last_location.getLongitude(), playerId);

                handler.postDelayed(this, 3000);
            }
        };

        handler.postDelayed(poll, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void updateBounds() {
        com.google.android.gms.maps.model.LatLngBounds.Builder b = LatLngBounds.builder();
        for (Marker m : playerMarkerMap.values())
            b = b.include(m.getPosition());

        for (Marker m : blockMarkerMap.values())
            b = b.include(m.getPosition());


        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(b.build(),
                getWindowManager().getDefaultDisplay().getHeight(),
                getWindowManager().getDefaultDisplay().getWidth(), 5));
    }

    private void updatePlayerMarkers(Player[] players) {
        for (Player p : players) {
            if (playerMarkerMap.containsKey(p.name)) {
                playerMarkerMap.remove(p.name);
            }

            playerMarkerMap.put(p.name, mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(p.location[0], p.location[1]))
                    .title(p.name)
                    .alpha(.9f)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));
        }
    }

    private void updateBlockMarkers(Map<String, Block> blocks) {
        for (String key : blocks.keySet()) {
            Block b = blocks.get(key);

            if (blockMarkerMap.containsKey(key)) {
                blockMarkerMap.get(key).remove();
            }

            blockMarkerMap.put(key, mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(b.center[0], b.center[1]))
                    .title("" + b.control)
                    .alpha(.9f)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))));
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
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
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
//        last_location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location loc) {
            last_location = loc;
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };
}
