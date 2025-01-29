package com.svtech.statussaver.imagepage;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.svtech.statussaver.R;
import com.svtech.statussaver.myinterface.ClearSelectedFiles;
import com.svtech.statussaver.service.SaveFileService;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ImagePage extends Fragment implements ClearSelectedFiles {

    private Context context;

    private RecyclerView recyclerView;
    private File[] allFiles;
    public ImageRecyclerAdapter adapter;
    public SwipeRefreshLayout swipeRefreshLayout;
    private ExecutorService executorService;
    private Handler mainHandler;


    public ImagePage(Context context) {
        this.context = context;
        this.allFiles = new File[0];
        adapter = new ImageRecyclerAdapter(context, allFiles);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        loadFiles();
    }

    public void loadFiles() {
        executorService.execute(new Runnable() {
            private File[] newFiles;

            @Override
            public void run() {
                newFiles = new File[0];
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                path = path + SaveFileService.defaultWhatsappPath;
                newFiles = new File(path).listFiles();
                Comparator<File> comparator = new Comparator<File>() {
                    @Override
                    public int compare(File file, File file2) {
                        if (file.lastModified() > file2.lastModified()) {
                            return -1;
                        } else if (file.lastModified() < file2.lastModified()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                };
                Arrays.sort(newFiles, comparator);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        allFiles = newFiles;
                        adapter.setFiles(allFiles);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_page, container, false);
        loadViews(view);
        return view;
    }

    private void loadViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.imageRecyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFiles();
            }
        });
    }

    @Override
    public void clearSelectedFiles() {
        adapter.clearSelectedFile();
    }


}