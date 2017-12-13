package com.gw.kisansewa.api;


import com.gw.kisansewa.models.FarmerDetails;
import com.gw.kisansewa.models.RequestDetails;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RequestAPI {

//    **PURCHASE REQUESTS**

    //    Getting purchase requests of an user
    @GET("purchase/{buyerNo}")
    Call<ArrayList<RequestDetails>> getPurchaseRequests(@Path("buyerNo") String buyerNo);

//    **Requests common to both views**

    //Deleting a purchase request of an user
    @DELETE("delete/{sellerNo}/{buyerNo}/{cropName}")
    Call<Void> cancelRequest(@Path("sellerNo") String sellerNo, @Path("buyerNo") String buyerNo,
                                        @Path("cropName") String cropName);

    //    Getting Farmer details
    @GET("view/{farmerNo}")
    Call<FarmerDetails> getFarmerDetails(@Path("farmerNo") String farmerNo);


//    **SELL REQUESTS**

    // Getting all the sell requests of a particular user
    @GET("sell/{sellerMobileNo}")
    Call<ArrayList<RequestDetails>> getSellRequests(@Path("sellerMobileNo") String sellerMobileNo);

    // Getting all buyer names
    @POST("sell/buyers")
    Call<ArrayList<String>> getBuyerNames(@Body ArrayList<String> buyerMobileNos);

}