package com.gw.kisansewa.BuyProductsTab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<CropDetails> cropDetails;
    boolean flag = false;
    String userMobileNo=new String();
    ArrayList<String> sellerCities;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.buy_products, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.buyProductsRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        cropDetails=new ArrayList<CropDetails>();
        sellerCities=new ArrayList<String>();
        sharedPreferences = getActivity().getSharedPreferences(FarmerLogin.FarmerPreferences,Context.MODE_PRIVATE);
        userMobileNo=sharedPreferences.getString(FarmerLogin.FMobileNo,"");
        recyclerView=(RecyclerView)view.findViewById(R.id.buyProductsView);

        getCropsAvailable();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getCropsAvailable();

                        adapter=new BuyProductsRecyclerAdapter(cropDetails,sellerCities,getContext(),userMobileNo);
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
                    getSellerCities();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CropDetails>> call, Throwable t) {
                Toast.makeText(getContext(), "Unable to connect to server at the moment!", Toast.LENGTH_SHORT).show();
            }
        });
        return flag;
    }
    void getSellerCities()
    {
        buyProductAPI = ProductGenerator.createService(BuyProductAPI.class);
        Call<ArrayList<String>> getSellerNamesCall = buyProductAPI.getSellerNames(cropDetails);
        getSellerNamesCall.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if(response.code() == 200) {
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
                Toast.makeText(getContext(), "Unable to connect to server at the moment!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
