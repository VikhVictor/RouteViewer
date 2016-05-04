package com.victor.routeviewer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
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
    private boolean drawDots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.maps_toolbar);
        initToolbar();
        RouteLoader loader = new RouteLoader(this);
        loader.execute(getIntent().getExtras().getString(MenuActivity.INTENT_KEY_PATH));

        setUpMapIfNeeded();
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
        mMap.clear();
        if (drawDots) {
            drawDots = false;
        } else {
            drawDots = true;
        }
        for (LatLng ll : route.waypoints)
            mMap.addMarker(new MarkerOptions().position(ll));
    }

    private void focusOnStartPoint() {
        if (route != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.waypoints.get(0), 10));
        }
    }

    private void setSatelliteMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    private void setLocalMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
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
        /*mMap.addMarker(new MarkerOptions().position(route.waypoints.get(0)).title("Start"));
        mMap.addMarker(new MarkerOptions().position(route.waypoints.get(route.waypoints.size() - 1)).title("Finish"));
        mMap.addPolyline(new PolylineOptions().addAll(route.waypoints));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.waypoints.get(0),10));*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }

    class RouteLoader extends AsyncTask<String, Void, Void> {

        private ProgressDialog progress;
        private Context context;

        RouteLoader (Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(context);
            progress.setMessage(context.getResources().getString(R.string.progress_message));
            progress.show();
        }

        @Override
        protected Void doInBackground(String[] params) {
            route = new Route(params[0]);
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.cancel();
            if (mMap != null) {
                mMap.addMarker(new MarkerOptions().position(route.waypoints.get(0)).title("Start"));
                mMap.addMarker(new MarkerOptions().position(route.waypoints.get(route.waypoints.size() - 1)).title("Finish"));
                mMap.addPolyline(new PolylineOptions().addAll(route.waypoints));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.waypoints.get(0),10));
            }
        }
    }


}
