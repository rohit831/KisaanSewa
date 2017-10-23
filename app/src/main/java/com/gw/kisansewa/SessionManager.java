package com.gw.kisansewa;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.gw.kisansewa.authentication.FarmerLogin;

public class SessionManager  {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;

    int PRIVATE_MODE= 0;

    private static final String PREF_NAME= "KisaanSewa";
    private static final String IS_LOGIN="IsLoggedIn";

    public static final String KEY_MOBILE_NO="mobileNo";

    public SessionManager(Context context)
    {
        this.context=context;
        preferences=context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor=preferences.edit();

    }

    public void createLoginSession(String mobileNo)
    {
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(KEY_MOBILE_NO,mobileNo);
        editor.commit();
    }

    public void checkLogin()
    {
        if(!this.isLoggedIn())
        {
            Intent intent=new Intent(context,FarmerLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public void logoutUser()
    {
        editor.clear();
        editor.commit();
        Intent intent= new Intent(context,FarmerLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public boolean isLoggedIn()
    {
        return preferences.getBoolean(IS_LOGIN,false);
    }
}
