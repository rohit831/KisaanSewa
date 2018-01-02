package com.gw.kisansewa.orders.purchased;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gw.kisansewa.R;
import com.gw.kisansewa.api.OrderAPI;
import com.gw.kisansewa.apiGenerator.OrderGenerator;
import com.gw.kisansewa.authentication.FarmerLogin;
import com.gw.kisansewa.models.FarmerDetails;
import com.gw.kisansewa.models.Orders;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchasedFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAPI orderAPI;
    private SwipeRefreshLayout swipeToRefresh;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<FarmerDetails> farmerDetails;
    private ArrayList<Orders> orders;
    private String userMobileNo;
    private SharedPreferences sharedPreferences;
    private LinearLayout progressBar, noInternet;
    private TextView noOrders;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.orders_purchased,container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize(view);
        getOrders();

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeToRefresh.setRefreshing(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getOrders();
                    }
                },500);
            }
        });
    }

    private void initialize(View view)
    {
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_purchased_orders);
        swipeToRefresh = (SwipeRefreshLayout)view.findViewById(R.id.orderPurchasedRefresh);
        swipeToRefresh.setColorSchemeResources(R.color.colorPrimary);
        progressBar = (LinearLayout) view.findViewById(R.id.progress_orders_purchased);
        farmerDetails = new ArrayList<>();
        orders = new ArrayList<>();
        sharedPreferences = getActivity().getSharedPreferences(FarmerLogin.FarmerPreferences, Context.MODE_PRIVATE);
        userMobileNo = sharedPreferences.getString(FarmerLogin.FMobileNo, "");
        noOrders = (TextView)view.findViewById(R.id.no_orders_purchased);
        noInternet = (LinearLayout)view.findViewById(R.id.no_internet_purchased);
    }

    private void getOrders()
    {
        showProgressBar(true);
        orderAPI = OrderGenerator.createService(OrderAPI.class);
        Call<ArrayList<Orders>> getPurchasedOrdersCall = orderAPI.getPurchasedOrders(userMobileNo);
        getPurchasedOrdersCall.enqueue(new Callback<ArrayList<Orders>>() {
            @Override
            public void onResponse(Call<ArrayList<Orders>> call, Response<ArrayList<Orders>> response) {
                if(response.code() == 200)
                {
                    //checking for no orders yet
                    if(response.body().size() == 0) {
                        showProgressBar(false);
                        noOrders.setVisibility(View.VISIBLE);
                        noInternet.setVisibility(View.GONE);
                    }
                    else{
                        orders = response.body();
                        getSellerDetails();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Orders>> call, Throwable t) {
                showProgressBar(false);
                noInternetConnectionFound();
            }
        });
    }

    private void getSellerDetails()
    {
        orderAPI = OrderGenerator.createService(OrderAPI.class);
        Call<ArrayList<FarmerDetails>> getSellerDetailsCall = orderAPI.getSellerDetails(userMobileNo);
        getSellerDetailsCall.enqueue(new Callback<ArrayList<FarmerDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<FarmerDetails>> call, Response<ArrayList<FarmerDetails>> response) {
                showProgressBar(false);
                if(response.code() == 200){
                    farmerDetails = response.body();
                    adapter = new PurchasedFragmentAdapter(getContext(), orders, farmerDetails);
                    layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(layoutManager);
                }
                else
                    Toast.makeText(getContext(),R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ArrayList<FarmerDetails>> call, Throwable t) {
                showProgressBar(false);
                noInternetConnectionFound();
            }
        });
    }

//    show no internet connection
    private void noInternetConnectionFound() {
        noInternet.setVisibility(View.VISIBLE);
        noOrders.setVisibility(View.GONE);
    }

//    hide/show progress bar
    private void showProgressBar(boolean show){
        if(show){
            progressBar.setVisibility(View.VISIBLE);
        }
        else
            progressBar.setVisibility(View.GONE);
    }
}
