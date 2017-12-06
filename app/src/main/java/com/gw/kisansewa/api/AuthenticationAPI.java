package com.gw.kisansewa.api;

import com.gw.kisansewa.models.FarmerDetails;
import com.gw.kisansewa.models.LoginInformation;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AuthenticationAPI {

//    to check if a mobile no is available
    @GET("available/{mobileNo}/")
    Call<Void> checkAvailability(@Path("mobileNo") String mobileNo);

//    sign up a new user
    @POST("register/")
    Call<Void> createNewUser(@Body FarmerDetails farmerDetails);

//    login user
    @POST("login")
    Call<Void> loginUser(@Body LoginInformation loginInformation);

    //    get details of a user
    @GET("editprofile/{mobileNo}")
    Call<FarmerDetails> getFarmerDetail(@Path("mobileNo") String mobileNo);

    //update details of a user
    @POST("editprofile/{mobileNo}")
    Call<Void> updateFarmerDetails(@Body FarmerDetails farmerDetails
            ,@Path("mobileNo") String mobileNo);
}


