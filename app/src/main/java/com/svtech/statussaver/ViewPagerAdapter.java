package com.svtech.statussaver;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.svtech.statussaver.imagepage.ImagePage;
import com.svtech.statussaver.savepage.SavedPage;
import com.svtech.statussaver.settingpage.SettingsPage;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private int itemCount;
    private Context context;

    private Fragment imageFragment, savedFragment, settingsFragment;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, int itemCount) {
        super(fragmentActivity);
        this.itemCount = itemCount;
        this.context = fragmentActivity;
        imageFragment = new ImagePage(context);
        savedFragment = new SavedPage(context);
        settingsFragment = new SettingsPage(context);
    }

    public Fragment getFragment() {
        return imageFragment;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return imageFragment;
            case 1:
                return savedFragment;
            case 2:
                return settingsFragment;
        }
        return imageFragment;
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }
}
