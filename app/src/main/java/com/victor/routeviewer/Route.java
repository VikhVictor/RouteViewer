package com.victor.routeviewer;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Date;
import java.util.ArrayList;


public class Route {


    private ArrayList<LatLng> waypoints;
    private ArrayList<String> timeList;
    private String path;
    private String trackName;


    public Route () {
        //this.path = path;
        waypoints = new ArrayList<>();
        timeList = new ArrayList<>();
    }

//    pu

    public void setTrackName(String name) {
        trackName = name;
    }

    public String getTrackName() {
        return trackName;
    }

    public void addWaypoint(LatLng p) {
        waypoints.add(p);
    }

    public void addWaypointTime(String time) {
        timeList.add(time);
    }

    public String getRouteInfo() {
        return "";
    }

    public ArrayList<LatLng> getWaypoints() {
        return waypoints;
    }

    public LatLng getStartWaypoint() {
        if (waypoints.size() != 0)
            return waypoints.get(0);
        else
            return new LatLng(0,0);
    }

    public LatLng getFinishWaypoint() {
        if (waypoints.size() != 0) {
            return waypoints.get(waypoints.size() - 1);
        } else {
            return new LatLng(0,0);
        }
    }

    public String getWaypointTime(int i) {
        if (i < timeList.size()) {
            return timeList.get(i);
        } else {
            return "none";
        }
    }

    public String getInfo() {
        Log.d("MyLog", "Route's start : \n" + waypoints.size() + " waypoints\n" + timeList.get(0));
        return waypoints.size() + " waypoints " + timeList.get(0);
    }
}
