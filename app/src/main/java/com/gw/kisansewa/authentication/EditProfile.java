package com.gw.kisansewa.authentication;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gw.kisansewa.Homescreen.HomeScreen;
import com.gw.kisansewa.R;
import com.gw.kisansewa.api.AuthenticationAPI;
import com.gw.kisansewa.apiGenerator.AuthenticationGenerator;
import com.gw.kisansewa.models.FarmerDetails;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends AppCompatActivity {

    private EditText name, mobileNo, area, city, state;
//    ImageView backButton, updateButton;
    AuthenticationAPI authenticationAPI;
    SharedPreferences sharedPreferences;
    String mobile;
    String passwrd;
    private LinearLayout progressBar, no_internet;
    private CoordinatorLayout snackLayout;
    private TextView retry_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        initialize();
        getDetails();


//      enable back button and changing the name of action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.tab_edit_profile);

        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no_internet.setVisibility(View.GONE);
                getDetails();
            }
        });

//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(EditProfile.this, HomeScreen.class));
//                finish();
//            }
//        });
//
//        updateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateDetails();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }
        else if(id == R.id.edit_profile_done_btn){
            if(no_internet.getVisibility() == View.VISIBLE)
                Snackbar.make(snackLayout, R.string.check_network_connection, Snackbar.LENGTH_SHORT).show();
            else
                updateDetails();
        }
        return super.onOptionsItemSelected(item);
    }

//    referencing all the elements
    private void initialize() {

        int color = getResources().getColor(R.color.colorPrimary);
        Drawable dname = getResources().getDrawable(R.drawable.ic_edit_profile_name);
        Drawable dmobileNo = getResources().getDrawable(R.drawable.ic_call);
        Drawable darea= getResources().getDrawable(R.drawable.ic_place);
        Drawable dcity = getResources().getDrawable(R.drawable.ic_location_city);
        Drawable dstate = getResources().getDrawable(R.drawable.ic_location_state);
        dname.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        dmobileNo.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        darea.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        dcity.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        dstate.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        name = (EditText) findViewById(R.id.edit_profile_user);
        mobileNo = (EditText) findViewById(R.id.edit_profile_mobileNo);
        area = (EditText) findViewById(R.id.edit_profile_area);
        city = (EditText) findViewById(R.id.edit_profile_city);
        state = (EditText) findViewById(R.id.edit_profile_state);
        sharedPreferences = getSharedPreferences(FarmerLogin.FarmerPreferences, Context.MODE_PRIVATE);
        mobile = sharedPreferences.getString(FarmerLogin.FMobileNo, "");
        progressBar = (LinearLayout) findViewById(R.id.progress_edit_profile);
        no_internet = (LinearLayout) findViewById(R.id.no_internet_edit_profile);
        snackLayout = (CoordinatorLayout) findViewById(R.id.snack_layout_edit_profile);
        retry_btn = (TextView) findViewById(R.id.retry_edit_profile);
//        backButton =(ImageView)findViewById(R.id.edit_profile_back_btn);
//        updateButton = (ImageView) findViewById(R.id.edit_profile_update_btn);
    }

//    Get user details
    private void getDetails() {
        authenticationAPI = AuthenticationGenerator.createService(AuthenticationAPI.class);
        showProgressBar(true);
        Call<FarmerDetails> getFarmerDetailsDetailsCall = authenticationAPI.getFarmerDetail(mobile);
        getFarmerDetailsDetailsCall.enqueue(new Callback<FarmerDetails>() {
            @Override
            public void onResponse(Call<FarmerDetails> call, Response<FarmerDetails> response) {
                showProgressBar(false);
                if(response.code()==200 ){
                    assignDetails(response.body());
                }
                else{
                    Snackbar.make(snackLayout, R.string.something_went_wrong, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FarmerDetails> call, Throwable t) {
                showProgressBar(false);
                no_internet.setVisibility(View.VISIBLE);
            }
        });
    }

//    assigning data to view
    private void assignDetails(FarmerDetails farmer) {
        name.setText(farmer.getName());
        city.setText(farmer.getCity());
        mobileNo.setText(mobile);
        area.setText(farmer.getArea());
        state.setText(farmer.getState());
        passwrd = farmer.getPassword();
    }

//    updating user data
    private void updateDetails() {
        FarmerDetails farmer = new FarmerDetails(name.getText().toString(), mobile,
                area.getText().toString(),city.getText().toString(),
                state.getText().toString(),passwrd);

        authenticationAPI = AuthenticationGenerator.createService(AuthenticationAPI.class);
        Call<Void> updateDetailsCall = authenticationAPI.updateFarmerDetails(farmer, mobile);
        updateDetailsCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    Toast.makeText(EditProfile.this, "Updated profile successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditProfile.this, HomeScreen.class));
                    finish();
                }
                else{
                    Toast.makeText(EditProfile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditProfile.this, HomeScreen.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditProfile.this, "Can't connect to server at the moment", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    show/hide progress bar
    private void showProgressBar(boolean show) {
        if(show)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

}
