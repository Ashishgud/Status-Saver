package com.developer.ashishtech.statussaver.Adapters;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.developer.ashishtech.statussaver.Gallery_Fragement;
import com.developer.ashishtech.statussaver.Status_Fragement;

@SuppressWarnings("deprecation")
public class Fragement_Adapter extends FragmentPagerAdapter {
    public Fragement_Adapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new Status_Fragement();
            case 1:
                return new Gallery_Fragement();
            default:
                return new Status_Fragement();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title=null;
        if(position==0)
        {
            title="Status";
        }
        else if(position==1)
        {
            title="Gallery";
        }
        return title;
    }
}