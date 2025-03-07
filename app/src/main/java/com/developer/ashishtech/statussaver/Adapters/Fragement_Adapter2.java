package com.developer.ashishtech.statussaver.Adapters;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.developer.ashishtech.statussaver.Gallery_Fragement;
import com.developer.ashishtech.statussaver.Status_Fragement;
import com.developer.ashishtech.statussaver.wbStatus;
import com.developer.ashishtech.statussaver.wbgallery;

@SuppressWarnings("deprecation")
public class Fragement_Adapter2 extends FragmentPagerAdapter {
    public Fragement_Adapter2(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new wbStatus();
            case 1:
                return new wbgallery();
            default:
                return new wbStatus();
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