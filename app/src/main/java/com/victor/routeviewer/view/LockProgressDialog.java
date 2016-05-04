package com.victor.routeviewer.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ProgressBar;

/**
 * Created by Виктор on 05.05.2016.
 */
public class LockProgressDialog extends ProgressDialog {
    public LockProgressDialog(Context context) {
        super(context);
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
