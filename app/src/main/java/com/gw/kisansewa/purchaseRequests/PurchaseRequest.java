package com.gw.kisansewa.purchaseRequests;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private RequestAPI requestAPI;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<RequestDetails> requestDetails;
    private SharedPreferences sharedPreferences;
    SwipeRefreshLayout swipeRefreshLayout;
    final Context context = this;
    String userMobileNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase_requests);

        requestDetails = new ArrayList<RequestDetails>();
        sharedPreferences = getSharedPreferences(FarmerLogin.FarmerPreferences, Context.MODE_PRIVATE);
        userMobileNo = sharedPreferences.getString(FarmerLogin.FMobileNo,"");
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_purchase_requests);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_purchase_requests);

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

        getPurchaseRequests();
    }

    private void getPurchaseRequests()
    {
        RequestAPI requestAPI = RequestGenerator.createService(RequestAPI.class);
        Call<ArrayList<RequestDetails>> purchaseRequestsCall = requestAPI.getPurchaseRequests(userMobileNo);
        purchaseRequestsCall.enqueue(new Callback<ArrayList<RequestDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<RequestDetails>> call, Response<ArrayList<RequestDetails>> response) {
                if(response.code() == 200){
                    requestDetails = response.body();
                    adapter = new PurchaseRequestAdapter(requestDetails, context);
                    layoutManager = new LinearLayoutManager(context);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setHasFixedSize(true);
                }
                else
                    Toast.makeText(context, "Oops! Something went wrong!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ArrayList<RequestDetails>> call, Throwable t) {
                Toast.makeText(context, "Unable to connect to the  server at the moment!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
