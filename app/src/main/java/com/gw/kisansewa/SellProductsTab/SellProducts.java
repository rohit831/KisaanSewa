package com.gw.kisansewa.SellProductsTab;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gw.kisansewa.R;
import com.gw.kisansewa.api.SellProductAPI;
import com.gw.kisansewa.apiGenerator.ProductGenerator;
import com.gw.kisansewa.authentication.FarmerLogin;
import com.gw.kisansewa.models.CropDetails;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellProducts extends Fragment {

    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<CropDetails> cropDetails;
    private Button addProduct;
    String mobileNo;
    SharedPreferences sharedPreferences;
    boolean isAvailable = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sell_products,container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.sellProductsRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        recyclerView=(RecyclerView)view.findViewById(R.id.sellProductsView);
        sharedPreferences = getActivity().getSharedPreferences(FarmerLogin.FarmerPreferences, Context.MODE_PRIVATE);
        mobileNo = sharedPreferences.getString(FarmerLogin.FMobileNo,"");
        retrieveSellerCrops(mobileNo);


        addProduct=(Button)view.findViewById(R.id.addProduct);

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li=LayoutInflater.from(getContext());
                final View dialogView=li.inflate(R.layout.add_product,null);
                final AlertDialog.Builder customDialog= new AlertDialog.Builder(getContext());

                customDialog.setView(dialogView);
                final TextView title;
                final EditText productName,productPrice,productQuantity;
                productName=(EditText)dialogView.findViewById(R.id.productNameDialog);
                productPrice=(EditText)dialogView.findViewById(R.id.productPriceDialog);
                productQuantity=(EditText)dialogView.findViewById(R.id.productQuantityDialog);

                customDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                customDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(productName.getText().toString().equals("")
                                || productPrice.getText().toString().equals("")
                                || productQuantity.getText().toString().equals(""))
                        {
                            Toast.makeText(getContext(),"Enter All Details!!",Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            final CropDetails crop = new CropDetails(productName.getText().toString(),
                                    productQuantity.getText().toString(),
                                    productPrice.getText().toString(),mobileNo);

                            if(!checkCropAvailability(mobileNo, productName.getText().toString())){
                                if(Long.parseLong(productQuantity.getText().toString())==0)
                                    Toast.makeText(getContext(),"Quantity cannot be zero",Toast.LENGTH_SHORT).show();
                                else{
                                    SellProductAPI sellProductAPI = ProductGenerator.createService(SellProductAPI.class);
                                    Call<Void> addNewCropCall = sellProductAPI.addNewCrop(crop);
                                    addNewCropCall.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            if(response.code()==201) {
                                                Toast.makeText(getContext(), "Crop successfully added!", Toast.LENGTH_SHORT).show();
                                                retrieveSellerCrops(mobileNo);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(getContext(), "Unable to connect to server! :(", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                            else{

                            }
                        }

                    }
                });
                customDialog.create();
                customDialog.show();
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        retrieveSellerCrops(mobileNo);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },500);
            }
        });


    }

    public void retrieveSellerCrops(final String mobileNo){
        SellProductAPI sellProductAPI = ProductGenerator.createService(SellProductAPI.class);
        Call<ArrayList<CropDetails>> getUserCropsCall = sellProductAPI.getUserCrops(mobileNo);
        getUserCropsCall.enqueue(new Callback<ArrayList<CropDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<CropDetails>> call, Response<ArrayList<CropDetails>> response) {
                recyclerAdapter=new SellProductsRecyclerAdapter(response.body(),getContext(),mobileNo);
                layoutManager=new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<CropDetails>> call, Throwable t) {
                Toast.makeText(getContext(), "Can't get user crops at the moment!Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean checkCropAvailability(String mobileNo, String cropName){
        SellProductAPI sellProductAPI = ProductGenerator.createService(SellProductAPI.class);
        Call<Void> checkCropAvailabilityCall = sellProductAPI.checkCropAvailability(mobileNo, cropName);

        checkCropAvailabilityCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 302){
                    isAvailable = false;
                    Toast.makeText(getContext(), "Crop already exists, try some another name.", Toast.LENGTH_SHORT).show();
                }
                else if(response.code() == 200){
                    isAvailable = true;
                }
                else if(response.code() == 502){
                    Toast.makeText(getContext(), "Unable to process your request at the moment.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Unable to process your request at the moment.", Toast.LENGTH_SHORT).show();
                isAvailable = false;
            }
        });
        return isAvailable;
    }
}
