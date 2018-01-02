package com.gw.kisansewa.authentication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gw.kisansewa.Homescreen.HomeScreen;
import com.gw.kisansewa.api.AuthenticationAPI;
import com.gw.kisansewa.R;
import com.gw.kisansewa.apiGenerator.AuthenticationGenerator;
import com.gw.kisansewa.models.FarmerDetails;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FarmerRegister extends AppCompatActivity {

    private FarmerDetails farmerDetails;
    private EditText name,mobileNo,area, city,state, password, confirmPassword;
    private Button register;
    private CoordinatorLayout layout;
    private AuthenticationAPI authenticationAPI;
    private ProgressBar progressBar;

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
        layout = (CoordinatorLayout)findViewById(R.id.layout_signup);
        authenticationAPI = AuthenticationGenerator.createService(AuthenticationAPI.class);
        progressBar =  (ProgressBar) findViewById(R.id.progress_signup);
    }

//    Registering the farmer
    public void RegisterOnClick(View view) {
        hideKeyboard();
        checkValidity();
    }

    //    checking validation of the input
    private void checkValidity(){
        if(name.getText().toString().equals("") || mobileNo.getText().toString().equals("") ||
                area.getText().toString().equals("") || city.getText().toString().equals("") ||
                state.getText().toString().equals("") || password.getText().toString().equals("") ||
                confirmPassword.getText().toString().equals(""))
            Snackbar.make(layout,R.string.enter_all_details,Snackbar.LENGTH_SHORT).show();
        else if (mobileNo.getText().toString().length() != 10)
            Snackbar.make(layout,R.string.signup_valid_no,Snackbar.LENGTH_SHORT).show();
        else  if (!password.getText().toString().equals(confirmPassword.getText().toString()))
            Snackbar.make(layout,R.string.passwords_do_not_match,Snackbar.LENGTH_SHORT).show();
        else
            checkAvailable();
    }

    //    check availability of mobile no
    private void checkAvailable(){
        showProgressBar(true);
        Call<Void> checkAvailabilityCall = authenticationAPI.checkAvailability(mobileNo.getText().toString());
        checkAvailabilityCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 302){
                    Snackbar.make(layout, R.string.signup_already_registered, Snackbar.LENGTH_SHORT).show();
                    showProgressBar(false);
                }
                else if (response.code() == 200) {
                    register();
                }
            }
            @Override
            public void onFailure(Call < Void > call, Throwable t){
                showProgressBar(false);
                Snackbar snackbar = Snackbar.make(layout, R.string.check_network_connection, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkValidity();
                            }
                        });
                snackbar.show();
            }
        });
    }

    //    register user
    private void register(){

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
                    editor.putString(FarmerLogin.FMobileNo, mobileNo.getText().toString());
                    editor.commit();



                    showProgressBar(false);
                    Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                    intent.putExtra("mobileNo", mobileNo.getText().toString());
                    startActivity(intent);
                    finish();
                }
                if(response.code() == 502){
                    Snackbar.make(layout,R.string.something_went_wrong, Snackbar.LENGTH_SHORT).show();
                    showProgressBar(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showProgressBar(false);
                Snackbar snackbar = Snackbar.make(layout,R.string.check_network_connection,Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkValidity();
                            }
                        });
                snackbar.show();
            }
        });
    }

    //    hide the keyboard
    private void hideKeyboard(){
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


