package com.gw.kisansewa.orders;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gw.kisansewa.orders.purchased.PurchasedFragment;
import com.gw.kisansewa.orders.sold.SoldFragment;

public class OrderAdapter extends FragmentStatePagerAdapter {

    int numOfTabs;

    public OrderAdapter(FragmentManager fm, int numOfTabs){
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new PurchasedFragment();
            case 1:
                return new SoldFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
