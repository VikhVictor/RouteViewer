package com.victor.routeviewer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.victor.routeviewer.view.LockProgressDialog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class RouteLoader extends AsyncTask<String, Void, Route> {

    private static final String TAG_TIME = "time";

    private static final int ERR_FILE_UNEXIST = -1;
    private static final int ERR_FILE_EMPTY = -2;
    private static final int ERR_BAD_DATA = -3;

    private LockProgressDialog progress;
    private Context context;

    private OnLoadCallback response;

    private static final String TAG_TRACK = "trk";
    private static final String TAG_SEGMENT = "trkseg";
    private static final String TAG_POINT = "trkpt";

    private static final String ATTR_LON = "lon";
    private static final String ATTR_LAT = "lat";

    /*
        ERR_FILE_UNEXIST - файл отсутствует
        ERR_FILE_EMPTY - файл пустой
        ERR_BAD_DATA - ошибка при чтении

     */
    int error = 0;


    public RouteLoader(Context context) {
        this.context = context;
    }

    public void setOnLoadingFinishListener(OnLoadCallback listener) {
        this.response = listener;
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
        File gpx = new File(params[0]);

        if (gpx.exists()) {
            try {
                XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();

                /*ArrayList<LatLng> readedWaypoints = new ArrayList<>();
                ArrayList<String> dates = new ArrayList<>();*/
                Route route = new Route();
                parser.setInput(new FileInputStream(gpx), "iso-8859-1");
                while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {

                    switch (parser.getEventType()) {

                        case XmlPullParser.START_DOCUMENT:
                            //Log.d("MyLog", "start document");
                            break;
                        case XmlPullParser.START_TAG: {
                            String tag = parser.getName();
                            if (tag.equals(TAG_TRACK)) {
                                //Log.d("MyLog", "namespace count : " + parser.getNamespaceCount(parser.getDepth()) + "");
                            }

                            if (tag.equals(TAG_POINT)) {

                                route.addWaypoint(new LatLng(Double.parseDouble(parser.getAttributeValue(null, ATTR_LAT)),
                                        Double.parseDouble(parser.getAttributeValue(null, ATTR_LON))));
                            }

                            if (tag.equals(TAG_TIME)) {
                                //Log.d("MyLog", "flag " + parser.getName());
                                parser.next();
                                route.addWaypointTime(parser.getText());
                            }

                            break;
                        }

                        case XmlPullParser.END_DOCUMENT:
                            break;
                        default:
                            break;
                    }
                    parser.next();
                }
                if (route.getWaypoints().size() == 0) {
                    error = ERR_FILE_EMPTY;
                    return null;
                }
                return route;
            } catch (FileNotFoundException e) {
                error = ERR_FILE_UNEXIST;
                //e.printStackTrace();
                return null;
            } catch (XmlPullParserException e) {
                error = ERR_BAD_DATA;
                //e.printStackTrace();
                return null;
            } catch (IOException e) {
                error = ERR_BAD_DATA;
                //e.printStackTrace();
                return null;
            }
        } else {
            error = ERR_FILE_UNEXIST;
            return null;
        }
    }



    @Override
    protected void onPostExecute(Route route) {
        super.onPostExecute(route);
        response.onLoadingFinish(route, error);
        progress.cancel();
    }
}