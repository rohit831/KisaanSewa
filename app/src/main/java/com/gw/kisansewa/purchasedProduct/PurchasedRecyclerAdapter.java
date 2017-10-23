package com.gw.kisansewa.purchasedProduct;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gw.kisansewa.R;
import com.gw.kisansewa.models.CropDetails;
import com.gw.kisansewa.models.FarmerDetails;

import java.util.ArrayList;

public class PurchasedRecyclerAdapter extends RecyclerView.Adapter<PurchasedRecyclerAdapter.PurchasedRecyclerViewHolder>
{

    ArrayList<CropDetails> cropDetails;
    ArrayList<FarmerDetails> farmerDetails;
    Context context;
    String userMobileNo;

    public PurchasedRecyclerAdapter(ArrayList<CropDetails> cropDetails, ArrayList<FarmerDetails> farmerDetails, Context context, String userMobileNo) {
        this.cropDetails = cropDetails;
        this.farmerDetails = farmerDetails;
        this.context = context;
        this.userMobileNo = userMobileNo;
    }


    @Override
    public PurchasedRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_layout,parent,false);

        PurchasedRecyclerViewHolder holder=new PurchasedRecyclerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PurchasedRecyclerViewHolder holder, int position) {
        holder.vh_sellerState.setText(farmerDetails.get(position).getState());
        holder.vh_sellerCity.setText(farmerDetails.get(position).getCity());
        holder.vh_sellerLocality.setText(farmerDetails.get(position).getArea());
        holder.vh_sellerMobileNo.setText(farmerDetails.get(position).getMobileNo());
        holder.vh_sellerName.setText(farmerDetails.get(position).getName());

        holder.vh_ProductName.setText(cropDetails.get(position).getCropName());
        holder.vh_ProductPrice.setText(cropDetails.get(position).getCropPrice());
        holder.vh_ProductQuantity.setText(cropDetails.get(position).getCropQuantity());
    }

    @Override
    public int getItemCount() {
        return cropDetails.size();
    }

    public class PurchasedRecyclerViewHolder extends RecyclerView.ViewHolder

    {
        TextView vh_ProductName,vh_ProductQuantity,vh_sellerName,vh_ProductPrice,vh_sellerMobileNo;
        TextView vh_sellerLocality,vh_sellerCity,vh_sellerState;

        public PurchasedRecyclerViewHolder(View itemView) {
            super(itemView);
            vh_ProductName=(TextView)itemView.findViewById(R.id.purchasedProductName);
            vh_ProductQuantity=(TextView)itemView.findViewById(R.id.purchasedProductQuantity);
            vh_sellerName=(TextView)itemView.findViewById(R.id.purchasedSellerName);
            vh_ProductPrice=(TextView)itemView.findViewById(R.id.purchasedProductPrice);
            vh_sellerMobileNo=(TextView)itemView.findViewById(R.id.purchasedSellerMobileNo);
            vh_sellerLocality=(TextView)itemView.findViewById(R.id.purchasedSellerLocality);
            vh_sellerCity=(TextView)itemView.findViewById(R.id.purchasedSellerCity);
            vh_sellerState=(TextView)itemView.findViewById(R.id.purchasedSellerState);
        }
    }

}
