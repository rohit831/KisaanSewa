package com.gw.kisansewa.api;


import com.gw.kisansewa.models.FarmerDetails;
import com.gw.kisansewa.models.Orders;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OrderAPI {


//    **PURCHASED**
//    get all purchased orders
    @GET("purchased/{buyerNo}")
    Call<ArrayList<Orders>> getPurchasedOrders(@Path("buyerNo") String buyerNo);

//    get all seller details
    @GET("purchased/sellers/{buyerNo}")
    Call<ArrayList<FarmerDetails>> getSellerDetails(@Path("buyerNo") String buyerNo);

//    **SOLD**
//    get all sold orders
    @GET("sold/{sellerNo}")
    Call<ArrayList<Orders>> getSoldOrders(@Path("sellerNo") String sellerNo);

//    get all buyer details
    @GET("sold/buyers/{sellerNo}")
    Call<ArrayList<FarmerDetails>> getBuyerDetails(@Path("sellerNo") String sellerNo);

}
