package com.svtech.statussaver.settingpage;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.ToggleButton;

import com.svtech.statussaver.R;
import com.svtech.statussaver.helper.HelperFunction;
import com.svtech.statussaver.myinterface.ButtonReceiver;
import com.svtech.statussaver.service.FloatingService;

public class SettingsPage extends Fragment {

    private Context context;
    private ImageButton floatinSaver, showNotification;
    private View parentView;
    public static final String CHANNEL_ID = "satus_saver_channer_1";
    public static final int NOTIFICATION_ID = 1;
    private View notificationOpen;

    public SettingsPage(Context context) {
        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void setAttribute() {
        floatinSaver = parentView.findViewById(R.id.floatingSaver);
        showNotification = parentView.findViewById(R.id.showNotification);
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_settings_page, container, false);
        setAttribute();
        createNotificationChannel();
        loadApp();
        return parentView;
    }

    private void loadApp() {
        floatinSaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Settings.canDrawOverlays(context)) {
                    Intent intent = new Intent(context, FloatingService.class);
                    context.startService(intent);
                } else {
                    HelperFunction.print(context, "Please allow draw overlay in this app");
                    checkFloatPermission();
                }
            }
        });

        showNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNotification();
            }
        });

    }

    private void showNotification() {
        RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        //       open status action
        Intent btnOpen = new Intent(context, ButtonReceiver.class);
        btnOpen.setAction(ButtonReceiver.OPEN_STATUS_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, btnOpen,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notificationLayout.setOnClickPendingIntent(R.id.notificationOpen, pendingIntent);

        //        Close action
        Intent closeNotifIntent = new Intent(context, ButtonReceiver.class);
        closeNotifIntent.putExtra(CHANNEL_ID, NOTIFICATION_ID);
        closeNotifIntent.setAction(ButtonReceiver.CLOSE_NOTIFICATION_ACTION);

        notificationLayout.setOnClickPendingIntent(R.id.close_notification_btn,
                PendingIntent.getBroadcast(context,  0, closeNotifIntent,
                PendingIntent.FLAG_UPDATE_CURRENT));

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.status_saver_icon_foreground) // Replace with your own icon
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        HelperFunction.print(context, "Notification Shown");
    }


    private void checkFloatPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.getPackageName()));
                startActivityForResult(intent, 100);
            }
        }
    }

}