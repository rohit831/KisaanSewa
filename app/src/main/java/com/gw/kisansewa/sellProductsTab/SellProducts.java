package com.gw.kisansewa.sellProductsTab;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.gw.kisansewa.R;
import com.gw.kisansewa.api.SellProductAPI;
import com.gw.kisansewa.apiGenerator.ProductGenerator;
import com.gw.kisansewa.authentication.FarmerLogin;
import com.gw.kisansewa.models.CropDetails;

import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellProducts extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<CropDetails> cropDetails;
    private Button addProduct, addProductsNoCrops;
    private String mobileNo;
    private SharedPreferences sharedPreferences;
    boolean isAvailable = false;
    private LinearLayout progressBar, no_crops, no_internet, snackLayout;
    private ProgressDialog progressDialog;
    private TextView retry_btn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sell_products,container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize(view);
        retrieveSellerCrops(mobileNo);

        addProductsNoCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no_crops.setVisibility(View.GONE);
                addNewProduct();
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewProduct();
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        retrieveSellerCrops(mobileNo);
                    }
                },500);
            }
        });

        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no_crops.setVisibility(View.GONE);
                no_internet.setVisibility(View.GONE);
                retrieveSellerCrops(mobileNo);
            }
        });
    }

    //referencing all the elements
    private void initialize(View view) {
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.sellProductsRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        recyclerView=(RecyclerView)view.findViewById(R.id.sellProductsView);
        progressBar = (LinearLayout)view.findViewById(R.id.progress_sell_products);
        sharedPreferences = getActivity().getSharedPreferences(FarmerLogin.FarmerPreferences, Context.MODE_PRIVATE);
        mobileNo = sharedPreferences.getString(FarmerLogin.FMobileNo,"");
        addProductsNoCrops = (Button)view.findViewById(R.id.addProductNoCrops);
        no_crops = (LinearLayout)view.findViewById(R.id.no_crops_sell_products);
        no_internet = (LinearLayout)view.findViewById(R.id.no_internet_sell_products);
        addProduct=(Button)view.findViewById(R.id.addProduct);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        snackLayout = (LinearLayout)view.findViewById(R.id.snack_layout_sell_product);
        retry_btn = (TextView) view.findViewById(R.id.retry_sell_products);
    }

    //retrieve all the seller crops
    public void retrieveSellerCrops(final String mobileNo){
        SellProductAPI sellProductAPI = ProductGenerator.createService(SellProductAPI.class);
        Call<ArrayList<CropDetails>> getUserCropsCall = sellProductAPI.getUserCrops(mobileNo);
        showProgressBar(true);
        getUserCropsCall.enqueue(new Callback<ArrayList<CropDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<CropDetails>> call, Response<ArrayList<CropDetails>> response) {
                cropDetails = response.body();
                showProgressBar(false);
                if(response.body().size() == 0)
                    noCropsToSell();
                else {
                    recyclerAdapter = new SellProductsRecyclerAdapter(cropDetails, getContext(), mobileNo, snackLayout);
                    layoutManager = new GridLayoutManager(getContext(), 2);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(recyclerAdapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CropDetails>> call, Throwable t) {
                showProgressBar(false);
                noInternetConnectionFound();
            }
        });
    }

    //show dialog and add a new product
    private void addNewProduct() {
        LayoutInflater li=LayoutInflater.from(getContext());
        final View dialogView=li.inflate(R.layout.add_product,null);
        final AlertDialog.Builder customDialog= new AlertDialog.Builder(getContext());

        customDialog.setView(dialogView);
        final EditText productName,productPrice,productQuantity;
        productName=(EditText)dialogView.findViewById(R.id.productNameDialog);
        productPrice=(EditText)dialogView.findViewById(R.id.productPriceDialog);
        productQuantity=(EditText)dialogView.findViewById(R.id.productQuantityDialog);

        customDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if(cropDetails.size() == 0)
                    noCropsToSell();
            }
        });

        customDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(productName.getText().toString().equals("")
                        || productPrice.getText().toString().equals("")
                        || productQuantity.getText().toString().trim().equals("")) {
                        noCropsCheck();
                        showSnack("Enter All Details");
                }
                else if (Long.parseLong(productQuantity.getText().toString())==0) {
                    noCropsCheck();
                    showSnack("Quantity cannot be zero!");
                }
                else
                {
                    final CropDetails crop = new CropDetails(productName.getText().toString(),
                            productQuantity.getText().toString(),
                            productPrice.getText().toString(),mobileNo);

                    if(!checkCropAvailability(mobileNo, productName.getText().toString())){
                        SellProductAPI sellProductAPI = ProductGenerator.createService(SellProductAPI.class);
                        Call<Void> addNewCropCall = sellProductAPI.addNewCrop(crop);
                        addNewCropCall.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.code()==201) {
                                    progressDialog.hide();
                                    retrieveSellerCrops(mobileNo);
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                progressDialog.hide();
                                    noCropsCheck();
                                    showSnack("Something went wrong! Please try again later!");
                            }
                        });
                    }
                }

            }
        });
        customDialog.create();
        customDialog.show();
    }

    //check if the name of crop is available for that seller
    public boolean checkCropAvailability(String mobileNo, String cropName){
        isAvailable =false;
        SellProductAPI sellProductAPI = ProductGenerator.createService(SellProductAPI.class);
        Call<Void> checkCropAvailabilityCall = sellProductAPI.checkCropAvailability(mobileNo, cropName);
        progressDialog.setMessage("Adding new crop .. ");
        progressDialog.show();
        checkCropAvailabilityCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 302){
                    progressDialog.hide();
                    isAvailable = false;
                        noCropsCheck();
                        showSnack("Crop already exists, try some another name!");
                }
                else if(response.code() == 200){
                    isAvailable = true;
                }
                else if(response.code() == 502){
                    isAvailable = false;
                    progressDialog.hide();
                        noCropsCheck();
                        showSnack("Oops! Something went wrong!");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.hide();
                noCropsCheck();
                showSnack("Unable to process your request at the moment!");
                isAvailable = false;
            }
        });
        return isAvailable;
    }

    //check if no crop present
    private void noCropsCheck() {
        if(cropDetails.size() == 0){
            noCropsToSell();
        }
    }

    //view if seller has no crops to sell
    void noCropsToSell() {
        no_internet.setVisibility(View.GONE);
        no_crops.setVisibility(View.VISIBLE);
    }

    //view if no internet connection is found
    void noInternetConnectionFound() {
        no_internet.setVisibility(View.VISIBLE);
        no_crops.setVisibility(View.GONE);
    }

    //show/hide progress loader
    private void showProgressBar(boolean show){
        if(show){
            progressBar.setVisibility(View.VISIBLE);
        }
        else
            progressBar.setVisibility(View.GONE);
    }

    //show snack
    void showSnack(String message) {
        TSnackbar snack = TSnackbar.make(snackLayout,message,TSnackbar.LENGTH_SHORT );
        View snackView = snack.getView();
        snackView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        TextView textView = (TextView)snackView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.white));
        snack.show();
    }
}

