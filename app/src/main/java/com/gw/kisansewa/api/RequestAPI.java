package com.gw.kisansewa.api;


import com.gw.kisansewa.models.FarmerDetails;
import com.gw.kisansewa.models.RequestDetails;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RequestAPI {


    //    Getting purchase requests of an user
    @GET("purchase/{buyerNo}")
    Call<ArrayList<RequestDetails>> getPurchaseRequests(@Path("buyerNo") String buyerNo);

    //    Getting Seller details
    @GET("viewseller/{sellerNo}")
    Call<FarmerDetails> getSellerDetails(@Path("sellerNo") String sellerNo);


    //Deleting a purchase request of an user
    @DELETE("purchase/{sellerNo}/{buyerNo}/{cropName}")
    Call<Void> cancelRequest(@Path("sellerNo") String sellerNo, @Path("buyerNo") String buyerNo,
                                        @Path("cropName") String cropName);

}