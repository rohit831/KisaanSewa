package com.gw.kisansewa.buyProductsTab;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmProductBuy extends AppCompatActivity {

    private TextView productName,productPrice,productQuantity,sellerName,sellerMobileNo, retry_btn;
    private TextView sellerAddress, message, call, seeDirections, requestProduct, dist_from_current, dist_from_addr;
    private BuyProductAPI buyProductAPI;
    private String sellerNo,buyProductName,userMobileNo;
    private FarmerDetails farmerDetails;
    final Context context=this;
    private CropDetails cropDetails;
    private LinearLayout progressBar, no_internet, snackLayout;
    private ProgressDialog progressDialog;
    private CardView dist_card;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_product_buy);

//      enable back button and changing the name of action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.tab_confirm_product_buy);

        initialize();
        getCropDetails();

        requestProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });

        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no_internet.setVisibility(View.GONE);
                getCropDetails();
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


        seeDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeDirectionsClicked();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
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
        seeDirections = (TextView)findViewById(R.id.confirm_buy_get_direction);
        dist_from_current = (TextView) findViewById(R.id.dist_from_current_view);
        dist_from_addr = (TextView)findViewById(R.id.dist_from_addr_view);
        requestProduct = (TextView) findViewById(R.id.request_product_confirm_buy);
        farmerDetails=new FarmerDetails();
        cropDetails=new CropDetails();
        progressBar = (LinearLayout)findViewById(R.id.progress_confirm_product_buy);
        no_internet = (LinearLayout)findViewById(R.id.no_internet_confirm_product_buy);
        snackLayout = (LinearLayout)findViewById(R.id.snack_layout_confirm_buy_product);
        retry_btn= (TextView)findViewById(R.id.retry_confirm_product_buy);
        dist_card = (CardView)findViewById(R.id.card_dist);


        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);

        sellerNo=getIntent().getStringExtra("sellerMobileNo");
        userMobileNo=getIntent().getStringExtra("userMobileNo");
        buyProductName=getIntent().getStringExtra("productName");
    }

    public void findDistanceOnClick(View v) {
        if (!checkLocationPermissions()) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Snackbar.make(snackLayout, R.string.permission_required, Snackbar.LENGTH_SHORT).show();
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
                    if(!(dist_from_current.getVisibility() == View.VISIBLE))
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

//    Get the details of the crop selected
     void getCropDetails() {
        buyProductAPI = ProductGenerator.createService(BuyProductAPI.class);
        showProgressBar(true);
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
                showProgressBar(false);
                noInternetConnectionFound();
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
                    showProgressBar(false);
                    setValues();
                }
            }

            @Override
            public void onFailure(Call<FarmerDetails> call, Throwable t) {
                showProgressBar(false);
                noInternetConnectionFound();
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

        customDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                        requestProduct();
                    }

        });

        customDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
        Call<String> requestProductCall = buyProductAPI.requestProduct(transaction);
        progressDialog.setMessage(getString(R.string.confirm_buy_dialog_creating_request));
        progressDialog.show();
        requestProductCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, final Response<String> response) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.hide();
                        if(response.code() == 200){
                            if(response.body().equals("true"))
                                Toast.makeText(context, R.string.confirm_buy_request_made, Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(context,R.string.confirm_buy_already_requested, Toast.LENGTH_SHORT ).show();
                            Intent intent=new Intent(context,HomeScreen.class);
                            intent.putExtra("mobileNo",userMobileNo);
                            startActivity(intent);
                            finish();
                        }
                    }
                },900);

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.hide();
                Snackbar.make(snackLayout, R.string.something_went_wrong, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

//    finding distance from current location
    private void findDistanceFromCurrentLocation()
    {
        progressDialog.setMessage(getString(R.string.confirm_buy_dialog_calculating_distance));
        progressDialog.show();
//        Toast.makeText(ConfirmProductBuy.this, "Find distance from current location clicked!", Toast.LENGTH_SHORT).show();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria,false);
        Double lat,lon;
        Location location = locationManager.getLastKnownLocation(provider);
        if(location!=null){
            lat = location.getLatitude();
            lon = location.getLongitude();

            String buyerAddress = String.valueOf(lat).concat(",").concat(String.valueOf(lon));
            buyProductAPI = ProductGenerator.createService(BuyProductAPI.class);
            Call<String> getDistanceCall = buyProductAPI.getDistance(buyerAddress,
                    sellerAddress.getText().toString());
            getDistanceCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if(response.code() == 204){
                        progressDialog.hide();
                        dist_card.setVisibility(View.VISIBLE);
                        dist_from_current.setVisibility(View.VISIBLE);
                        dist_from_current.setText(R.string.no_route_curr);
                    }
                    else if(response.code() == 200){
                        progressDialog.hide();
                        dist_card.setVisibility(View.VISIBLE);
                        dist_from_current.setVisibility(View.VISIBLE);
                        dist_from_current.setText(dist_from_current.getText().toString().concat(" ").concat(response.body()));
                    }
                    else{
                        progressDialog.hide();
                        Snackbar.make(snackLayout, R.string.something_went_wrong, Snackbar.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressDialog.hide();
                    Snackbar.make(snackLayout, R.string.check_network_connection, Snackbar.LENGTH_SHORT).show();
                }
            });

        }
        else{
            progressDialog.hide();
            Snackbar.make(snackLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
        }
    }

//    finding distance from address
    private void findDistanceFromAddress()
    {
        //Getting address of buyer
        AuthenticationAPI authAPI = AuthenticationGenerator.createService(AuthenticationAPI.class);
        Call<FarmerDetails> getAddressBuyerCall = authAPI.getFarmerDetail(userMobileNo);
        progressDialog.setMessage(getString(R.string.confirm_buy_dialog_calculating_distance));
        progressDialog.show();
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
                            progressDialog.hide();
                            dist_card.setVisibility(View.VISIBLE);
                            dist_from_addr.setVisibility(View.VISIBLE);
                            dist_from_addr.setText(R.string.no_route_addr);
                        }
                        else if(response.code() == 200){
                            progressDialog.hide();
                            dist_card.setVisibility(View.VISIBLE);
                            dist_from_addr.setVisibility(View.VISIBLE);
                            dist_from_addr.setText(dist_from_addr.getText().toString().concat(" ").concat(response.body()));
                        }
                        else{
                            progressDialog.hide();
                            Snackbar.make(snackLayout, R.string.something_went_wrong, Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressDialog.hide();
                        Snackbar.make(snackLayout, R.string.check_network_connection, Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<FarmerDetails> call, Throwable t) {
                progressDialog.hide();
                Snackbar.make(snackLayout, R.string.check_network_connection, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void seeDirectionsClicked()
    {
//        List<String> addr = Arrays.asList(farmerDetails.getAddress().split("\\s*,\\s*"));
//        String addrStr = android.text.TextUtils.join("+",addr);
//        List<String> city = Arrays.asList(farmerDetails.getCity().split("\\s*,\\s*"));
//        String cityStr = android.text.TextUtils.join("+",city);
//        List<String> state = Arrays.asList(farmerDetails.getState().split("\\s*,\\s*"));
//        String stateStr = android.text.TextUtils.join("+",state);
//        String address = addrStr.concat(",").concat(cityStr).concat(",").concat(stateStr);
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(farmerDetails.getAddress()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
        else{
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            //Copy App URL from Google Play Store.
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps"));
            startActivity(intent);
        }
    }


//  check for network permissions
    private boolean checkLocationPermissions() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

//    show/hide progress bar
    private void showProgressBar(boolean flag) {
        if(flag)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

//   no internet connection found
    private void noInternetConnectionFound()
    {
        no_internet.setVisibility(View.VISIBLE);
    }
}
