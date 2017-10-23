package com.gw.kisansewa.api;

import com.gw.kisansewa.models.FarmerDetails;
import com.gw.kisansewa.models.LoginInformation;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AuthenticationAPI {

    @GET("available/{mobileNo}/")
    Call<Void> checkAvailability(@Path("mobileNo") String mobileNo);

    @POST("register/")
    Call<Void> createNewUser(@Body FarmerDetails farmerDetails);

    @POST("login")
    Call<Void> loginUser(@Body LoginInformation loginInformation);
}
