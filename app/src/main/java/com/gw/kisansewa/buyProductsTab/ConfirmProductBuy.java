package com.gw.kisansewa.buyProductsTab;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gw.kisansewa.Homescreen.HomeScreen;
import com.gw.kisansewa.R;
import com.gw.kisansewa.api.AuthenticationAPI;
import com.gw.kisansewa.api.BuyProductAPI;
import com.gw.kisansewa.apiGenerator.AuthenticationGenerator;
import com.gw.kisansewa.apiGenerator.ProductGenerator;
import com.gw.kisansewa.models.CropDetails;
import com.gw.kisansewa.models.FarmerDetails;
import com.gw.kisansewa.models.RequestDetails;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConfirmProductBuy extends AppCompatActivity {

    private TextView productName,productPrice,productQuantity,sellerName,sellerMobileNo;
    private TextView sellerAddress, message, call, findDistance, requestProduct, dist_from_current, dist_from_addr;
    private BuyProductAPI buyProductAPI;
    private String sellerNo,buyProductName,userMobileNo;
    private FarmerDetails farmerDetails;
    final Context context=this;
    private CropDetails cropDetails;
    private LinearLayout progressBar, no_internet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_product_buy);
//
////      enable back button and changing the name of action bar
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(R.string.tab_confirm_product_buy);

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
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if(id == android.R.id.home){
//            this.finish();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public void findDistanceOnClick(View v) {
        if (!checkLocationPermissions()) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Toast.makeText(ConfirmProductBuy.this, "You need to grant us permission!", Toast.LENGTH_SHORT).show();
        } else {
            LayoutInflater li = LayoutInflater.from(context);
            final View dialogView = li.inflate(R.layout.distance_dialog, null);
            final AlertDialog.Builder customDialog = new AlertDialog.Builder(context);
            final AlertDialog dialog = customDialog.create();
            dialog.setView(dialogView);
            dialog.show();

            TextView current_dist, addr_dist;
            addr_dist = (TextView) dialogView.findViewById(R.id.dist_address_btn);
            current_dist = (TextView) dialogView.findViewById(R.id.dist_current_btn);

            current_dist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findDistanceFromCurrentLocation();
                    dialog.cancel();
                }
            });

            addr_dist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!(dist_from_addr.getVisibility() == View.VISIBLE))
                        findDistanceFromAddress();
                    dialog.cancel();
                }
            });
        }
    }

//    Reference all the variables
    public void initialize() {
        productName=(TextView)findViewById(R.id.finalProductName);
        productPrice=(TextView)findViewById(R.id.finalProductPrice);
        productQuantity=(TextView)findViewById(R.id.finalProductQuantity);
        sellerName=(TextView)findViewById(R.id.finalSellerName);
        sellerMobileNo=(TextView)findViewById(R.id.finalMobileNo);
        sellerAddress = (TextView)findViewById(R.id.address_confirm_buy_product);
        message = (TextView)findViewById(R.id.message_confirm_buy);
        call = (TextView) findViewById(R.id.call_confirm_buy);
        dist_from_current = (TextView) findViewById(R.id.dist_from_current_view);
        dist_from_addr = (TextView)findViewById(R.id.dist_from_addr_view);
        findDistance = (TextView) findViewById(R.id.find_distance_confirm_buy);
        requestProduct = (TextView) findViewById(R.id.request_product_confirm_buy);
        farmerDetails=new FarmerDetails();
        cropDetails=new CropDetails();
        progressBar = (LinearLayout)findViewById(R.id.progress_confirm_product_buy);
        no_internet = (LinearLayout)findViewById(R.id.no_internet_confirm_product_buy);

        sellerNo=getIntent().getStringExtra("sellerMobileNo");
        userMobileNo=getIntent().getStringExtra("userMobileNo");
        buyProductName=getIntent().getStringExtra("productName");
    }

//    Get the details of the crop selected
     void getCropDetails() {
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

//    Get the details of the respective seller
    void getFarmerDetails() {
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

//    Set the view elements value to the response generated
    public void setValues() {
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

//    show custom dialog for confirming request of crop
    public void showCustomDialog() {
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

//    requesting a crop
    void requestProduct() {
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

//    finding distance from current location
    private void findDistanceFromCurrentLocation()
    {
        Toast.makeText(ConfirmProductBuy.this, "Find distance from current location clicked!", Toast.LENGTH_SHORT).show();

    }

//    finding distance from address
    private void findDistanceFromAddress()
    {
        //Getting address of buyer
        AuthenticationAPI authAPI = AuthenticationGenerator.createService(AuthenticationAPI.class);
        Call<FarmerDetails> getAddressBuyerCall = authAPI.getFarmerDetail(userMobileNo);
        getAddressBuyerCall.enqueue(new Callback<FarmerDetails>() {
            @Override
            public void onResponse(Call<FarmerDetails> call, Response<FarmerDetails> response) {
                FarmerDetails farmerDetails = response.body();
                String area = farmerDetails.getArea();
                String city = farmerDetails.getCity();
                String state = farmerDetails.getState();
                String buyerAddress = area.concat(", ").concat(city).concat(", ").concat(state);

                buyProductAPI = ProductGenerator.createService(BuyProductAPI.class);
                Call<String> getDistanceCall = buyProductAPI.getDistance(buyerAddress,
                        sellerAddress.getText().toString());
                getDistanceCall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.code() == 204){
                            dist_from_addr.setVisibility(View.VISIBLE);
                            dist_from_addr.setText(R.string.no_route_addr);
                        }
                        else if(response.code() == 200){
                            dist_from_addr.setVisibility(View.VISIBLE);
                            dist_from_addr.setText(dist_from_addr.getText().toString().concat(response.body()));
                        }
                        else{
                            Toast.makeText(ConfirmProductBuy.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(ConfirmProductBuy.this, "Can't connect to server at the moment", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<FarmerDetails> call, Throwable t) {
                Toast.makeText(ConfirmProductBuy.this, "Can't connect to server at the moment", Toast.LENGTH_SHORT).show();
            }
        });
    }

//
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//    }

//  check for network permissions
    private boolean checkLocationPermissions() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

//   no internet connection found
    private void noInternetConnectionFound()
    {
        no_internet.setVisibility(View.VISIBLE);
    }
}
