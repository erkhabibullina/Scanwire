package com.example.android.scanwire;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class BarcodeFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public BarcodeFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior, List<Fragment> fragments) {
        super(fm, behavior);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "#" + (position + 1);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
