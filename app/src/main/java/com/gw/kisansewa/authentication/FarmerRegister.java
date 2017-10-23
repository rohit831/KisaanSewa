package com.gw.kisansewa.authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gw.kisansewa.api.AuthenticationAPI;
import com.gw.kisansewa.HomeScreen;
import com.gw.kisansewa.R;
import com.gw.kisansewa.apiGenerator.AuthenticationGenerator;
import com.gw.kisansewa.models.FarmerDetails;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FarmerRegister extends AppCompatActivity {

    private FarmerDetails farmerDetails;

    private EditText name;
    private EditText mobileNo;
    private EditText area;
    private EditText city;
    private EditText state;
    private EditText password;
    private EditText confirmPassword;
    private Button register;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        farmerInitialization();
    }

    public void farmerInitialization()
    {
        name=(EditText)findViewById(R.id.name);
        mobileNo=(EditText)findViewById(R.id.mobileNo);
        area=(EditText)findViewById(R.id.area);
        city=(EditText)findViewById(R.id.city);
        state=(EditText)findViewById(R.id.state);
        password=(EditText)findViewById(R.id.password);
        confirmPassword=(EditText)findViewById(R.id.confirmPassword);
        register=(Button) findViewById(R.id.registerBtn);

    }

    public void RegisterOnClick(View view)
    {
        if(checkValidity()) {
            final AuthenticationAPI authenticationAPI = AuthenticationGenerator.createService(AuthenticationAPI.class);
            Call<Void> checkAvailabilityCall = authenticationAPI.checkAvailability(mobileNo.getText().toString());

            checkAvailabilityCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.code() == 302)
                        Toast.makeText(getApplicationContext(), "User already registered", Toast.LENGTH_SHORT).show();
                    else if(response.code()==200){
                        farmerDetails = new FarmerDetails(name.getText().toString(), mobileNo.getText().toString(),
                                area.getText().toString(), city.getText().toString(),
                                state.getText().toString(), password.getText().toString());

                        Call<Void> signUpCall = authenticationAPI.createNewUser(farmerDetails);
                        signUpCall.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.code() == 201){
                                    SharedPreferences sharedPreferences = getSharedPreferences(FarmerLogin.FarmerPreferences, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putLong(FarmerLogin.FMobileNo, Long.parseLong(mobileNo.getText().toString()));
                                    editor.commit();

                                    Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                                    intent.putExtra("mobileNo", mobileNo.getText().toString());
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "Unable to process your request at the moment!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Unable to process your request at the moment." +
                            "Please try again later!!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public boolean checkValidity(){
        if(name.getText().toString().equals("") || mobileNo.getText().toString().equals("") ||
                area.getText().toString().equals("") || city.getText().toString().equals("") ||
                state.getText().toString().equals("") || password.getText().toString().equals("") ||
                confirmPassword.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Enter All Details!!",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (mobileNo.getText().toString().length() != 10) {
            Toast.makeText(getApplicationContext(), "Enter a valid Mobile No!!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else  if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Passwords Don't Match!!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }
}


