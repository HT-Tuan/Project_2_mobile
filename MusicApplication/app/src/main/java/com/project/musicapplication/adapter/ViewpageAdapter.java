package com.project.musicapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.project.musicapplication.fragment.Home_fragment_tuan;
import com.project.musicapplication.fragment.personalFragment;
import com.project.musicapplication.fragment.playlistFragment;

public class ViewpageAdapter extends FragmentStatePagerAdapter {


    public ViewpageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Home_fragment_tuan();
            case 1:
                return new playlistFragment();
            case 2:
                return new personalFragment();
            default:
                return new Home_fragment_tuan();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
