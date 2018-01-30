package com.rdypda.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DengJf on 2018/1/30.
 */

public class FragmentViewPagerAdapter extends FragmentPagerAdapter{
    private List<Fragment> mFragmentList;
    private List<String>mTitle;


    public FragmentViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList=new ArrayList<>();
        mTitle=new ArrayList<>();
    }

    public void addFragment(Fragment fragment,String title){
        mFragmentList.add(fragment);
        mTitle.add(title);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = mFragmentList.get(position);

        return fragment;
    }

    @Override
    public int getCount() {

        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle.get(position);
    }
}
