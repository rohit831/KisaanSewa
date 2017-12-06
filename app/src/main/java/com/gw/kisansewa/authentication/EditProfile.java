package com.gw.kisansewa.authentication;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

    EditText name, mobileNo, area, city, state;
    ImageView backButton, updateButton;
    AuthenticationAPI authenticationAPI;
    SharedPreferences sharedPreferences;
    String mobile;
    String passwrd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        initialize();
        getDetails();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfile.this, HomeScreen.class));
                finish();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDetails();
            }
        });
    }

    private void initialize()
    {
        name = (EditText) findViewById(R.id.edit_profile_user);
        mobileNo = (EditText) findViewById(R.id.edit_profile_mobileNo);
        area = (EditText) findViewById(R.id.edit_profile_area);
        city = (EditText) findViewById(R.id.edit_profile_city);
        state = (EditText) findViewById(R.id.edit_profile_state);
        sharedPreferences = getSharedPreferences(FarmerLogin.FarmerPreferences, Context.MODE_PRIVATE);
        mobile = sharedPreferences.getString(FarmerLogin.FMobileNo, "");
        backButton =(ImageView)findViewById(R.id.edit_profile_back_btn);
        updateButton = (ImageView) findViewById(R.id.edit_profile_update_btn);
    }

    private void getDetails()
    {
        authenticationAPI = AuthenticationGenerator.createService(AuthenticationAPI.class);
        Call<FarmerDetails> getFarmerDetailsDetailsCall = authenticationAPI.getFarmerDetail(mobile);
        getFarmerDetailsDetailsCall.enqueue(new Callback<FarmerDetails>() {
            @Override
            public void onResponse(Call<FarmerDetails> call, Response<FarmerDetails> response) {
                if(response.code()==200 ){
                    assignDetails(response.body());
                }
                else{
                    Toast.makeText(EditProfile.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FarmerDetails> call, Throwable t) {
                Toast.makeText(EditProfile.this, "Can't connect to server at the moment!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignDetails(FarmerDetails farmer)
    {
        name.setText(farmer.getName());
        city.setText(farmer.getCity());
        mobileNo.setText(mobile);
        area.setText(farmer.getArea());
        state.setText(farmer.getState());
        passwrd = farmer.getPassword();
    }

    private void updateDetails()
    {
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
}
