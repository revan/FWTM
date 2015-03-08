package com.rsopher.fwtm;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;

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
    private ServerClient service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setUpMapIfNeeded();
        final ImageButton button = (ImageButton) findViewById(R.id.attack_button);
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent qrDroid = new Intent("la.droid.qr.scan");
                startActivityForResult(qrDroid, 1);
            }
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        last_location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (last_location == null) {
            last_location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        //TEMP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("http://64222b7e.ngrok.com")
                .build();
        service = restAdapter.create(ServerClient.class);

        final Handler handler = new Handler();

        final Runnable poll = new Runnable() {
            @Override
            public void run() {
                ServerStatus status = service.getStatus();

                Player[] players = status.players;
                updatePlayerMarkers(players);

                Map<String, Block> blocks = status.blocks;
                updateBlockMarkers(blocks);

                if (playerId == null) {
                    updateBounds();
                    if (last_location != null) {
                        playerId = service.registerPlayer("Bob", "" + last_location.getLatitude(), "" + last_location.getLongitude());
                        if (playerId != null) {
                            button.setEnabled(true);
                            generateQR(playerId);
                        }
                    }
                } else {
                    service.updateLocation("" + last_location.getLatitude(), "" + last_location.getLongitude(), playerId);
                }

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
                playerMarkerMap.get(p.name).remove();
            }

            float hue = BitmapDescriptorFactory.HUE_RED;
            if (playerId!= null && p.id == Integer.parseInt(playerId)) {
                hue = BitmapDescriptorFactory.HUE_MAGENTA;
            }
            playerMarkerMap.put(p.name, mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(p.location[0], p.location[1]))
                    .title(p.name)
                    .alpha(.9f)
                    .icon(BitmapDescriptorFactory.defaultMarker(hue))));
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
                    .icon(BitmapDescriptorFactory.defaultMarker(getHueForBlock(b)))));
        }
    }

    private static float getHueForBlock(Block block) {
        int c = block.control;
        if (c > 10) return BitmapDescriptorFactory.HUE_BLUE;
        if (c < -10) return BitmapDescriptorFactory.HUE_GREEN;
        return BitmapDescriptorFactory.HUE_YELLOW;
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call setUpMap() once when {@link #mMap} is not null.
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
        }
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

    private void generateQR(String id) {
        Intent qrDroid = new Intent("la.droid.qr.encode");
        qrDroid.putExtra("la.droid.qr.code", id);
        startActivityForResult(qrDroid, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String result = data.getExtras().getString("la.droid.qr.result");

        if (requestCode == 0) {
            Intent browser = new Intent(Intent.ACTION_VIEW);
            browser.setData(Uri.parse(result));
            startActivity(browser);
        } else if (requestCode == 1) {
            service.sendAttack(playerId, result);
        }
    }
}
