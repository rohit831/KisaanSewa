package com.gw.kisansewa.soldProduct;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.gw.kisansewa.DBHandler;
import com.gw.kisansewa.R;
import com.gw.kisansewa.models.CropDetails;

import java.util.ArrayList;

public class Sold extends AppCompatActivity
{
    private RecyclerView recyclerView;
    final Context context=this;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private DBHandler dbHandler;
    ArrayList<CropDetails> cropDetails;
    ArrayList<String> buyerNames;
    String userMobileNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sold);

        recyclerView=(RecyclerView)findViewById(R.id.soldView);
        cropDetails=new ArrayList<CropDetails>();
        buyerNames=new ArrayList<String>();
        userMobileNo=getIntent().getStringExtra("mobileNo");
        dbHandler=new DBHandler(context,null,null,1);

        cropDetails=dbHandler.getCropSold(userMobileNo);
        buyerNames=dbHandler.getBuyerNames(cropDetails);

        adapter=new SoldRecyclerAdapter(cropDetails,buyerNames,context,userMobileNo);
        layoutManager=new LinearLayoutManager(context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

}
