package com.svtech.statussaver.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.FileUtils;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.svtech.statussaver.helper.HelperFunction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.TimerTask;

public class SaveFileService extends Service {

    public static final String defaultSaveDirectory = "/WhatsappStatus";
    public static final String saveFilePath = "SaveServiceFileName";
    public static final String singleFile = "SingleFileSent";
    public static final String multipleFile = "MultipleFilesSent";
    public static final String fileTypeSent = "FileTypeSent";
    public static final String defaultWhatsappPath = "/whatsapp/media/.statuses";

    private String saveDirectory;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (checkSaveDirectory()) {
            String fileType = intent.getStringExtra(fileTypeSent);

            if(fileType != null && fileType.equals(singleFile)){
                String filePath = intent.getStringExtra(saveFilePath);
                copyFile(filePath);
            }
            if(fileType != null && fileType.equals(multipleFile)){
                String[] filePath = intent.getStringArrayExtra(saveFilePath);
                if(filePath != null && filePath.length>0){
                    for(String path : filePath){
                        copyFile(path);
                    }
                }
            }

        }
        return START_STICKY;
    }

    private void copyFile(String filePath) {
        File file = new File(filePath);
        File saveFile = new File(saveDirectory + "/" + file.getName());
        if (saveFile.exists()) {
            HelperFunction.print(this, "Already Saved");
        } else {
            try {
                copySingleFile(file, saveFile);
                String fileExtension = file.getName().endsWith(".jpg") ? "Image" : "Video";
                HelperFunction.print(this, fileExtension+ " Saved");
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                sendBroadcast(intent);
                scanMedia(saveFile.getAbsolutePath());
                scanMedia(file.getAbsolutePath());
            } catch (IOException e) {
                HelperFunction.print(this, "Could not copy error = "+String.valueOf(e.getMessage()));
            }
        }
    }

    private void scanMedia(String path) {
        MyMediaScannerConnectionClient client = new MyMediaScannerConnectionClient(path);
        MediaScannerConnection msc = new MediaScannerConnection(this, client);
        client.setMediaScannerConnection(msc);
        msc.connect();
    }

    private void copySingleFile(File sourceFile, File destFile)
            throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;

        try {
            sourceChannel = new FileInputStream(sourceFile).getChannel();
            destChannel = new FileOutputStream(destFile).getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
        } finally {
            if (sourceChannel != null) {
                sourceChannel.close();
            }
            if (destChannel != null) {
                destChannel.close();
            }
        }
    }


    private boolean checkSaveDirectory() {
        File drS = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + defaultSaveDirectory);
        if (drS.exists()) {
            saveDirectory = drS.getAbsolutePath();
            return true;
        } else {
            try {
                drS.mkdir();
                saveDirectory = drS.getAbsolutePath();
                return true;
            } catch (Exception e) {
                HelperFunction.print(this, "Directory not Created with error = " + String.valueOf(e.getMessage()));
                return false;
            }
        }
    }


    private static class MyMediaScannerConnectionClient implements
            MediaScannerConnection.MediaScannerConnectionClient {


        private MediaScannerConnection msc;
        private String path;

        public MyMediaScannerConnectionClient(String path) {
            this.path = path;
        }

        public void setMediaScannerConnection(MediaScannerConnection msc) {
            this.msc = msc;
        }

        @Override
        public void onMediaScannerConnected() {
            if (msc != null) {
                msc.scanFile(path, null);
            }
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            if (msc != null) {
                msc.disconnect();
            }
        }

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        HelperFunction.print(this, "Service Stopped");
    }
}
