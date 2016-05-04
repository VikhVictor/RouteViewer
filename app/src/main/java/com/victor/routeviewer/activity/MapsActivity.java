package com.victor.routeviewer.activity;

import android.os.PersistableBundle;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.victor.routeviewer.R;
import com.victor.routeviewer.Route;
import com.victor.routeviewer.RouteLoader;

import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String KEY_ROUTE = "state";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SupportMapFragment mFragment;



    android.support.v7.widget.Toolbar toolbar;

    //private
    Route route;
    private boolean drawDots = false;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.maps_toolbar);
        initToolbar();
    /*    if (savedInstanceState != null && savedInstanceState.get(KEY_ROUTE) != null) {
            Gson gson = new Gson();
            route = gson.fromJson(savedInstanceState.getString(KEY_ROUTE), Route.class);
        } else {*/
            RouteLoader loader = new RouteLoader(this);
        try {
            route = loader.execute(getIntent().getExtras().getString(MenuActivity.INTENT_KEY_PATH)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //  }
        setUpMapIfNeeded();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*Log.d("MyLog", "on destroy");
        getSupportFragmentManager().beginTransaction().remove(spf).commit();
        Log.d("MyLog", "Fragment removed");*/
    }

    private int initToolbar() {
        if (toolbar == null) { return -1; }

        toolbar.setTitle(R.string.title_activity_maps);
        toolbar.inflateMenu(R.menu.map_tools);

        toolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.action_local_map:
                        setLocalMap();
                        return true;
                    case R.id.action_map:
                        setSatelliteMap();
                        return true;
                    case R.id.action_focus:
                        focusOnStartPoint();
                        return true;
                    case R.id.action_view:
                        drawDots = !drawDots;
                        changeRouteView();
                        return true;

                    default:
                        return false;
                }

            }
        });

        return 0;
    }

    private void changeRouteView() {
        if (mMap != null && route != null) {
            mMap.clear();
            if (drawDots) {

                for (LatLng ll : route.waypoints)
                    mMap.addMarker(new MarkerOptions().position(ll)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.point)))
                            .setTitle("Time");
            } else {
                mMap.addMarker(new MarkerOptions().position(route.waypoints.get(0)).title("Start"));
                mMap.addMarker(new MarkerOptions().position(route.waypoints.get(route.waypoints.size() - 1)).title("Finish"));
                mMap.addPolyline(new PolylineOptions().addAll(route.waypoints));
            }
        }
    }

    private void focusOnStartPoint() {
        if (mMap != null && route != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.waypoints.get(0), 10));
        }
    }

    private void setSatelliteMap() {
        if (mMap != null)
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    private void setLocalMap() {
        if (mMap != null)
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        /*Log.d("MyLog", "OnSaveState");
        if (route != null) {
            Gson gson = new Gson();
            outState.putString(KEY_ROUTE, gson.toJson(route));
            Log.d("MyLog", outState.getString(KEY_ROUTE));
            //Log.d("MyLog", gson.toJson(route));
        }*/
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
        if (route != null) {
            mMap.addMarker(new MarkerOptions().position(route.waypoints.get(0)).title("Start"));
            mMap.addMarker(new MarkerOptions().position(route.waypoints.get(route.waypoints.size() - 1)).title("Finish"));
            mMap.addPolyline(new PolylineOptions().addAll(route.waypoints));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.waypoints.get(0), 10));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }


}
