package com.victor.routeviewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MenuActivity extends Activity implements View.OnClickListener{

    private static final int READ_REQUEST_CODE = 42;

    Button bOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        bOpen = (Button) findViewById(R.id.b_open);
        bOpen.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().

                Log.i("MyTag", "Uri: " + data.getExtras().get(""));
        }
    }

    @Override
    public void onClick(View v) {
        FileDialog dialogBuilder = new FileDialog(this);
        AlertDialog alert = dialogBuilder.create();
        alert.show();
    }
}
