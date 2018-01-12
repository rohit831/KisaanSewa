package com.gw.kisansewa.buyCropsTab.cropDetail;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gw.kisansewa.R;
import com.gw.kisansewa.buyProductsTab.ConfirmProductBuy;

import java.util.ArrayList;

public class CropDetailAdapter extends RecyclerView.Adapter<CropDetailAdapter.CropDetailHolder> {

    private Context context;
    private ArrayList<CropDetailModel> cropDetails;
    private String productName;

    public CropDetailAdapter(Context context,String productName, ArrayList<CropDetailModel> cropDetails) {
        this.context = context;
        this.cropDetails = cropDetails;
        this.productName = productName;
    }

    @Override
    public CropDetailHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.crop_detail_item, parent, false);
        return new CropDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(CropDetailHolder holder, int position) {
        holder.vh_city.setText(cropDetails.get(position).getCity());
        holder.vh_name.setText(cropDetails.get(position).getName());
        holder.vh_price.setText(cropDetails.get(position).getPrice());
    }

    @Override
    public int getItemCount() {
        return cropDetails.size();
    }

    class CropDetailHolder extends RecyclerView.ViewHolder{
        TextView vh_name, vh_price, vh_city;
        private CropDetailHolder(View view) {
            super(view);

            vh_name = view.findViewById(R.id.item_crop_detail_name);
            vh_price = view.findViewById(R.id.item_crop_detail_price);
            vh_city = view.findViewById(R.id.item_crop_detail_city);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent =new Intent(context,ConfirmProductBuy.class);
                    intent.putExtra("sellerMobileNo",cropDetails.get(getAdapterPosition()).getMobileNo());
                    intent.putExtra("productName",productName);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}
