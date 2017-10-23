package com.gw.kisansewa.api;

import com.gw.kisansewa.models.CropDetails;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SellProductAPI {

    @GET("sell/{mobileNo}")
    Call<ArrayList<CropDetails>> getUserCrops(@Path("mobileNo") String mobileNo);

    @GET("sell/available/{mobileNo}/{cropName}")
    Call<Void> checkCropAvailability(@Path("mobileNo") String mobileNo, @Path("cropName") String cropName);

    @POST("sell/")
    Call<Void> addNewCrop(@Body CropDetails cropDetails);

    @DELETE("sell/{mobileNo}/{cropName}")
    Call<Void> deleteCrop(@Path("mobileNo") String mobileNo, @Path("cropName") String cropName);

    @POST("sell/{mobileNo}/{cropName}/edit")
    Call<Void> editCrop(@Path("mobileNo") String mobileNo, @Path("cropName") String cropName, @Body CropDetails cropDetails);
}
