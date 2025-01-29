package com.svtech.statussaver.myinterface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.core.app.NotificationManagerCompat;

import com.svtech.statussaver.helper.HelperFunction;
import com.svtech.statussaver.service.FloatingService;
import com.svtech.statussaver.settingpage.SettingsPage;


public class ButtonReceiver extends BroadcastReceiver {
    public static final String OPEN_STATUS_ACTION = "com.svtech.statussaver.Open_Status_Saver";

    public static final String CLOSE_NOTIFICATION_ACTION = "com.svtech.statussaver.Close_Notification";


    @Override
    public void onReceive(Context context, Intent intent) {
        if(OPEN_STATUS_ACTION.equals(intent.getAction())){
            if (Settings.canDrawOverlays(context)) {
                Intent floatIntent = new Intent(context, FloatingService.class);
                context.startService(floatIntent);
            } else {
                HelperFunction.print(context, "Please allow draw overlay in this app");
            }
            closeNotificationDrawer(context);
        }

        if(CLOSE_NOTIFICATION_ACTION.equals(intent.getAction())){
            int notification_id = intent.getIntExtra(SettingsPage.CHANNEL_ID, -1);
            if(notification_id != -1){
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.cancel(notification_id);
                HelperFunction.print(context, "Notification Closed");
            }

            closeNotificationDrawer(context);
        }
    }



    private void closeNotificationDrawer(Context context){
        Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(closeIntent);
    }
}
