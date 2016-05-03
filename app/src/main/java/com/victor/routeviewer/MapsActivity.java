package com.victor.routeviewer;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.xmlpull.v1.XmlPullParserFactory;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SupportMapFragment mFragment;

    android.support.v7.widget.Toolbar toolbar;

    //private
    Route route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.maps_toolbar);
        initToolbar();
        route = new Route(this, getIntent().getExtras().getString(MenuActivity.INTENT_KEY_PATH));
        setUpMapIfNeeded();
    }

    private int initToolbar() {
        if (toolbar == null) { return -1; }

        toolbar.setTitle(R.string.title_activity_maps);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        return 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            mFragment.getMapAsync(this);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setZoomGesturesEnabled(true);
        mMap.addMarker(new MarkerOptions().position(route.waypoints.get(0)).title("Start"));
        mMap.addMarker(new MarkerOptions().position(route.waypoints.get(route.waypoints.size() - 1)).title("Finish"));
        mMap.addPolyline(new PolylineOptions().addAll(route.waypoints));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.waypoints.get(0),10));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }
}
