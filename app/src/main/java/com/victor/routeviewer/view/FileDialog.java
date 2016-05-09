package com.victor.routeviewer.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.victor.routeviewer.DialogListener;
import com.victor.routeviewer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


public class FileDialog extends AlertDialog.Builder {

    private Context context;

    LinearLayout dialogLayout, scrollLayout;
    HorizontalScrollView breadCrumbs;
    LinkedList<Crumb> buttons;
    private ListView fileList;
    private ArrayAdapter<String> adapter;
    List<File> files;
    ArrayList<String> filenames;
    private DialogListener listener;
    private String currentPath;

    public FileDialog(final Context context, String path) {
        super(context);
        this.context = context;
        initDialogView();
        currentPath = path;
        refreshDialogView(currentPath);

        this.setTitle(R.string.dialog_hint).setView(dialogLayout).setNegativeButton(R.string.cancel_button, null);
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
        breadCrumbs.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        scrollLayout = new LinearLayout(context);
        scrollLayout.setOrientation(LinearLayout.HORIZONTAL);
        scrollLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        breadCrumbs.addView(scrollLayout);
        dialogLayout.addView(breadCrumbs);

        fileList = new ListView(context);
        fileList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPath = files.get(position).getAbsolutePath();
                if (files.get(position).isDirectory()) {
                    currentPath = files.get(position).getAbsolutePath();
                    refreshDialogView(currentPath);
                } else {
                    listener.onFileSelected(currentPath);
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

    public FileDialog setListener(DialogListener listener) {
        this.listener = listener;
        return this;
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

    public String getCurrentPath() {
        return currentPath;
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
