package com.gw.kisansewa.sellRequests;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gw.kisansewa.R;
import com.gw.kisansewa.api.RequestAPI;
import com.gw.kisansewa.apiGenerator.RequestGenerator;
import com.gw.kisansewa.authentication.FarmerLogin;
import com.gw.kisansewa.models.RequestDetails;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellRequests extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestAPI requestAPI;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final Context context = this;
    private String userMobileNo;
    private ArrayList<RequestDetails> requestDetails;
    private ArrayList<String> buyerNames;
    private LinearLayout noInternet;
    private TextView retry_btn, no_requests;
    private LinearLayout progressBar;
    private CoordinatorLayout snackLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sell_requests);

//      enable back button and changing the name of action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.tab_sell_req);


        // reference all the elements and get the shared preferences
        initialize();
        getSellRequests();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getSellRequests();
                    }
                },500);
            }
        });

        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSellRequests();
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

    // reference all the elements and get the shared preferences
    private void initialize(){
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_sell_requests);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_sell_requests);
        sharedPreferences = getSharedPreferences(FarmerLogin.FarmerPreferences,Context.MODE_PRIVATE);
        userMobileNo = sharedPreferences.getString(FarmerLogin.FMobileNo,"");
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        noInternet = (LinearLayout)findViewById(R.id.no_internet_sell_requests);
        retry_btn = (TextView)findViewById(R.id.retry_sell_requests);
        no_requests = (TextView)findViewById(R.id.no_requests_sell_requests);
        progressBar = (LinearLayout)findViewById(R.id.progress_sell_requests);
        snackLayout = (CoordinatorLayout)findViewById(R.id.snack_layout_sell_requests);
    }

    //get all the sell requests of a seller
    private void getSellRequests(){
        requestAPI = RequestGenerator.createService(RequestAPI.class);
        Call<ArrayList<RequestDetails>> getSellRequestsCall = requestAPI.getSellRequests(userMobileNo);
        showProgressBar(true);
        getSellRequestsCall.enqueue(new Callback<ArrayList<RequestDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<RequestDetails>> call, Response<ArrayList<RequestDetails>> response) {
                if(response.code() == 200){
                    if(response.body().size() == 0){
                        showProgressBar(false);
                        noRequestsCurrently();
                    }
                    else{
                        noInternet.setVisibility(View.GONE);
                        no_requests.setVisibility(View.GONE);
                        requestDetails = response.body();
                        getBuyerNames();
                    }
                }
                if(response.code() == 502){
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

    private void getBuyerNames(){
        requestAPI = RequestGenerator.createService(RequestAPI.class);
        Call<ArrayList<String>> getBuyerNamesCall = requestAPI.getBuyerNames(userMobileNo);
        getBuyerNamesCall.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                showProgressBar(false);
                if(response.code() == 200){
                    buyerNames = response.body();
                    adapter = new SellRequestAdapter(requestDetails, buyerNames, context);
                    layoutManager = new LinearLayoutManager(context);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(layoutManager);
                }
                else
                    noInternetConnectionFound();
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                showProgressBar(false);
                noInternetConnectionFound();
            }
        });
    }

    void noInternetConnectionFound(){
        noInternet.setVisibility(View.VISIBLE);
        no_requests.setVisibility(View.GONE);
    }

    void noRequestsCurrently() {
        no_requests.setVisibility(View.VISIBLE);
        noInternet.setVisibility(View.GONE);
    }

    void showSnack(String message) {
        Snackbar.make(snackLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    void showProgressBar(boolean flag){
        if(flag)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    void hideKeyboard()
    {
        View view = this.getCurrentFocus();
        if(view!=null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
