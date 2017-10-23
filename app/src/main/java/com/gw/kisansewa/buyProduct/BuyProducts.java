package com.gw.kisansewa.buyProduct;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.gw.kisansewa.DBHandler;
import com.gw.kisansewa.R;
import com.gw.kisansewa.api.BuyProductAPI;
import com.gw.kisansewa.apiGenerator.ProductGenerator;
import com.gw.kisansewa.models.CropDetails;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyProducts extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BuyProductAPI buyProductAPI;
    private RecyclerView.Adapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    final Context mContext=this;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<CropDetails> cropDetails;
    boolean flag = false;
    String userMobileNo=new String();
    final Context context=this;
    ArrayList<String> sellerNames;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_products);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.buyProductsRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        cropDetails=new ArrayList<CropDetails>();
        sellerNames=new ArrayList<String>();
        userMobileNo=getIntent().getStringExtra("mobileNo");

        recyclerView=(RecyclerView)findViewById(R.id.buyProductsView);

        getCropsAvailable();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        dbHandler.removeUsedCrops();
                            getCropsAvailable();

                        adapter=new BuyProductsRecyclerAdapter(cropDetails,sellerNames,context,userMobileNo);
                        recyclerView.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },500);

            }
        });
    }
    boolean getCropsAvailable()
    {
        flag = false;
        buyProductAPI = ProductGenerator.createService(BuyProductAPI.class);
        Call<ArrayList<CropDetails>> gettingCropsCall = buyProductAPI.getCropsAvailable();
        gettingCropsCall.enqueue(new Callback<ArrayList<CropDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<CropDetails>> call, Response<ArrayList<CropDetails>> response) {
                if(response.code() == 200){
                    cropDetails = response.body();
                    flag = true;
                    getSellerNames();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CropDetails>> call, Throwable t) {
                Toast.makeText(BuyProducts.this, "Unable to connect to server at the moment!", Toast.LENGTH_SHORT).show();
            }
        });
        return flag;
    }

    void getSellerNames()
    {
        buyProductAPI = ProductGenerator.createService(BuyProductAPI.class);
        Call<ArrayList<String>> getSellerNamesCall = buyProductAPI.getSellerNames(cropDetails);
        getSellerNamesCall.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if(response.code() == 200) {
                    sellerNames = response.body();
                    adapter = new BuyProductsRecyclerAdapter(cropDetails, sellerNames, context, userMobileNo);
                    layoutManager = new LinearLayoutManager(context);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(layoutManager);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                Toast.makeText(BuyProducts.this, "Unable to connect to server at the moment!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
