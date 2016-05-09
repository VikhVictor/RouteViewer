package com.victor.routeviewer.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.victor.routeviewer.DialogListener;
import com.victor.routeviewer.RouteAdapter;
import com.victor.routeviewer.RouteItem;
import com.victor.routeviewer.view.FileDialog;
import com.victor.routeviewer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;


public class MenuActivity extends AppCompatActivity
                            implements android.support.v7.widget.Toolbar.OnMenuItemClickListener,
                                        DialogListener {


    private static final String SET_KEY = "routes";
    public static final String INTENT_KEY_PATH = "path";
    private static final String BUNDLE_KEY_DIALOG_STATE = "isOpenDialog";
    private static final String BUNDLE_KEY_DIALOG_PATH = "dialogPath";


    Toolbar toolbar;
    ListView list;
    FileDialog dialogBuilder;

    ArrayList<RouteItem> items;
    RouteAdapter adapter;
    AlertDialog alert;
    SharedPreferences preferences;
    private String path;
    private boolean isOpenDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        path = Environment.getExternalStorageDirectory().toString();
        isOpenDialog = false;
        initToolbar();

        list = (ListView) findViewById(R.id.listView);
        items = new ArrayList<RouteItem>();
        loadUserRoutes();
        loadSavedInstance(savedInstanceState);
        dialogBuilder.setListener(this);
    }


    private void loadSavedInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean(BUNDLE_KEY_DIALOG_STATE)) {
            isOpenDialog = true;
            if (savedInstanceState.getString(BUNDLE_KEY_DIALOG_PATH) != null)
                path = savedInstanceState.getString(BUNDLE_KEY_DIALOG_PATH);
        }
        dialogBuilder = new FileDialog(this, path);
        if (isOpenDialog) {
            alert = dialogBuilder.create();
            alert.show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_KEY_DIALOG_STATE, isOpenDialog);
        if (isOpenDialog) {
            outState.putString(BUNDLE_KEY_DIALOG_PATH, dialogBuilder.getCurrentPath());
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_menu);
        toolbar.inflateMenu(R.menu.toolbar_tools);
        toolbar.setOnMenuItemClickListener(this);
    }

    private void loadUserRoutes() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> set = preferences.getStringSet(SET_KEY, null);
        if (set == null) {
            items.clear();
            items.add(new RouteItem(getResources().getString(R.string.empty_user_routes), ""));
        } else {
            items.clear();
            for (String s : set) {
                File f = new File(s);
                if (f.exists()) {
                    items.add(new RouteItem(s, String.format("%.2f", f.length() / 1024.0 / 1024.0)
                            + getResources().getString(R.string.size)));
                }
            }
        }
        adapter = new RouteAdapter(this, items);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra(INTENT_KEY_PATH, items.get(position).getPath());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add: {
                dialogBuilder = new FileDialog(this, path);
                dialogBuilder.setListener(this);
                alert = dialogBuilder.create();
                isOpenDialog = true;
                alert.show();
                return true;
            }
            default:
                return false;
        }
    }

    @Override
    public void onFileSelected(String gpx) {
        alert.cancel();
        isOpenDialog = false;
        //save path to userRoutes
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> tmp = preferences.getStringSet(SET_KEY, new TreeSet<String>());
        tmp.add(gpx);
        items.clear();
        for (String s : tmp) {
            File f = new File(s);
            if (f.exists()) {
                items.add(new RouteItem(s, String.format("%.2f ", f.length() / 1024.0 / 1024.0)
                        + getResources().getString(R.string.size)));
            }
        }
        adapter.notifyDataSetChanged();
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putStringSet(SET_KEY, tmp);
        editor.commit();
    }
}
