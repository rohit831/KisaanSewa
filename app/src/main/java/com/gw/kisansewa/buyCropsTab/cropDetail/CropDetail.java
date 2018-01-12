package com.gw.kisansewa.buyCropsTab.cropDetail;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gw.kisansewa.R;
import com.gw.kisansewa.api.BuyProductAPI;
import com.gw.kisansewa.apiGenerator.ProductGenerator;
import com.gw.kisansewa.authentication.FarmerLogin;
import com.gw.kisansewa.buyProductsTab.BuyProductsRecyclerAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CropDetail extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BuyProductAPI buyProductAPI;
    private RecyclerView.Adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.LayoutManager layoutManager;
    private String userMobileNo;
    private LinearLayout progressBar;
    private LinearLayout noInternet;
    private TextView retry_btn;
    private ImageView image;
    private String cropName, imageLink;
    private final Context context = this;
    private ArrayList<CropDetailModel> crops;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_detail);

        initialize();

//      enable back button and changing the name of action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(cropName);

        getCropsAvailable();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getCropsAvailable();
                        adapter=new CropDetailAdapter(context,cropName,crops);
                        recyclerView.setAdapter(adapter);
                    }
                },500);
            }
        });

        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInternet.setVisibility(View.GONE);
                getCropsAvailable();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void initialize(){
        cropName = getIntent().getStringExtra("cropName");
        imageLink = getIntent().getStringExtra("imageLink");

        image = findViewById(R.id.crop_detail_image);
        Picasso.with(context)
                .load(imageLink)
                .into(image);

        noInternet = findViewById(R.id.no_internet_crop_detail);
        retry_btn = findViewById(R.id.retry_crop_detail);
        swipeRefreshLayout = findViewById(R.id.cropDetailRefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        progressBar = findViewById(R.id.progress_crop_detail);
        crops = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences(FarmerLogin.FarmerPreferences, MODE_PRIVATE);
        userMobileNo = sharedPreferences.getString(FarmerLogin.FMobileNo, "");
        recyclerView = findViewById(R.id.cropDetailRecyclerView);
    }


    private void getCropsAvailable(){
        buyProductAPI = ProductGenerator.createService(BuyProductAPI.class);
        Call<ArrayList<CropDetailModel>> getCropSellersCall = buyProductAPI.getCropSellers(cropName);
        showProgressBar(true);
        getCropSellersCall.enqueue(new Callback<ArrayList<CropDetailModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CropDetailModel>> call, Response<ArrayList<CropDetailModel>> response) {
                if(response.code() == 200){
                    showProgressBar(false);
                    crops = response.body();
                    adapter = new CropDetailAdapter(context, cropName, crops);
                    layoutManager = new LinearLayoutManager(context);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setHasFixedSize(true);
                }
                else{
                    showProgressBar(false);
                    noInternetConnectionFound();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CropDetailModel>> call, Throwable t) {
                showProgressBar(false);
                noInternetConnectionFound();
            }
        });
    }

    private void noInternetConnectionFound() {
        noInternet.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showProgressBar(boolean show){
        if(show){
            progressBar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
        }
        else{
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}
