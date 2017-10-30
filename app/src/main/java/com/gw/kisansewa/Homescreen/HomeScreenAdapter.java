package com.gw.kisansewa.Homescreen;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gw.kisansewa.BuyProductsTab.BuyProducts;
import com.gw.kisansewa.SellProductsTab.SellProducts;

public class HomeScreenAdapter extends FragmentStatePagerAdapter{

    int numOfTabs;

    public HomeScreenAdapter(FragmentManager fm, int numOfTabs)
    {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new BuyProducts();
            case 1:
                return new SellProducts();
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return numOfTabs;
    }
}
