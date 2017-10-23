package com.gw.kisansewa.buyProduct;

import
        android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gw.kisansewa.DBHandler;
import com.gw.kisansewa.R;
import com.gw.kisansewa.api.BuyProductAPI;
import com.gw.kisansewa.apiGenerator.ProductGenerator;
import com.gw.kisansewa.models.CropDetails;
import com.gw.kisansewa.models.FarmerDetails;
import com.gw.kisansewa.models.PurchaseDetails;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConfirmProductBuy extends AppCompatActivity {

    TextView productName,productPrice,productQuantity;
    TextView sellerName,sellerMobileNo,sellerLocality,sellerCity,sellerState;
    EditText quantityRequired;
    Button confirmBuyProduct;
    private BuyProductAPI buyProductAPI;
    private DBHandler dbHandler;
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
//        getFarmerDetails();
        confirmBuyProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });
    }

    public void initialize()
    {
        dbHandler=new DBHandler(this,null,null,1);
        productName=(TextView)findViewById(R.id.finalProductName);
        productPrice=(TextView)findViewById(R.id.finalProductPrice);
        productQuantity=(TextView)findViewById(R.id.finalProductQuantity);
        sellerName=(TextView)findViewById(R.id.finalSellerName);
        sellerMobileNo=(TextView)findViewById(R.id.finalMobileNo);
        sellerLocality=(TextView)findViewById(R.id.finalLocality);
        sellerCity=(TextView)findViewById(R.id.finalCity);
        sellerState=(TextView)findViewById(R.id.finalState);
        quantityRequired=(EditText) findViewById(R.id.finalRequiredQuantity);
        confirmBuyProduct=(Button) findViewById(R.id.finalBuyProduct);
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
        sellerLocality.setText(farmerDetails.getArea());
        sellerCity.setText(farmerDetails.getCity());
        sellerState.setText(farmerDetails.getState());
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
                if(quantityRequired.getText().toString().equals(""))
                {
                    Toast.makeText(context,"Enter Required Quantity first!!",Toast.LENGTH_SHORT).show();
                }
                else {
                    if(Long.parseLong(quantityRequired.getText().toString())>Long.parseLong(productQuantity.getText().toString())) {
                        Toast.makeText(context, "Quantity out of range!!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        if ((Long.parseLong(quantityRequired.getText().toString()))==0)
                            Toast.makeText(context,"Quantity cannot be zero",Toast.LENGTH_SHORT).show();
                    else
                    {
                        purchaseProduct(quantityRequired.getText().toString(),productPrice.getText().toString());

//                        if (dbHandler.purchaseProduct(userMobileNo, sellerNo, buyProductName, quantityRequired.getText().toString(),
//                                productPrice.getText().toString())) {
//
//                            if (dbHandler.changeQuantity(sellerNo, buyProductName, quantityRequired.getText().toString())) {
//                                Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show();
//
//                                Intent intent=new Intent(context,BuyProducts.class);
//                                intent.putExtra("mobileNo",userMobileNo);
//                                startActivity(intent);
//                                finish();
                                quantityRequired.setText("");
//                            }
//
//                        } else
//                            Toast.makeText(context, "Product unable to add", Toast.LENGTH_SHORT).show();
                    }
                }
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
    void purchaseProduct(String quantityRequired, String productPrice)
    {
        buyProductAPI = ProductGenerator.createService(BuyProductAPI.class);
        PurchaseDetails transaction = new PurchaseDetails(sellerNo,userMobileNo, buyProductName,quantityRequired,productPrice);
        Call<Void> purchaseProductCall = buyProductAPI.purchaseProduct(transaction);
        purchaseProductCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(context,BuyProducts.class);
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
