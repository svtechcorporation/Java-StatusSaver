package com.svtech.statussaver.imagepage;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.svtech.statussaver.R;
import com.svtech.statussaver.ViewActivity;
import com.svtech.statussaver.helper.HelperFunction;
import com.svtech.statussaver.myinterface.HeaderSelectUpdate;
import com.svtech.statussaver.service.SaveFileService;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.MyViewHolder> {


    private final Context context;
    private File[] data;
    private List<String> selectedFiles;

    private HeaderSelectUpdate headUpdate;

    public ImageRecyclerAdapter(Context context, File[] data) {
        this.context = context;
        this.headUpdate = (HeaderSelectUpdate) context;
        this.data = data;
        this.selectedFiles = new ArrayList<String>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.imagepage_list, parent, false);
        return new MyViewHolder(view);
    }

    private void toggleSelectedIcons(MyViewHolder holder, int visibility) {
        holder.selectedIcon.setVisibility(visibility);
        holder.imageText.setVisibility(visibility);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFiles(File[] files){
        data = files;
        selectedFiles.clear();
        headUpdate.updateData(selectedFiles);
        notifyDataSetChanged();
    }
    

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        File currentFile = data[position];
        if (selectedFiles.contains(currentFile.getAbsolutePath())) {
            toggleSelectedIcons(holder, View.VISIBLE);
        } else {
            toggleSelectedIcons(holder, View.INVISIBLE);
        }

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
                if (selectedFiles.isEmpty()) {
                    showPopup(view, currentFile);
                } else {
                    if (selectedFiles.contains(currentFile.getAbsolutePath())) {
                        removeSelected(currentFile.getAbsolutePath());
                    } else {
                        addSelected(currentFile.getAbsolutePath());
                    }
                }
            }
        });

        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (selectedFiles.contains(currentFile.getAbsolutePath())) {
                    removeSelected(currentFile.getAbsolutePath());
                } else {
                    addSelected(currentFile.getAbsolutePath());
                }
                return true;
            }
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void addSelected(String filename) {
        selectedFiles.add(filename);
        notifyDataSetChanged();
        headUpdate.updateData(selectedFiles);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void removeSelected(String filename) {
        selectedFiles.remove(filename);
        notifyDataSetChanged();
        headUpdate.updateData(selectedFiles);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearSelectedFile(){
        selectedFiles.clear();
        headUpdate.updateData(selectedFiles);
        notifyDataSetChanged();
    }

    private void showPopup(View view, File currentFile) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenu().add("Save");
        popupMenu.getMenu().add("View");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle().equals("Save")) {
                    Intent serviceIntent = new Intent(context, SaveFileService.class);
                    serviceIntent.putExtra(SaveFileService.saveFilePath, currentFile.getAbsolutePath());
                    serviceIntent.putExtra(SaveFileService.fileTypeSent, SaveFileService.singleFile);
                    context.startService(serviceIntent);
                } else if (menuItem.getTitle().equals("View")) {
                    viewImage(currentFile);
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
