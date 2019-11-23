package com.example.chatplease;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class Tsbsaccessor extends FragmentPagerAdapter {

    public Tsbsaccessor(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
//        switch(position) {
//            case 0:
                groups g = new groups();
                return g;
//            case 1:
//                return null;
//            case 2:
//                return null;
//
//            default:
//                return null;
//        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch(position) {
            case 0:
                return "groups";
            case 1:
                return null;
            case 2:
                return null;
            default:
                return null;
        }

    }
}
