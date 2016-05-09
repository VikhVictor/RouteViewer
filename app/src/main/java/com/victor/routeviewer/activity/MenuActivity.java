package com.victor.routeviewer.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.victor.routeviewer.DialogListener;
import com.victor.routeviewer.view.FileDialog;
import com.victor.routeviewer.R;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;


public class MenuActivity extends AppCompatActivity {


    private static final String SET_KEY = "routes";
    public static final String INTENT_KEY_PATH = "path";

    Toolbar toolbar;
    ListView list;
    ArrayAdapter<String> adapter;
    ArrayList<String> userRoutes;
    AlertDialog alert;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initToolbar();

        userRoutes = new ArrayList<String>();
        list = (ListView) findViewById(R.id.listView);
        loadUserRoutes();

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_menu);
        toolbar.inflateMenu(R.menu.toolbar_tools);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add :
                        openFileDialog();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void loadUserRoutes() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> set = preferences.getStringSet(SET_KEY, null);
        if (set == null) {
            userRoutes.clear();
            userRoutes.add(getResources().getString(R.string.empty_user_routes));
        } else {
            userRoutes.clear();
            for (String s : set) {
                userRoutes.add(s);
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userRoutes);
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userRoutes);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra(INTENT_KEY_PATH, userRoutes.get(position));
                startActivity(intent);
            }
        });
    }

    public void openFileDialog() {
        FileDialog dialogBuilder = new FileDialog(this);
        dialogBuilder.setListener(new DialogListener() {
            @Override
            public void onFileSelected(String gpx) {
                alert.cancel();
                //save path to userRoutes
                preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Set<String> tmp = preferences.getStringSet(SET_KEY, new TreeSet<String>());
                tmp.add(gpx);
                userRoutes.clear();
                for (String s : tmp) {
                    userRoutes.add(s);
                }
                adapter.notifyDataSetChanged();
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.putStringSet(SET_KEY, tmp);
                editor.commit();

            }
        });
        alert = dialogBuilder.create();

        alert.show();

    }
}
