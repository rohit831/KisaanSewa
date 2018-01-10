package com.gw.kisansewa.authentication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gw.kisansewa.Homescreen.Homescreen;
import com.gw.kisansewa.R;
import com.gw.kisansewa.api.AuthenticationAPI;
import com.gw.kisansewa.apiGenerator.AuthenticationGenerator;
import com.gw.kisansewa.models.FarmerDetails;
import com.gw.kisansewa.models.LoginInformation;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfile extends AppCompatActivity {

    private EditText name, mobileNo, area, city, state, oldPassword, newPassword, confNewPassword;
    private AuthenticationAPI authenticationAPI;
    private SharedPreferences sharedPreferences;
    private SharedPreferences languagePreferences;
    private TextView notifyPassword;
    private FarmerDetails farmerDetails;
    private CircleImageView profile_pic;
    private byte[] profileBytes;

    private String mobile,passwrd, language;
    private LinearLayout progressBar, no_internet;
    private LinearLayout snackLayout;
    private TextView retry_btn, choose_language, updatePassword;
    private AlertDialog alertDialog;
    public static Locale myLocale;
    private TextView vChangeLang, vChangePassword, vOldPassword,
            vNewPassword, vConfNewPassword;
    private Context context = this;

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

        oldPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    notifyPassword.setVisibility(View.INVISIBLE);
            }
        });
        newPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    notifyPassword.setVisibility(View.INVISIBLE);
            }
        });
        confNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    updatePassword.animate().translationY(0);
                    notifyPassword.setVisibility(View.INVISIBLE);
            }
        });
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

    //Update password
    public void updatePasswordClicked(View v){
        if(oldPassword.getText().toString().trim().equals("") ||
                newPassword.getText().toString().trim().equals("") ||
                confNewPassword.getText().toString().trim().equals("")) {
            notifyPassword.setVisibility(View.VISIBLE);
            notifyPassword.setText(R.string.enter_all_details);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                   notifyPassword.setVisibility(View.GONE);
//                }
//            },500);
        }
        else if(!newPassword.getText().toString()
                .equals(confNewPassword.getText().toString())){
            notifyPassword.setVisibility(View.VISIBLE);
            notifyPassword.setText(R.string.passwords_do_not_match);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    notifyPassword.setVisibility(View.GONE);
//                }
//            },500);
        }
        else{
            final ProgressDialog progressDialog =new ProgressDialog(EditProfile.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.password_updating));
            progressDialog.show();
            if(!oldPassword.getText().toString().equals(farmerDetails.getPassword()))
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.hide();
                        Snackbar.make(snackLayout, R.string.wrong_password, Snackbar.LENGTH_LONG).show();
                    }
                },500);
            }
            else{
                AuthenticationAPI authAPI = AuthenticationGenerator.createService(AuthenticationAPI.class);
                final LoginInformation loginInformation =  new LoginInformation(mobile, newPassword.getText().toString());
                Call<Void> updatePasswordCall = authAPI.updatePassword(loginInformation);
                updatePasswordCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressDialog.hide();
                        oldPassword.setText("");
                        newPassword.setText("");
                        confNewPassword.setText("");
                        if(response.code() == 200){
                            passwrd = loginInformation.getPassword();
                            Snackbar.make(snackLayout,R.string.password_updated, Snackbar.LENGTH_LONG).show();
                        }
                        else
                            Snackbar.make(snackLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressDialog.hide();
                        Snackbar snackbar = Snackbar.make(snackLayout, R.string.check_network_connection, Snackbar.LENGTH_LONG);
                        snackbar.setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updatePassword.performClick();
                            }
                        });
                        snackbar.show();
                    }
                });
            }
        }


    }

//    referencing all the elements
    private void initialize() {

        int color = getResources().getColor(R.color.cardview_dark_background);
        Drawable dname = getResources().getDrawable(R.drawable.ic_edit_profile_name);
        Drawable dmobileNo = getResources().getDrawable(R.drawable.ic_call);
        Drawable darea = getResources().getDrawable(R.drawable.ic_place);
        Drawable dcity = getResources().getDrawable(R.drawable.ic_location_city);
        Drawable dstate = getResources().getDrawable(R.drawable.ic_location_state);
        Drawable dedit = getResources().getDrawable(R.drawable.ic_edit);
        dname.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        dmobileNo.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        darea.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        dcity.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        dstate.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        dedit.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        name = (EditText) findViewById(R.id.edit_profile_user);
        mobileNo = (EditText) findViewById(R.id.edit_profile_mobileNo);
        area = (EditText) findViewById(R.id.edit_profile_area);
        city = (EditText) findViewById(R.id.edit_profile_city);
        state = (EditText) findViewById(R.id.edit_profile_state);
        oldPassword = (EditText) findViewById(R.id.edit_profile_old_passwrd);
        newPassword = (EditText) findViewById(R.id.edit_profile_new_passwrd);
        confNewPassword = (EditText) findViewById(R.id.edit_profile_conf_new_passwrd);
        profile_pic = (CircleImageView) findViewById(R.id.edit_profile_profile_pic);

        sharedPreferences = getSharedPreferences(FarmerLogin.FarmerPreferences, Context.MODE_PRIVATE);
        mobile = sharedPreferences.getString(FarmerLogin.FMobileNo, "");

        languagePreferences = getSharedPreferences(FarmerLogin.Local_Preference, MODE_PRIVATE);
        getCurrentLanguage(languagePreferences.getString(FarmerLogin.Local_KeyValue, ""));

        vChangeLang = (TextView) findViewById(R.id.edit_profile_change_language_view);
        vChangePassword = (TextView) findViewById(R.id.edit_profile_change_passwrd_view);
        vOldPassword = (TextView) findViewById(R.id.edit_profile_old_passwrd_view);
        vNewPassword = (TextView) findViewById(R.id.edit_profile_new_passwrd_view);
        vConfNewPassword = (TextView) findViewById(R.id.edit_profile_conf_new_passwrd_view);

        progressBar = (LinearLayout) findViewById(R.id.progress_edit_profile);
        no_internet = (LinearLayout) findViewById(R.id.no_internet_edit_profile);
        snackLayout = (LinearLayout) findViewById(R.id.snack_layout_edit_profile);
        retry_btn = (TextView) findViewById(R.id.retry_edit_profile);
        choose_language = (TextView) findViewById(R.id.edit_profile_choose_language);
        updatePassword = (TextView) findViewById(R.id.edit_profile_update_password);
        notifyPassword = (TextView) findViewById(R.id.edit_profile_notify_password);
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
                    farmerDetails = response.body();
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
        if(!farmer.getImage().equals("")){
            Picasso.with(EditProfile.this)
                    .load(farmer.getImage())
                    .noFade()
                    .into(profile_pic);
        }
        choose_language.setText(language);
    }

    //    change language clicked
    public void changeLanguageEditProfileClicked(View view) {
        String[] languages = getResources().getStringArray(R.array.languages);
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
        builder.setTitle(R.string.choose_language_dialog);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        builder.setSingleChoiceItems(languages, getSelectedLanguageCode(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which)
                {
                    case 0:
                        changeLanguage("en");
                        choose_language.setText(R.string.lang_en);
                        break;
                    case 1:
                        changeLanguage("hi");
                        choose_language.setText(R.string.lang_hi);
                        break;
                    case 2:
                        changeLanguage("mr");
                        choose_language.setText(R.string.lang_mr);
                        break;
                    case 3:
                        changeLanguage("pa");
                        choose_language.setText(R.string.lang_pa);
                        break;
                    case 4:
                        changeLanguage("sd");
                        choose_language.setText(R.string.lang_sd);
                        break;
                    case 5:
                        changeLanguage("ta");
                        choose_language.setText(R.string.lang_ta);
                        break;
                    case 6:
                        changeLanguage("te");
                        choose_language.setText(R.string.lang_te);
                        break;
                }

            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

//  get language from language code
    private void getCurrentLanguage(String langCode) {
        switch(langCode){
            case "en":
                language = getString(R.string.lang_en);
                break;
            case "hi":
                language = getString(R.string.lang_hi);
                break;
            case "mr":
                language = getString(R.string.lang_mr);
                break;
            case "pa":
                language = getString(R.string.lang_pa);
                break;
            case "sd":
                language = getString(R.string.lang_sd);
                break;
            case "ta":
                language = getString(R.string.lang_ta);
                break;
            case "te":
                language = getString(R.string.lang_te);
                break;
        }
    }

    //    get code of current language
    private int getSelectedLanguageCode() {
        int flag = 0;
        String langCode = languagePreferences.getString(FarmerLogin.Local_KeyValue, "");
        switch(langCode){
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

    //change language
    private void changeLanguage(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        SharedPreferences.Editor editor;
        editor = languagePreferences.edit();
        editor.putString(FarmerLogin.Local_KeyValue, lang);
        editor.commit();
        Locale.setDefault(myLocale);
        Configuration conf = new Configuration();
        conf.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(conf,
                getBaseContext().getResources().getDisplayMetrics());
        updateTexts();
    }

    //    Updates texts after language change
    private void updateTexts(){
        getSupportActionBar().setTitle(R.string.tab_edit_profile);
        vChangeLang.setText(R.string.change_language);
        vChangePassword.setText(R.string.change_password);
        vOldPassword.setText(R.string.edit_profile_old_paswrd);
        vNewPassword.setText(R.string.edit_profile_new_paswrd);
        vConfNewPassword.setText(R.string.edit_profile_conf_new_paswrd);
        updatePassword.setText(R.string.update);
    }

    //    updating user data
    private void updateDetails() {
        FarmerDetails farmer = new FarmerDetails(name.getText().toString(), mobile,
                area.getText().toString(),city.getText().toString(),
                state.getText().toString(),passwrd);

        final ProgressDialog progressDialog = new ProgressDialog(EditProfile.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.profile_updating));
        progressDialog.show();

        authenticationAPI = AuthenticationGenerator.createService(AuthenticationAPI.class);
        Call<Void> updateDetailsCall = authenticationAPI.updateFarmerDetails(farmer, mobile);
        updateDetailsCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    progressDialog.hide();
                    Toast.makeText(EditProfile.this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditProfile.this, Homescreen.class));
                    finish();
                }
                else{
                    progressDialog.hide();
                    Snackbar.make(snackLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.hide();
                Snackbar.make(snackLayout, R.string.check_network_connection, Snackbar.LENGTH_LONG).show();
            }
        });
    }

//  update profile pic clicked
    public void updateProfilePicClicked(View view){
        if(!checkStoragePermissions()){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            try {
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), 0);
            } catch (ActivityNotFoundException e) {
                Snackbar.make(snackLayout, R.string.something_went_wrong, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            if(permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)){
                if(!(grantResults[0] == PackageManager.PERMISSION_GRANTED))
                    Snackbar.make(snackLayout, R.string.permission_required, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                try{
                    InputStream is = getContentResolver().openInputStream(data.getData());
                    getBytes(is);
                    uploadImage();
                }catch(Exception e){
                    Snackbar.make(snackLayout, R.string.something_went_wrong, Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

//  convert input stream into a byte array
    public void getBytes(InputStream is)throws IOException {
        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();

        int buffSize = 1024;
        byte[] buff = new byte[buffSize];

        int len;
        while((len = is.read(buff)) != -1){
            byteBuff.write(buff, 0 , len);
        }

        profileBytes = byteBuff.toByteArray();
    }

    private void uploadImage(){
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.225.133:3000/customer/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        authenticationAPI = retrofit.create(AuthenticationAPI.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), profileBytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);
        Call<String> uploadImageCall = authenticationAPI.uploadPic(body, mobile);
        Snackbar.make(snackLayout, "Uploading .. ", Snackbar.LENGTH_SHORT).show();
        uploadImageCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code() == 200){
                    Snackbar.make(snackLayout, "Uploaded successfully", Snackbar.LENGTH_SHORT).show();
                    Toast.makeText(EditProfile.this, response.body(), Toast.LENGTH_SHORT).show();
                    Picasso.with(EditProfile.this)
                            .load(response.body())
                            .noFade()
                            .into(profile_pic);
                }else{
                    Snackbar.make(snackLayout, R.string.something_went_wrong, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Snackbar.make(snackLayout, R.string.check_network_connection, Snackbar.LENGTH_SHORT).show();
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

    //  check for network permissions
    private boolean checkStoragePermissions() {
        String permission = "android.permission.READ_EXTERNAL_STORAGE";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


}
