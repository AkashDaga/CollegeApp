package com.example.akash.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Akash on 01-04-2016.
 */
public class SwipeTabAdapter extends FragmentPagerAdapter {
    List<Fragment> listFragments;

    public SwipeTabAdapter(FragmentManager fm,List<Fragment> listFragments) {
        super(fm);
        this.listFragments=listFragments;
    }

    @Override
    public Fragment getItem(int position) { return listFragments.get(position); }

    @Override
    public int getCount() {
        return listFragments.size();
    }
}
