package com.rovas.forgram.fogram.base;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;


import com.rovas.forgram.fogram.interfaces.Searchable;

import java.util.ArrayList;
/**
 * Created by Mohamed El Sayed
 */
public class TabsPagerAdapter extends SmartFragmentStatePagerAdapter {

    private final ArrayList<TabInfo> mTabs = new ArrayList<>();
    private final FragmentActivity mActivity;

    public TabsPagerAdapter(FragmentActivity activity, FragmentManager fragmentManager) {
        super(fragmentManager);
        mActivity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        return mTabs.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    public void addTab(Class<? extends Searchable> clss, @Nullable Bundle args, String title) {
        mTabs.add(new TabInfo(title, Fragment.instantiate(mActivity, clss.getName(), args)));
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).getTitle();
    }

    static final class TabInfo {
        String title;
        Fragment fragment;

        public TabInfo(String title, Fragment fragment) {
            this.title = title;
            this.fragment = fragment;
        }

        public String getTitle() {
            return title;
        }

        public Fragment getFragment() {
            return fragment;
        }
    }
}
