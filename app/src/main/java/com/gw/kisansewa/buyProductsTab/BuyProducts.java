package com.gw.kisansewa.buyProductsTab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gw.kisansewa.R;
import com.gw.kisansewa.api.BuyProductAPI;
import com.gw.kisansewa.apiGenerator.ProductGenerator;
import com.gw.kisansewa.authentication.FarmerLogin;
import com.gw.kisansewa.models.CropDetails;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyProducts extends Fragment
{
    private RecyclerView recyclerView;
    private BuyProductAPI buyProductAPI;
    private RecyclerView.Adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<CropDetails> cropDetails;
    boolean flag = false;
    private String userMobileNo=new String();
    private ArrayList<String> sellerCities;
    private SharedPreferences sharedPreferences;
    private LinearLayout progressBar;
    private LinearLayout noInternet;
    private TextView retry_btn, no_crops;
    private CoordinatorLayout snackLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.buy_products, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize(view);
        getCropsAvailable();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getCropsAvailable();
                        adapter=new BuyProductsRecyclerAdapter(cropDetails,sellerCities,getContext(),userMobileNo);
                        recyclerView.setAdapter(adapter);
                    }
                },500);
            }
        });

        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInternet.setVisibility(View.GONE);
                no_crops.setVisibility(View.GONE);
                getCropsAvailable();
            }
        });
    }

    private void initialize(View view)
    {
        noInternet = (LinearLayout)view.findViewById(R.id.no_internet_buy_products);
        retry_btn = (TextView)view.findViewById(R.id.retry_buy_products);
        no_crops = (TextView)view.findViewById(R.id.no_requests_buy_products);
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.buyProductsRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        progressBar = (LinearLayout) view.findViewById(R.id.progress_buy_products);
        cropDetails=new ArrayList<>();
        sellerCities=new ArrayList<>();
        sharedPreferences = getActivity().getSharedPreferences(FarmerLogin.FarmerPreferences,Context.MODE_PRIVATE);
        userMobileNo=sharedPreferences.getString(FarmerLogin.FMobileNo,"");
        recyclerView=(RecyclerView)view.findViewById(R.id.buyProductsView);
        snackLayout = (CoordinatorLayout)view.findViewById(R.id.snack_layout_buy_products);
    }

    void getCropsAvailable()
    {
        flag = false;
        buyProductAPI = ProductGenerator.createService(BuyProductAPI.class);
        Call<ArrayList<CropDetails>> gettingCropsCall = buyProductAPI.getCropsAvailable(userMobileNo);
        showProgressBar(true);
        gettingCropsCall.enqueue(new Callback<ArrayList<CropDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<CropDetails>> call, Response<ArrayList<CropDetails>> response) {
                if(response.code() == 200){
                    if(response.body().size() == 0)
                    {
                        showProgressBar(false);
                        noCrops();
                    }
                    else {
                        cropDetails = response.body();
                        getSellerCities();
                    }
                }
                else{
                    showProgressBar(false);
                    noInternetConnectionFound();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CropDetails>> call, Throwable t) {
                showProgressBar(false);
                noInternetConnectionFound();
            }
        });
    }
    void getSellerCities()
    {
        buyProductAPI = ProductGenerator.createService(BuyProductAPI.class);
        Call<ArrayList<String>> getSellerNamesCall = buyProductAPI.getSellerNames(cropDetails);
        getSellerNamesCall.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if(response.code() == 200) {
                    showProgressBar(false);
                    sellerCities = response.body();
                    adapter = new BuyProductsRecyclerAdapter(cropDetails,sellerCities, getContext(), userMobileNo);
                    layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(layoutManager);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                showProgressBar(false);
                noInternetConnectionFound();
            }
        });
    }

    private void noCrops(){
        no_crops.setVisibility(View.VISIBLE);
        noInternet.setVisibility(View.GONE);
    }

    private void noInternetConnectionFound() {
        noInternet.setVisibility(View.VISIBLE);
        no_crops.setVisibility(View.GONE);
    }

    private void showProgressBar(boolean show){
        if(show){
            progressBar.setVisibility(View.VISIBLE);
        }
        else
            progressBar.setVisibility(View.GONE);
    }
}
