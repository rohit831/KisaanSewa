package com.gw.kisansewa.Homescreen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.gw.kisansewa.R;
import com.gw.kisansewa.authentication.EditProfile;
import com.gw.kisansewa.authentication.FarmerLogin;
import com.gw.kisansewa.buyProductsTab.BuyProducts;
import com.gw.kisansewa.marketTab.Market;
import com.gw.kisansewa.orders.Order;
import com.gw.kisansewa.purchaseRequests.PurchaseRequest;
import com.gw.kisansewa.sellProductsTab.SellProducts;
import com.gw.kisansewa.sellRequests.SellRequests;

public class Homescreen extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private MenuItem prevMenuItem;
    private BuyProducts buyProducts;
    private SellProducts sellProducts;
    private Market market;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        viewPager = findViewById(R.id.homescreen_content);

        bottomNavigationView =  findViewById(R.id.bottom_navigation);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());
        bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_nav_buy:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.bottom_nav_sell:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.bottom_nav_market:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                if(prevMenuItem!= null){
                    prevMenuItem.setChecked(false);
                }else{
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        setupViewPager();
    }

//    Inflate menu for three dots
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

//   Give onClick transitions for dropdown menu
    @Override
    public boolean onOptionsItemSelected(MenuItem it) {

        switch (it.getItemId()){
            case R.id.market_status:
                Toast.makeText(Homescreen.this, "Clicked Market Status", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.purchase_requests:
//                Toast.makeText(Homescreen.this, "Clicked Purchase Requests", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, PurchaseRequest.class));
                return true;

            case R.id.sell_requests:
                startActivity(new Intent(this, SellRequests.class));
                return true;

            case R.id.orders:
                startActivity(new Intent(this, Order.class));
                return true;

            case R.id.my_profile:
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
                return super.onOptionsItemSelected(it);
        }
    }

//  setting up view pager
    private void setupViewPager(){
        HomescreenAdapter adapter = new HomescreenAdapter(getSupportFragmentManager());
        buyProducts = new BuyProducts();
        sellProducts = new SellProducts();
        market = new Market();
        adapter.addFragment(buyProducts);
        adapter.addFragment(sellProducts);
        adapter.addFragment(market);
        viewPager.setAdapter(adapter);
    }
}

