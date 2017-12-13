package com.gw.kisansewa.api;


import com.gw.kisansewa.models.CropDetails;
import com.gw.kisansewa.models.FarmerDetails;
import com.gw.kisansewa.models.RequestDetails;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BuyProductAPI {

//    get all the available crops in the market
    @GET("buy/allcrops")
    Call<ArrayList<CropDetails>> getCropsAvailable();

//    get all the seller names of respective crops
    @POST("buy/sellernames")
    Call<ArrayList<String>> getSellerNames(@Body ArrayList<CropDetails> cropDetails);

//  get selected crop details
    @GET("buy/{mobileNo}/{cropName}")
    Call<CropDetails> getCropDetails(@Path("mobileNo") String mobileNo, @Path("cropName") String cropName);

//    get crops seller details
    @GET("buy/{mobileNo}")
    Call<FarmerDetails> getFarmerDetails(@Path("mobileNo") String mobileNo);

//    confirm purchasing the product
    @POST("buy")
    Call<Void> requestProduct(@Body RequestDetails requestDetails);

//    get distance between two addresses
    @GET("buy/distance/{origin}/{destination}")
    Call<String> getDistance(@Path("origin") String origin, @Path("destination")String destination);
}
