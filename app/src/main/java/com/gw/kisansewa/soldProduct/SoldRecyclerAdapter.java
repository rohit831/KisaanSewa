package com.gw.kisansewa.soldProduct;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gw.kisansewa.R;
import com.gw.kisansewa.models.CropDetails;

import java.util.ArrayList;


public class SoldRecyclerAdapter extends RecyclerView.Adapter<SoldRecyclerAdapter.SoldRecyclerViewHolder> {

    ArrayList<CropDetails> cropDetails;
    ArrayList<String> buyerNames;
    Context context;
    String userMobileNo;

    public SoldRecyclerAdapter(ArrayList<CropDetails> cropDetails, ArrayList<String> buyerNames, Context context, String userMobileNo) {
        this.cropDetails = cropDetails;
        this.buyerNames = buyerNames;
        this.context = context;
        this.userMobileNo = userMobileNo;
    }

    @Override
    public SoldRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.sold_layout,parent,false);
        SoldRecyclerViewHolder holder=new SoldRecyclerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SoldRecyclerViewHolder holder, int position) {

        holder.vh_buyerMobileNo.setText(cropDetails.get(position).getMobileNo());
        holder.vh_buyerName.setText(buyerNames.get(position));
        holder.vh_ProductName.setText(cropDetails.get(position).getCropName());
        holder.vh_ProductPrice.setText(cropDetails.get(position).getCropPrice());
        holder.vh_ProductQuantity.setText(cropDetails.get(position).getCropQuantity());
    }

    @Override
    public int getItemCount() {
        return cropDetails.size();
    }

    public class SoldRecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView vh_ProductName,vh_ProductQuantity,vh_buyerName,vh_ProductPrice,vh_buyerMobileNo;

        public SoldRecyclerViewHolder(View itemView) {
            super(itemView);
            vh_ProductName=(TextView)itemView.findViewById(R.id.soldProductName);
            vh_ProductQuantity=(TextView)itemView.findViewById(R.id.soldProductQuantity);
            vh_buyerName=(TextView)itemView.findViewById(R.id.soldBuyerName);
            vh_ProductPrice=(TextView)itemView.findViewById(R.id.soldProductPrice);
            vh_buyerMobileNo=(TextView)itemView.findViewById(R.id.soldBuyerMobileNo);

        }
    }
}
