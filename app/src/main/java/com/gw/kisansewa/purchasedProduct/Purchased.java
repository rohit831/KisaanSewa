package com.gw.kisansewa.purchasedProduct;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.gw.kisansewa.DBHandler;
import com.gw.kisansewa.R;
import com.gw.kisansewa.models.CropDetails;
import com.gw.kisansewa.models.FarmerDetails;

import java.util.ArrayList;

public class Purchased extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DBHandler dbHandler;
    private RecyclerView.Adapter adapter;
    final Context context=this;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<FarmerDetails> farmerDetails;
    ArrayList<CropDetails> cropDetails;
    String userMobileNo=new String();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchased);

        farmerDetails=new ArrayList<FarmerDetails>();
        cropDetails=new ArrayList<CropDetails>();
        userMobileNo=getIntent().getStringExtra("mobileNo");
        dbHandler=new DBHandler(this,null,null,1);
        recyclerView=(RecyclerView)findViewById(R.id.purchasedView);

        cropDetails=dbHandler.getCropPurchased(userMobileNo);
        farmerDetails=dbHandler.getSellerDetails(cropDetails);

        adapter=new PurchasedRecyclerAdapter(cropDetails,farmerDetails,context,userMobileNo);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

    }
}
