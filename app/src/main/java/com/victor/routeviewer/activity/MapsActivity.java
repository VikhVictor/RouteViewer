package com.victor.routeviewer.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import com.google.gson.Gson;
import com.victor.routeviewer.OnLoadCallback;
import com.victor.routeviewer.R;
import com.victor.routeviewer.Route;
import com.victor.routeviewer.RouteLoader;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, OnLoadCallback {

    private static final String KEY_ROUTE = "state";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SupportMapFragment mFragment;
    RouteLoader loader;

    android.support.v7.widget.Toolbar toolbar;

    Route route;
    private boolean drawDots = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.get(KEY_ROUTE) != null) {
            Gson gson = new Gson();
            route = gson.fromJson(savedInstanceState.getString(KEY_ROUTE), Route.class);
        } else {
            loader = new RouteLoader(this);
            loader.setOnLoadingFinishListener(this);
            route = null;
            loader.execute(getIntent().getExtras().getString(MenuActivity.INTENT_KEY_PATH));
        }
        setContentView(R.layout.activity_maps);
        toolbar = new android.support.v7.widget.Toolbar(this);
        //setSupportActionBar(toolbar);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.maps_toolbar);
        initToolbar();

        setUpMapIfNeeded();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            if (drawDots) {
                mMap.clear();
            } else {
                mMap.addMarker(new MarkerOptions().position(route.getStartWaypoint()).title(route.getInfo()));
                mMap.addMarker(new MarkerOptions().position(route.getWaypoints().get(route.getWaypoints().size() - 1)).title("Finish"));
                mMap.addPolyline(new PolylineOptions().addAll(route.getWaypoints()));
            }
        }
    }

    private void focusOnStartPoint() {
        if (mMap != null && route != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.getWaypoints().get(0), 10));
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (route != null) {
            //mFragment.onSaveInstanceState(outState);
            Gson gson = new Gson();
            outState.putString(KEY_ROUTE, gson.toJson(route));
        } else {
            if (loader != null) {
                loader.cancel(true);
            }
        }
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            mFragment.getMapAsync(this);
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
            mMap.addMarker(new MarkerOptions().position(route.getStartWaypoint()).title("Start point : ").snippet(route.getInfo()));
            mMap.addMarker(new MarkerOptions().position(route.getFinishWaypoint()).title("Finish"));
            mMap.addPolyline(new PolylineOptions().addAll(route.getWaypoints()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.getWaypoints().get(0), 10));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MyLog", "on map ready");
        mMap = googleMap;
        setUpMap();
    }


    @Override
    public void onLoadingFinish(Route route, int error) {
        if (route != null) {
            this.route = route;
            if (mMap != null) {
                setUpMap();
            } else {
                setUpMapIfNeeded();
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.error))
                    .setPositiveButton(getResources().getString(R.string.error_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    }).setMessage(getResources().getIdentifier("error" + -error, "string", getPackageName()));
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }
}
