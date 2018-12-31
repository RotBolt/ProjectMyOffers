package com.intellyticshub.projectmyoffers.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.intellyticshub.projectmyoffers.ui.fragments.ActiveOfferFragment;
import com.intellyticshub.projectmyoffers.ui.fragments.ExpiredOfferFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int numTabs;

    public PagerAdapter(@NonNull FragmentManager fm, int numTabs) {
        super(fm);
        this.numTabs = numTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0)
            return new ActiveOfferFragment();
        else
            return new ExpiredOfferFragment();
    }

    @Override
    public int getCount() {
        return numTabs;
    }
}
