package com.gw.kisansewa.purchaseRequests;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gw.kisansewa.R;
import com.gw.kisansewa.api.RequestAPI;
import com.gw.kisansewa.apiGenerator.RequestGenerator;
import com.gw.kisansewa.authentication.FarmerLogin;
import com.gw.kisansewa.models.RequestDetails;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseRequest extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<RequestDetails> requestDetails;
    private SharedPreferences sharedPreferences;
    SwipeRefreshLayout swipeRefreshLayout;
    final Context context = this;
    String userMobileNo;
    private LinearLayout noInternet;
    private TextView retry_btn, no_requests;
    private LinearLayout progressBar;
    private CoordinatorLayout snackLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase_requests);

//      enable back button and changing the name of action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.purchase_req_nav);

        initialize();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPurchaseRequests();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },500);
            }
        });

        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPurchaseRequests();
            }
        });

        getPurchaseRequests();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize()
    {
        requestDetails = new ArrayList<>();
        sharedPreferences = getSharedPreferences(FarmerLogin.FarmerPreferences, Context.MODE_PRIVATE);
        userMobileNo = sharedPreferences.getString(FarmerLogin.FMobileNo,"");
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_purchase_requests);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_purchase_requests);
        noInternet = (LinearLayout)findViewById(R.id.no_internet_purchase_requests);
        retry_btn = (TextView)findViewById(R.id.retry_purchase_requests);
        no_requests = (TextView)findViewById(R.id.no_requests_purchase_requests);
        progressBar = (LinearLayout)findViewById(R.id.progress_purchase_requests);
        snackLayout = (CoordinatorLayout)findViewById(R.id.snack_layout_purchase_requests);
    }

    private void getPurchaseRequests()
    {
        RequestAPI requestAPI = RequestGenerator.createService(RequestAPI.class);
        Call<ArrayList<RequestDetails>> purchaseRequestsCall = requestAPI.getPurchaseRequests(userMobileNo);
        showProgressBar(true);
        purchaseRequestsCall.enqueue(new Callback<ArrayList<RequestDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<RequestDetails>> call, Response<ArrayList<RequestDetails>> response) {
                if(response.code() == 200){
                    showProgressBar(false);
                    requestDetails = response.body();
                    if(requestDetails.size() == 0) {
                        noRequestsCurrently();
                    }
                    else{
                        no_requests.setVisibility(View.GONE);
                        noInternet.setVisibility(View.GONE);
                        adapter = new PurchaseRequestAdapter(requestDetails, context);
                        layoutManager = new LinearLayoutManager(context);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setHasFixedSize(true);
                    }
                }
                else {
                    showProgressBar(false);
                    noInternetConnectionFound();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<RequestDetails>> call, Throwable t) {
                showProgressBar(false);
                noInternetConnectionFound();
            }
        });
    }

    //in case of no internet
    void noInternetConnectionFound(){
        noInternet.setVisibility(View.VISIBLE);
        no_requests.setVisibility(View.GONE);
    }

    //if user has no requests
    void noRequestsCurrently() {
        no_requests.setVisibility(View.VISIBLE);
        noInternet.setVisibility(View.GONE);
    }

    //to display a snackbar
    void showSnack(String message) {
        Snackbar.make(snackLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    //show/hide loader
    void showProgressBar(boolean flag){
        if(flag)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

}
