package com.gw.kisansewa.authentication;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
    private CoordinatorLayout coordinatorLayout;
    private ProgressBar progressBar;

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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void farmerInitialization()
    {
        dbHandler= new DBHandler(this,null,null,1);

        mobileNo=(EditText)findViewById(R.id.loginMobileNo);
        password=(EditText)findViewById(R.id.loginPassword);
        loginButton=(Button)findViewById(R.id.loginButton);
        registerHere=(TextView)findViewById(R.id.registerHere);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.layout_login);
        progressBar = (ProgressBar)findViewById(R.id.progress_login);
    }

    private void login()
    {
        hideKeyboard();
        if(mobileNo.getText().toString().equals("") || password.getText().toString().equals(""))
            Snackbar.make(coordinatorLayout,"Enter Mobile No/Password First!!",Snackbar.LENGTH_SHORT).show();
        else {
            showProgressBar(true);
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

                        showProgressBar(false);

                        Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                        intent.putExtra("mobileNo",mobileNo.getText().toString());
                        startActivity(intent);
                        finish();
                    }
                    if(response.code()==401){
                        Snackbar.make(coordinatorLayout,"Invalid User Credentials",Snackbar.LENGTH_LONG).show();
                        showProgressBar(false);
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    showProgressBar(false);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout,"Unable to process your request at the moment!",Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    login();
                                }
                            });
                    snackbar.show();
                }
            });
        }
    }

    public void RegisterHereOnClick(View view)
    {
        Intent intent=new Intent(this,FarmerRegister.class);
        startActivity(intent);
    }

    private void hideKeyboard()
    {
        View view = this.getCurrentFocus();
        if(view!=null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void showProgressBar(boolean show){
        if(show){
            progressBar.setVisibility(View.VISIBLE);
        }
        else
            progressBar.setVisibility(View.INVISIBLE);
    }
}
