package com.gw.kisansewa.authentication;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gw.kisansewa.Homescreen.Homescreen;
import com.gw.kisansewa.api.AuthenticationAPI;
import com.gw.kisansewa.R;
import com.gw.kisansewa.apiGenerator.AuthenticationGenerator;
import com.gw.kisansewa.models.LoginInformation;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FarmerLogin extends AppCompatActivity {

    private EditText mobileNo;
    private EditText password;
    private Button loginButton;
    private TextView registerHere, choose_language, notRegisteredYet, appName;
    private CoordinatorLayout coordinatorLayout;
    private ProgressBar progressBar;
    private AlertDialog alertDialog;
    public static Locale myLocale;
    private TextInputLayout passwordLayout, mobileNoLayout;

    public static final String FarmerPreferences= "FarmerPrefs";
    public static final String FMobileNo="FMobileNo";
    public static final String Local_Preference = "Locale Preference";
    public static final String Local_KeyValue = "Saved Locale";

    SharedPreferences sharedPreferences, languagePreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        sharedPreferences=getSharedPreferences(FarmerPreferences,MODE_PRIVATE);
        languagePreferences = getSharedPreferences(Local_Preference, MODE_PRIVATE);
        if(!sharedPreferences.getString(FMobileNo,"").equals(""))
        {
            Intent intent=new Intent(this,Homescreen.class);
            intent.putExtra("mobileNo",sharedPreferences.getString(FMobileNo,""));
            startActivity(intent);
            finish();
        }
        farmerInitialization();
        loadLocale();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        choose_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageDialog();
            }
        });
    }

    public void farmerInitialization() {
        mobileNo=findViewById(R.id.loginMobileNo);
        appName = findViewById(R.id.login_appName);
        notRegisteredYet = findViewById(R.id.login_not_registered_yet);
        passwordLayout =  findViewById(R.id.login_password_textInputLayout);
        mobileNoLayout = findViewById(R.id.login_mobileNo_textInputLayout);
        password=findViewById(R.id.loginPassword);
        loginButton=findViewById(R.id.loginButton);
        registerHere=findViewById(R.id.registerHere);
        coordinatorLayout = findViewById(R.id.snack_layout_login);
        progressBar = findViewById(R.id.progress_login);
        choose_language = findViewById(R.id.choose_language_login);
    }

    private void login() {
        hideKeyboard();
        if(mobileNo.getText().toString().equals("") || password.getText().toString().equals(""))
            Snackbar.make(coordinatorLayout,R.string.login_details_enter_first,Snackbar.LENGTH_SHORT).show();
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

                        Intent intent = new Intent(getApplicationContext(), Homescreen.class);
                        intent.putExtra("mobileNo",mobileNo.getText().toString());
                        startActivity(intent);
                        finish();
                    }
                    if(response.code()==401){
                        Snackbar.make(coordinatorLayout,R.string.login_invalid_credentials,Snackbar.LENGTH_LONG).show();
                        showProgressBar(false);
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    showProgressBar(false);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout,R.string.unable_to_process_request,Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.retry, new View.OnClickListener() {
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

    public void RegisterHereOnClick(View view) {
        Intent intent=new Intent(this,FarmerRegister.class);
        startActivity(intent);
    }

    private void showLanguageDialog() {
        String[] languages = getResources().getStringArray(R.array.languages);
        AlertDialog.Builder builder = new AlertDialog.Builder(FarmerLogin.this);
        builder.setTitle(R.string.choose_language_dialog);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        builder.setSingleChoiceItems(languages, getSelectedLanguage(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which)
                {
                    case 0:
                        changeLanguage("en");
//                        loadActivityAgain();
                        break;
                    case 1:
                        changeLanguage("hi");
//                        loadActivityAgain();
                        break;
                    case 2:
                        changeLanguage("mr");
//                        loadActivityAgain();
                        break;
                    case 3:
                        changeLanguage("pa");
//                        loadActivityAgain();
                        break;
                    case 4:
                        changeLanguage("sd");
//                        loadActivityAgain();
                        break;
                    case 5:
                        changeLanguage("ta");
//                        loadActivityAgain();
                        break;
                    case 6:
                        changeLanguage("te");
//                        loadActivityAgain();
                        break;
                }

            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void changeLanguage(String lang)
    {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        editor = languagePreferences.edit();
        editor.putString(Local_KeyValue, lang);
        editor.commit();
        Locale.setDefault(myLocale);
        Configuration conf = new Configuration();
        conf.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(conf,
                getBaseContext().getResources().getDisplayMetrics());
        updateTexts();
    }

    private int getSelectedLanguage()
    {
        int flag = 0;
        String language = languagePreferences.getString(Local_KeyValue, "");
        switch(language){
            case "en":
                flag = 0;
                break;
            case "hi":
                flag = 1;
                break;
            case "mr":
                flag = 2;
                break;
            case "pa":
                flag = 3;
                break;
            case "sd":
                flag = 4;
                break;
            case "ta":
                flag = 5;
                break;
            case "te":
                flag = 6;
                break;
        }
        return flag;
    }

    private void loadActivityAgain()
    {
        finish();
        startActivity(new Intent(FarmerLogin.this, FarmerLogin.class));
    }

    public void loadLocale()
    {
        String language = languagePreferences.getString(Local_KeyValue, "");
        changeLanguage(language);
//        updateTexts();
    }

    private void updateTexts()
    {
        mobileNoLayout.setHint(getString(R.string.mobile_no_view));
        passwordLayout.setHint(getString(R.string.password_view));
        loginButton.setText(R.string.login_view);
        choose_language.setText(R.string.choose_language);
        registerHere.setText(R.string.register_here_view);
        appName.setText(R.string.app_name);
        notRegisteredYet.setText(R.string.not_registered_yet_view);
    }

    private void hideKeyboard() {
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
