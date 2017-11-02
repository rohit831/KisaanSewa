package com.gw.kisansewa.BuyProductsTab;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gw.kisansewa.DBHandler;
import com.gw.kisansewa.Homescreen.HomeScreen;
import com.gw.kisansewa.R;
import com.gw.kisansewa.api.BuyProductAPI;
import com.gw.kisansewa.apiGenerator.ProductGenerator;
import com.gw.kisansewa.models.CropDetails;
import com.gw.kisansewa.models.FarmerDetails;
import com.gw.kisansewa.models.RequestDetails;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConfirmProductBuy extends AppCompatActivity {

    TextView productName,productPrice,productQuantity;
    TextView sellerName,sellerMobileNo;
    TextView sellerAddress, message, call, findDistance, requestProduct;
    private BuyProductAPI buyProductAPI;
    String sellerNo= new String();
    String userMobileNo=new String();
    String buyProductName=new String();
    FarmerDetails farmerDetails;
    final Context context=this;
    CropDetails cropDetails;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_product_buy);

        initialize();
        getCropDetails();

        requestProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", sellerNo, null)));
            }
        });

        call.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", sellerNo, null)));
            }
        });
    }

    public void findDistanceOnClick(View v){
        Toast.makeText(ConfirmProductBuy.this, "Find Distance clicked", Toast.LENGTH_SHORT).show();
    }

    public void initialize()
    {
        productName=(TextView)findViewById(R.id.finalProductName);
        productPrice=(TextView)findViewById(R.id.finalProductPrice);
        productQuantity=(TextView)findViewById(R.id.finalProductQuantity);
        sellerName=(TextView)findViewById(R.id.finalSellerName);
        sellerMobileNo=(TextView)findViewById(R.id.finalMobileNo);
        sellerAddress = (TextView)findViewById(R.id.address_confirm_buy_product);
        message = (TextView)findViewById(R.id.message_confirm_buy);
        call = (TextView) findViewById(R.id.call_confirm_buy);
        findDistance = (TextView) findViewById(R.id.find_distance_confirm_buy);
        requestProduct = (TextView) findViewById(R.id.request_product_confirm_buy);
        farmerDetails=new FarmerDetails();
        cropDetails=new CropDetails();

        sellerNo=getIntent().getStringExtra("sellerMobileNo");
        userMobileNo=getIntent().getStringExtra("userMobileNo");
        buyProductName=getIntent().getStringExtra("productName");
    }

     void getCropDetails()
    {
        buyProductAPI = ProductGenerator.createService(BuyProductAPI.class);
        final Call<CropDetails> cropDetailCall = buyProductAPI.getCropDetails(sellerNo, buyProductName);
        cropDetailCall.enqueue(new Callback<CropDetails>() {
            @Override
            public void onResponse(Call<CropDetails> call, Response<CropDetails> response) {
                if(response.code()==200){
                    cropDetails = response.body();
                    getFarmerDetails();
                }
            }

            @Override
            public void onFailure(Call<CropDetails> call, Throwable t) {
                Toast.makeText(context, "Unable to connect to server at the moment!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void getFarmerDetails()
    {
        buyProductAPI = ProductGenerator.createService(BuyProductAPI.class);
        final Call<FarmerDetails> farmerDetailsCall = buyProductAPI.getFarmerDetails(sellerNo);
        farmerDetailsCall.enqueue(new Callback<FarmerDetails>() {
            @Override
            public void onResponse(Call<FarmerDetails> call, Response<FarmerDetails> response) {
                if(response.code()==200) {
                    farmerDetails = response.body();
                    setValues();
                }
            }

            @Override
            public void onFailure(Call<FarmerDetails> call, Throwable t) {
                Toast.makeText(context, "Unable to connect to server at the moment!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setValues()
    {
        productName.setText(cropDetails.getCropName());
        productPrice.setText(cropDetails.getCropPrice());
        productQuantity.setText(cropDetails.getCropQuantity());

        sellerName.setText(farmerDetails.getName());
        sellerMobileNo.setText(farmerDetails.getMobileNo());
        String area = farmerDetails.getArea();
        String city = farmerDetails.getCity();
        String state = farmerDetails.getState();
        sellerAddress.setText(area.concat(", ").concat(city).concat(", ")
        .concat(state));
    }

    public void showCustomDialog()
    {
        LayoutInflater li=LayoutInflater.from(context);
        final View dialogView= li.inflate(R.layout.dialog_confirm_product,null);
        final AlertDialog.Builder customDialog= new AlertDialog.Builder(context);
        customDialog.setView(dialogView);

        customDialog.setCancelable(false);

        customDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                        requestProduct();
                    }

        });

        customDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        customDialog.create();
        customDialog.show();
    }
    void requestProduct()
    {
        buyProductAPI = ProductGenerator.createService(BuyProductAPI.class);
        RequestDetails transaction = new RequestDetails(sellerNo,userMobileNo, buyProductName
                                            ,productPrice.getText().toString(), productQuantity.getText().toString());
        Call<Void> requestProductCall = buyProductAPI.requestProduct(transaction);
        requestProductCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    Toast.makeText(context, "Crop added to Purchase Requests", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(context,HomeScreen.class);
                    intent.putExtra("mobileNo",userMobileNo);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Unable to connect to the server at the moment", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
