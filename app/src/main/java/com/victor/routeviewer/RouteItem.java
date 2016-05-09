package com.victor.routeviewer;

public class RouteItem {

    private String path;
    private String name;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String size;
    private String date;

    public RouteItem(String path, String size) {
        this.path = path;
        this.name = path.substring(path.lastIndexOf('/') + 1, path.length());
        this.size = size;
    }

}
