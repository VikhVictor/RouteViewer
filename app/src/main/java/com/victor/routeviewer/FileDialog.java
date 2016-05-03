package com.victor.routeviewer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Виктор on 03.05.2016.
 */
public class FileDialog extends AlertDialog.Builder {

    private static final String TITLE = "Choose .gpx file";
    private static String currentPath;
    private Context context;

    LinearLayout dialogLayout, scrollLayout;
    HorizontalScrollView breadCrumbs;
    LinkedList<Crumb> buttons;
    private ListView fileList;
    private ArrayAdapter<String> adapter;
    List<File> files;
    ArrayList<String> filenames;


    public FileDialog(final Context context) {
        super(context);
        this.context = context;
        //currentPath = Environment.getExternalStorageDirectory().toString();
        initDialogView();
        refreshDialogView(Environment.getExternalStorageDirectory().toString());

        this.setTitle(TITLE).setView(dialogLayout).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(context, MapsActivity.class));
            }
        }).setNegativeButton("Cansel", null);
    }

    private void refreshDialogView(String path) {
        buttons = getBreadCrumbs(path);
        scrollLayout.removeAllViews();
        for (Button b : buttons) {
            scrollLayout.addView(b);
        }
        breadCrumbs.scrollTo(buttons.get(0).getRight(), 0);
        if (adapter != null) {
            //adapter.addAll(getFileList(path));
            getFileList(path);
            adapter.notifyDataSetChanged();
            fileList.setAdapter(adapter);

        } else {
            Log.d("MyLog", "adapter init");
            getFileList(path);
            adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, filenames);
            fileList.setAdapter(adapter);

        }


    }

    private void initDialogView() {
        dialogLayout = new LinearLayout(context);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);

        breadCrumbs = new HorizontalScrollView(context);
        breadCrumbs.setHorizontalScrollBarEnabled(true);
        breadCrumbs.setVerticalScrollBarEnabled(true);
        breadCrumbs.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        scrollLayout = new LinearLayout(context);
        scrollLayout.setOrientation(LinearLayout.HORIZONTAL);
        scrollLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        breadCrumbs.addView(scrollLayout);


        dialogLayout.addView(breadCrumbs);

        fileList = new ListView(context);
        fileList.setSelected(true);
        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("MyLog", files.get(position).getAbsolutePath());
                if (files.get(position).isDirectory()) {
                    refreshDialogView(files.get(position).getAbsolutePath());
                } else {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("path", files.get(position).getAbsolutePath());
                    context.startActivity(intent);
                    //view.setSelected(true);
                }
            }
        });
        dialogLayout.addView(fileList);
        filenames = new ArrayList<String>();
        files = new ArrayList<File>();

    }

    private LinkedList<Crumb> getBreadCrumbs(String currentPath) {

        LinkedList<Crumb> list = new LinkedList<Crumb>();
        File directory = new File(currentPath);
        do {
            Crumb crumb = new Crumb(context, directory.getPath() ,directory.getName());
            crumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Crumb c = (Crumb) v;
                    refreshDialogView(c.path);
                }
            });
            list.addFirst(crumb);
            directory = directory.getParentFile();
        } while (directory.getParent() != null);
        return  list;
    }

    private void getFileList(String path) {
        File[] tmp = new File(path).listFiles();
        files.clear();
        for (File f : tmp) {
            if (f.isDirectory()) {
                files.add(f);
            } else {
                int dotPos = f.getName().toString().lastIndexOf('.');
                if (f.isFile() && f.getName().toString().substring(dotPos + 1).equals("gpx")) {
                    files.add(f);
                }
            }
        }
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                if (lhs.isDirectory() && rhs.isFile()) {
                    return -1;
                } else if (lhs.isFile() && rhs.isDirectory()) {
                    return 1;
                } else {
                    return lhs.getName().compareTo(rhs.getName());
                }
            }
        });

        filenames.clear();
        for (int i = 0; i < files.size(); i++) {
            filenames.add(files.get(i).getName().toString());
        }
    }

    class Crumb extends Button {

        String path;
        String directory;

        public Crumb(Context context, String path, String directory) {
            super(context);
            this.path = path;
            this.directory = directory;
            this.setText(this.directory);
        }



    }


}
