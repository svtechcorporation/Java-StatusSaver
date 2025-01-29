package com.svtech.statussaver.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.svtech.statussaver.R;
import com.svtech.statussaver.helper.HelperFunction;
import com.svtech.statussaver.imagepage.ImageRecyclerAdapter;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;


public class FloatingService extends Service {


    private WindowManager windowManager;
    private View floatingView;
    private WindowManager.LayoutParams params;
    private ImageView floatingImageIcon;
    private boolean contentVisible;
    private View contentContainer;
    private int screenWidth;
    private int screenHeight;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingServiceAdapter adapter;
    private ImageRecyclerAdapter imageAdaper;
    private File[] allFiles;
    private ExecutorService executorService;
    private Handler mainHandler;
    private TextView floatingText;




    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("InflateParams")
    @Override
    public void onCreate() {
        super.onCreate();
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_view, null);
        setAttribute();
        setParameters();
        setMoveWidget();
        setClicks();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void setParameters(){
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        contentVisible = false;
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
            screenHeight = size.y;
        } else {
            screenWidth = display.getWidth();  // deprecated
            screenHeight = display.getHeight();  // deprecated
        }
        ViewGroup.LayoutParams params = contentContainer.getLayoutParams();
        params.width = screenWidth - 100;
        contentContainer.setLayoutParams(params);
        allFiles = new File[0];
        adapter = new FloatingServiceAdapter(this, allFiles);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
        loadFiles();
    }

    private void setAttribute(){
        floatingImageIcon = floatingView.findViewById(R.id.floatingIcon);
        contentContainer = floatingView.findViewById(R.id.floatingBody);
        swipeRefreshLayout = floatingView.findViewById(R.id.floatingSwipeRefresh);
        recyclerView = floatingView.findViewById(R.id.floatingRecyclerView);
        floatingText = floatingView.findViewById(R.id.floatingText);
    }

    private void loadFiles(){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                path = path + SaveFileService.defaultWhatsappPath;

                File[] newFiles = new File(path).listFiles();
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


    private void setClicks() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFiles();
            }
        });
    }

    private void toggleContent(){
        if(contentVisible){
            contentContainer.setVisibility(View.GONE);
        } else {
            contentContainer.setVisibility(View.VISIBLE);
            params.x = screenWidth;
            params.y = 50;
            windowManager.updateViewLayout(floatingView, params);
        }
        loadFiles();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMoveWidget(){
        // Set up the layout parameters
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        // Specify the position of the floating widget
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;

        // Add the view to the window
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatingView, params);

        // Make the floating widget draggable

        View.OnTouchListener listener = new View.OnTouchListener(){
            private int lastAction;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int difX = (int)(event.getRawX() - initialTouchX);
                        int difY = (int)(event.getRawY() - initialTouchY);
                        if(difX < 2 && difX > -2 && difY > -2 && difY < 2){
                            contentVisible = !contentVisible;
                            toggleContent();
                            windowManager.updateViewLayout(floatingView, params);
                        }
                        lastAction = event.getAction();
                        closeFloatingView((int) event.getRawX(), (int) event.getRawY());
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingView, params);
                        lastAction = event.getAction();
                        showClosingMessage((int) event.getRawX(), (int) event.getRawY());
                        return true;
                }
                return false;
            }

        };
        floatingView.findViewById(R.id.floatingIcon).setOnTouchListener(listener);
    }

    private void showClosingMessage(int x, int y){
        if(y > screenHeight-200 ){
            floatingText.setVisibility(View.VISIBLE);
        } else {
            floatingText.setVisibility(View.INVISIBLE);
        }
    }
    private void closeFloatingView(int x, int y){
        if(y > screenHeight-200){
            stopSelf();
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) windowManager.removeView(floatingView);
    }
}
