package com.svtech.statussaver.savepage;

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
import com.svtech.statussaver.helper.HelperFunction;
import com.svtech.statussaver.myinterface.HeaderSelectUpdate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SavedRecyclerAdapter extends RecyclerView.Adapter<SavedRecyclerAdapter.MyViewHolder> {


    private final Context context;
    private File[] data;


    public SavedRecyclerAdapter(Context context, File[] data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.imagepage_list, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFiles(File[] files){
        data = files;
        notifyDataSetChanged();
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
                viewImage(currentFile);
            }
        });
        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPopup(view, currentFile);
                return true;
            }
        });
    }


    private void showPopup(View view, File currentFile) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenu().add("Delete");
        popupMenu.getMenu().add("Share");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle().equals("Delete")) {
                    HelperFunction.print(context, "Delete file");
                } else if (menuItem.getTitle().equals("Share")) {
                    HelperFunction.print(context, "Share file");
                }
                return true;
            }
        });
        popupMenu.show();
    }

    public void viewImage(File file){
        Intent intent = new Intent(context, ViewActivity.class);
        intent.putExtra(ViewActivity.FILEPATH, file.getAbsolutePath());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {
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
