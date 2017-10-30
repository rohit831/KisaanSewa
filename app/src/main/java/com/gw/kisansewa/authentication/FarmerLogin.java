package com.gw.kisansewa.authentication;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gw.kisansewa.Homescreen.HomeScreen;
import com.gw.kisansewa.api.AuthenticationAPI;
import com.gw.kisansewa.DBHandler;
import com.gw.kisansewa.R;
import com.gw.kisansewa.apiGenerator.AuthenticationGenerator;
import com.gw.kisansewa.models.LoginInformation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FarmerLogin extends AppCompatActivity {

    private DBHandler dbHandler;
    private EditText mobileNo;
    private EditText password;
    private Button loginButton;
    private TextView registerHere;

    public static final String FarmerPreferences= "FarmerPrefs";
    public static final String FMobileNo="FMobileNo";

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        sharedPreferences=getSharedPreferences(FarmerPreferences,MODE_PRIVATE);
        if(!sharedPreferences.getString(FMobileNo,"").equals(""))
        {
            Intent intent=new Intent(this,HomeScreen.class);
            intent.putExtra("mobileNo",sharedPreferences.getString(FMobileNo,""));
            startActivity(intent);
            finish();
        }

        farmerInitialization();
    }

    public void farmerInitialization()
    {
        dbHandler= new DBHandler(this,null,null,1);

        mobileNo=(EditText)findViewById(R.id.loginMobileNo);
        password=(EditText)findViewById(R.id.loginPassword);
        loginButton=(Button)findViewById(R.id.loginButton);
        registerHere=(TextView)findViewById(R.id.registerHere);

    }

    public void LoginOnClick(View view)
    {
        if(mobileNo.getText().toString().equals("") || password.getText().toString().equals(""))
            Toast.makeText(getApplicationContext(),"Enter Mobile No/Password First!!",Toast.LENGTH_SHORT).show();
        else {

            AuthenticationAPI authenticationAPI = AuthenticationGenerator.createService(AuthenticationAPI.class);
            LoginInformation loginInformation = new LoginInformation(mobileNo.getText().toString(), password.getText().toString());
            Call<Void> loginCall = authenticationAPI.loginUser(loginInformation);

            loginCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.code() == 202){

                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString(FMobileNo,mobileNo.getText().toString());
                        editor.commit();

                        Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                        intent.putExtra("mobileNo",mobileNo.getText().toString());
                        startActivity(intent);
                        finish();
                    }
                    if(response.code()==401){
                        Toast.makeText(getApplicationContext(), "Invalid User crendentials!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Unable to process your request at the moment!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void RegisterHereOnClick(View view)
    {
        Intent intent=new Intent(this,FarmerRegister.class);
        startActivity(intent);
    }
}
