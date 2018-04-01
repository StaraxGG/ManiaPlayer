package com.example.nicolas.maniaplayer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Nicolas on 30.03.2018.
 * TODO Add Fragments and return them
 */

public class FragmentMusicAdapter extends FragmentPagerAdapter {
    public FragmentMusicAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        TitelFragment newFragment = new TitelFragment();
        return newFragment;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            default: return  "Interpret";
        }
    }
}
