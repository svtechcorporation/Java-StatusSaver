package com.svtech.statussaver;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.svtech.statussaver.helper.HelperFunction;
import com.svtech.statussaver.myinterface.ClearSelectedFiles;
import com.svtech.statussaver.myinterface.HeaderSelectUpdate;
import com.svtech.statussaver.service.SaveFileService;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements HeaderSelectUpdate {

    private ImageView imageNav, savedNav, settingsNav, selectedTextImage;
    private TextView imageText, settingsText, savedText, selectedTextCount, headerTitle, headerSubtitle;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private int countSelectedText;
    private View imagePageBtn, savedPageBtn, settingsPageBtn;
    private List<String> selectedFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkPermission()) {
            setAttribute();
            setNavClicks();
            setViewPager();
        } else {
            requestPermission();
        }
    }

    private void setViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(this, 3);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setUserInputEnabled(false);
    }

    private void setAttribute() {
        imageNav = findViewById(R.id.image_nav_icon);
        settingsNav = findViewById(R.id.settings_nav_icon);
        savedNav = findViewById(R.id.saved_nav_icon);
        imageText = findViewById(R.id.image_nav_text);
        settingsText = findViewById(R.id.settings_nav_text);
        savedText = findViewById(R.id.saved_nav_text);
        viewPager = findViewById(R.id.viewPager);
        selectedTextCount = findViewById(R.id.selectedTextCount);
        selectedTextImage = findViewById(R.id.selectedCountImage);
        headerTitle = findViewById(R.id.headerTitle);
        headerSubtitle = findViewById(R.id.headerSubtitle);
        imagePageBtn = findViewById(R.id.image_bg);
        settingsPageBtn = findViewById(R.id.settings_bg);
        savedPageBtn = findViewById(R.id.saved_bg);

        countSelectedText = 0;
        selectedFiles = new ArrayList<String>();
    }

    private void setNavClicks() {
        imagePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNavIcon(0);
            }
        });
        settingsPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNavIcon(2);
            }
        });
        savedPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNavIcon(1);
            }
        });

        selectedTextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.getMenu().add("Save All");
                popupMenu.getMenu().add("Unselect All");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getTitle().equals("Save All")) {
                            savedAllFiles();
                        } else if (menuItem.getTitle().equals("Unselect All")) {
                            try{
                                clearSelectedFiles();
                            } catch (Exception e){
                                HelperFunction.print(view.getContext(),
                                        "Error from Home Activity = " + String.valueOf(e.getMessage()));
                            }
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void clearSelectedFiles(){
        ClearSelectedFiles cls = (ClearSelectedFiles) viewPagerAdapter.getFragment();
        cls.clearSelectedFiles();
        selectedFiles.clear();
        updateData(selectedFiles);
    }

    private void savedAllFiles(){
        Intent intent = new Intent(this, SaveFileService.class);
        intent.putExtra(SaveFileService.fileTypeSent, SaveFileService.multipleFile);
        String[] stringArray = new String[selectedFiles.size()];
        stringArray = selectedFiles.toArray(stringArray);
        intent.putExtra(SaveFileService.saveFilePath, stringArray);
        startService(intent);
        clearSelectedFiles();
    }


    private void setNavIcon(int position) {
        int colorDark = ContextCompat.getColor(this, R.color.notActive);
        int colorLight = ContextCompat.getColor(this, R.color.active);

        imageText.setTextColor(colorDark);
        settingsText.setTextColor(colorDark);
        savedText.setTextColor(colorDark);

        imageNav.setImageResource(R.drawable.image_img);
        settingsNav.setImageResource(R.drawable.settings_image);
        savedNav.setImageResource(R.drawable.image_saved);

        switch (position) {
            case 0:
                imageText.setTextColor(colorLight);
                imageNav.setImageResource(R.drawable.image_img_active);
                setHeaderText("Statuses", "Long click for multiple selection");
                break;
            case 1:
                savedText.setTextColor(colorLight);
                savedNav.setImageResource(R.drawable.image_saved_active);
                setHeaderText("Saved", "Images/Videos Saved");
                break;
            case 2:
                settingsText.setTextColor(colorLight);
                settingsNav.setImageResource(R.drawable.image_settings_active);
                setHeaderText("Settings", "Personalize the app");
                break;
        }
        viewPager.setCurrentItem(position);
    }

    private void setHeaderText(String title, String subtitle){
        headerTitle.setText(title);
        headerSubtitle.setText(subtitle);
        if(title.equals("Statuses")){
            showSelectedCount();
        } else {
            selectedTextCount.setVisibility(View.INVISIBLE);
            selectedTextImage.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void updateData(List<String> selectedCount) {
        selectedFiles = selectedCount;
        countSelectedText = selectedCount.size();
        showSelectedCount();
        selectedTextCount.setText(String.valueOf(selectedCount.size() + " selected"));
    }

    private void showSelectedCount(){
        if (countSelectedText > 0) {
            selectedTextCount.setVisibility(View.VISIBLE);
            selectedTextImage.setVisibility(View.VISIBLE);
        } else {
            selectedTextCount.setVisibility(View.INVISIBLE);
            selectedTextImage.setVisibility(View.INVISIBLE);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            HelperFunction.print(MainActivity.this, "Please Allow Permission from Settings");
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
        }
    }


}