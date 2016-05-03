package com.victor.routeviewer;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;


public class Route {

    private static final String TAG_TRACK = "trk";
    private static final String TAG_SEGMENT = "trkseg";
    private static final String TAG_POINT = "trkpt";

    private static final String ATTR_LON = "lon";
    private static final String ATTR_LAT = "lat";

    ArrayList<LatLng> waypoints;
    Context context;
    String path;
    String trackName;
    int trackId;

    public Route (Context context, String path) {
        //trackId = id;
        this.context = context;
        this.path = path;
        waypoints = new ArrayList<LatLng>();
        Log.d("MyLog", "Route constructor");
        loadWayPoints();

    }

    private void loadWayPoints() {

        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            //parser.setInput(context.getResources().openRawResource(R.raw.track2), "iso-8859-1");
            File gpx = new File(path);
            parser.setInput(new FileInputStream(gpx), "iso-8859-1");
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {

                switch (parser.getEventType()) {

                    case XmlPullParser.START_DOCUMENT :
                        Log.d("MyLog", "start document");
                        break;
                    case XmlPullParser.START_TAG : {
                        String tag = parser.getName();
                        if (tag.equals(TAG_TRACK)) {

                        }
                        if (tag.equals(TAG_SEGMENT)) {

                        }
                        if (tag.equals(TAG_POINT)) {

                            waypoints.add(new LatLng(Double.parseDouble(parser.getAttributeValue(null, ATTR_LAT)),
                                                    Double.parseDouble(parser.getAttributeValue(null, ATTR_LON))));
                        }
                        break;
                    }

                    case XmlPullParser.END_DOCUMENT :
                        Log.d("MyLog", "end of document");
                        break;
                    default:
                        break;
                }
                parser.next();
            }
        } catch (Exception e) {
            Log.i("MyLog", "Failed in parsing XML", e);
        }


    }


}
