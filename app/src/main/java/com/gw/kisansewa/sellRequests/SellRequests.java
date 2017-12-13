package com.gw.kisansewa.sellRequests;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gw.kisansewa.Homescreen.HomeScreen;
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

    ImageButton back_btn;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sell_requests);

       // reference all the elements and get the shared preferences
        initialize();
        getSellRequests();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getSellRequests();
                       swipeRefreshLayout.setRefreshing(false);
                    }
                },500);
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, HomeScreen.class));
                finish();
            }
        });


        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSellRequests();
            }
        });
    }


    // reference all the elements and get the shared preferences
    private void initialize(){
        back_btn = (ImageButton)findViewById(R.id.sell_requests_back_btn);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_sell_requests);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_sell_requests);
        sharedPreferences = getSharedPreferences(FarmerLogin.FarmerPreferences,Context.MODE_PRIVATE);
        userMobileNo = sharedPreferences.getString(FarmerLogin.FMobileNo,"");
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        noInternet = (LinearLayout)findViewById(R.id.no_internet_sell_requests);
        retry_btn = (TextView)findViewById(R.id.retry_sell_requests);
        no_requests = (TextView)findViewById(R.id.no_requests_sell_requests);
    }

    //get all the sell requests of a seller
    private void getSellRequests(){
        requestAPI = RequestGenerator.createService(RequestAPI.class);
        Call<ArrayList<RequestDetails>> getSellRequestsCall = requestAPI.getSellRequests(userMobileNo);
        getSellRequestsCall.enqueue(new Callback<ArrayList<RequestDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<RequestDetails>> call, Response<ArrayList<RequestDetails>> response) {
                if(response.code() == 200){
                    if(response.body().size() == 0){
                        noInternet.setVisibility(View.GONE);
                        no_requests.setVisibility(View.VISIBLE);
                    }
                    else{
                        noInternet.setVisibility(View.GONE);
                        no_requests.setVisibility(View.GONE);
                        requestDetails = response.body();
                        ArrayList<String> buyerMobileNos = new ArrayList<String>();
                        for(RequestDetails request: requestDetails){
                            buyerMobileNos.add(request.getBuyerMobileNo());
                        }
                        getBuyerNames(buyerMobileNos);
                    }
                }
                if(response.code() == 502){
                    Toast.makeText(SellRequests.this,
                            "Can't connect to server at the moment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<RequestDetails>> call, Throwable t) {
                no_requests.setVisibility(View.GONE);
                noInternet.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getBuyerNames(ArrayList<String> buyerMobileNos){
        requestAPI = RequestGenerator.createService(RequestAPI.class);
        Call<ArrayList<String>> getBuyerNamesCall = requestAPI.getBuyerNames(buyerMobileNos);

        getBuyerNamesCall.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if(response.code() == 200){
                    buyerNames = response.body();
                    adapter = new SellRequestAdapter(requestDetails, buyerNames, context);
                    layoutManager = new LinearLayoutManager(context);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(layoutManager);
                }
                else{
                    Toast.makeText(SellRequests.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                no_requests.setVisibility(View.GONE);
                noInternet.setVisibility(View.VISIBLE);
            }
        });
    }
}
