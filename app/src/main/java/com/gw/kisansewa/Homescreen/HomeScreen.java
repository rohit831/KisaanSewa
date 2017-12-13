package com.gw.kisansewa.Homescreen;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.gw.kisansewa.R;
import com.gw.kisansewa.authentication.EditProfile;
import com.gw.kisansewa.authentication.FarmerLogin;
import com.gw.kisansewa.purchaseRequests.PurchaseRequest;
import com.gw.kisansewa.sellRequests.SellRequests;

public class HomeScreen extends AppCompatActivity {

    boolean doublePressedtoExitPressedOnce = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.hometab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Buy Crops"));
        tabLayout.addTab(tabLayout.newTab().setText("Sell Crops"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager =(ViewPager)findViewById(R.id.home_viewPager);
        final HomeScreenAdapter homeScreenAdapter = new HomeScreenAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(homeScreenAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.market_status:
                Toast.makeText(HomeScreen.this, "Clicked Market Status", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.purchase_requests:
//                Toast.makeText(HomeScreen.this, "Clicked Purchase Requests", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, PurchaseRequest.class));
                return true;

            case R.id.sell_requests:
                startActivity(new Intent(this, SellRequests.class));
//                Toast.makeText(HomeScreen.this, "Clicked Sell Requests", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.orders:
                Toast.makeText(HomeScreen.this, "Clicked Orders", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.my_profile:
//                Toast.makeText(HomeScreen.this, "Clicked My Profile", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, EditProfile.class));
                return true;

            case R.id.logout:
                SharedPreferences sharedPreferences = getSharedPreferences(FarmerLogin.FarmerPreferences, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                Intent intent = new Intent(this, FarmerLogin.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(doublePressedtoExitPressedOnce){
            finishAffinity();
        }
        this.doublePressedtoExitPressedOnce = true;
        Toast.makeText(HomeScreen.this, "Press again to exit!!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doublePressedtoExitPressedOnce = false;
            }
        },2000);
        super.onBackPressed();
    }
}
