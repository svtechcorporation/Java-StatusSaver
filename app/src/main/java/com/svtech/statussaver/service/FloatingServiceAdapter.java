package com.svtech.statussaver.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.svtech.statussaver.R;
import com.svtech.statussaver.ViewActivity;
import com.svtech.statussaver.savepage.SavedRecyclerAdapter;

import java.io.File;
import java.util.ArrayList;


public class FloatingServiceAdapter extends RecyclerView.Adapter<FloatingServiceAdapter.MyViewHolder> {

    private Context context;
    private File[] data;

    public FloatingServiceAdapter(Context context, File[] data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.imagepage_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        File currentFile = data[position];
        if (currentFile.getName().endsWith(".mp4")) {
            holder.videoIcon.setVisibility(View.VISIBLE);
        } else {
            holder.videoIcon.setVisibility(View.INVISIBLE);
        }
        Glide.with(context).load(new File(currentFile.getAbsolutePath()))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view, currentFile);
            }
        });
    }

    private void showPopup(View view, File currentFile) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenu().add("Save");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle().equals("Save")) {
                    Intent serviceIntent = new Intent(context, SaveFileService.class);
                    serviceIntent.putExtra(SaveFileService.saveFilePath, currentFile.getAbsolutePath());
                    serviceIntent.putExtra(SaveFileService.fileTypeSent, SaveFileService.singleFile);
                    context.startService(serviceIntent);
                }
                return true;
            }
        });
        popupMenu.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFiles(File[] files) {
        data = files;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return data.length;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image, videoIcon, selectedIcon;
        public TextView imageText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imagePage_vp_text);
            imageText = itemView.findViewById(R.id.imageListText);
            videoIcon = itemView.findViewById(R.id.videoIcon);
            selectedIcon = itemView.findViewById(R.id.selectedIcon);
        }
    }


}
