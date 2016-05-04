package com.victor.routeviewer;

import android.content.Context;
import android.os.AsyncTask;

import com.victor.routeviewer.view.LockProgressDialog;


public class RouteLoader extends AsyncTask<String, Void, Route> {

    private LockProgressDialog progress;
    private Context context;

    public RouteLoader(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = new LockProgressDialog(context);
        progress.setMessage(context.getResources().getString(R.string.progress_message));
        progress.show();
    }

    @Override
    protected Route doInBackground(String[] params) {
        return new Route(params[0]);
    }


    @Override
    protected void onPostExecute(Route route) {
        super.onPostExecute(route);
        progress.cancel();
    }
}