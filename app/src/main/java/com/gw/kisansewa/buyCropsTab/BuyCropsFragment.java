package com.gw.kisansewa.buyCropsTab;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gw.kisansewa.R;
import com.gw.kisansewa.api.BuyProductAPI;
import com.gw.kisansewa.apiGenerator.ProductGenerator;
import com.gw.kisansewa.authentication.FarmerLogin;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyCropsFragment extends Fragment {

    private RecyclerView recyclerView;
    private BuyProductAPI buyProductAPI;
    private RecyclerView.Adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<CropsModel> crops;
    private String userMobileNo=new String();
    private SharedPreferences sharedPreferences;
    private LinearLayout progressBar;
    private LinearLayout noInternet;
    private TextView retry_btn;
    private EditText search;
    private Context context = getContext();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.buy_crops_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize(view);
        getAllCrops();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getAllCrops();
                        adapter=new BuyCropsFragmentAdapter(getContext(), crops);
                        recyclerView.setAdapter(adapter);
                    }
                },500);
            }
        });

        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInternet.setVisibility(View.GONE);
                getAllCrops();
            }
        });
    }

//    initialize all the views
    private void initialize(View view){
        noInternet = view.findViewById(R.id.no_internet_buy_crops_tab);
        retry_btn = view.findViewById(R.id.retry_buy_crops_tab);
        swipeRefreshLayout = view.findViewById(R.id.buyCropsTabRefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        progressBar = view.findViewById(R.id.progress_buy_crops_tab);
        crops = new ArrayList<>();
        sharedPreferences = getActivity().getSharedPreferences(FarmerLogin.FarmerPreferences, Context.MODE_PRIVATE);
        userMobileNo = sharedPreferences.getString(FarmerLogin.FMobileNo, "");
        recyclerView = view.findViewById(R.id.buyCropsTabRecyclerView);
        search = view.findViewById(R.id.buy_crops_tab_search);
    }


//    get all the crops
    private void getAllCrops(){
        buyProductAPI = ProductGenerator.createService(BuyProductAPI.class);
        showProgressBar(true);
        Call<ArrayList<CropsModel>> getCropsCall = buyProductAPI.getAllCrops();
        getCropsCall.enqueue(new Callback<ArrayList<CropsModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CropsModel>> call, Response<ArrayList<CropsModel>> response) {
                if(response.code() == 200)
                {
                    noInternet.setVisibility(View.GONE);
                    showProgressBar(false);
                    crops = response.body();
                    adapter = new BuyCropsFragmentAdapter(getContext(), crops);
                    layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(layoutManager);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CropsModel>> call, Throwable t) {
                showProgressBar(false);
                noInternetConnectionFound();
            }
        });
    }

    private void noInternetConnectionFound() {
        noInternet.setVisibility(View.VISIBLE);
    }

    private void showProgressBar(boolean show){
        if(show){
            progressBar.setVisibility(View.VISIBLE);
        }
        else
            progressBar.setVisibility(View.GONE);
    }
}
