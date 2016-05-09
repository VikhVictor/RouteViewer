package com.victor.routeviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RouteAdapter extends BaseAdapter {

    ArrayList<RouteItem> items;
    Context context;
    LayoutInflater inflater;

    public RouteAdapter(Context c, ArrayList<RouteItem> routes) {
        context = c;
        items = routes;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.list_item, parent, false);
        }
        RouteItem routeItem = (RouteItem) getItem(position);
        ((TextView) view.findViewById(R.id.t_name)).setText(routeItem.getName());
        ((TextView) view.findViewById(R.id.t_size)).setText(routeItem.getSize());
        ((TextView) view.findViewById(R.id.t_path)).setText(routeItem.getPath());
        return view;
    }
}
