package com.svtech.statussaver;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.svtech.statussaver.helper.HelperFunction;

import java.io.File;


public class ViewActivity extends AppCompatActivity {

    public static final String FILEPATH = "RecievedFilePath";
    private View backBtn;
    private ImageView viewImage;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        setAttributes();
        setDefaults();

    }

    private void setVideo(boolean res){
        if(res){
            videoView.setVisibility(View.VISIBLE);
            viewImage.setVisibility(View.INVISIBLE);
        } else {
            videoView.setVisibility(View.INVISIBLE);
            viewImage.setVisibility(View.VISIBLE);
        }
    }

    private void setDefaults(){
        Intent intent = getIntent();
        String path = intent.getStringExtra(FILEPATH);

        if(path != null && !path.endsWith(".nomedia") && (path.endsWith(".jpg") || (path.endsWith(".png")))){
            setVideo(false);
           Glide.with(this).load(new File(path))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(viewImage);
        } else if(path.endsWith(".mp4")){

            setVideo(true);
            Uri videoUri = Uri.parse(path);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);

            videoView.setMediaController(mediaController);
            videoView.setVideoURI(videoUri);
            videoView.start();
        }



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setAttributes(){
        backBtn = findViewById(R.id.backBtn);
        viewImage = findViewById(R.id.viewImage);
        videoView = findViewById(R.id.videoView);
    }


}