package com.gw.kisansewa.buyCropsTab;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gw.kisansewa.R;
import com.gw.kisansewa.buyCropsTab.cropDetail.CropDetail;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BuyCropsFragmentAdapter extends RecyclerView.Adapter<BuyCropsFragmentAdapter.BuyCropsViewHolder>{

    private Context context;
    private ArrayList<CropsModel> crops;

    public BuyCropsFragmentAdapter(Context context, ArrayList<CropsModel> crops) {
        this.context = context;
        this.crops = crops;
    }

    @Override
    public BuyCropsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.buy_crops_tab_item,viewGroup, false);
        return new BuyCropsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BuyCropsViewHolder holder, int position) {
        holder.vh_cropName.setText(crops.get(position).getCropName());
        if(crops.get(position).getSellerQuantity() == 0)
            holder.vh_sellerQuantity.setText(R.string.no_sellers);
        else if(crops.get(position).getSellerQuantity() == 1 )
            holder.vh_sellerQuantity.setText(String.valueOf(crops.get(position).getSellerQuantity()).concat(" ").concat(context.getString(R.string.seller)));
        else
            holder.vh_sellerQuantity.setText(String.valueOf(crops.get(position).getSellerQuantity()).concat(" ").concat(context.getString(R.string.sellers)));

        Picasso.with(context)
                .load(crops.get(position).getImageLink())
                .into(holder.vh_cropImage);
    }

    @Override
    public int getItemCount() {
        return crops.size();
    }

    class BuyCropsViewHolder extends RecyclerView.ViewHolder {

        TextView vh_cropName, vh_sellerQuantity;
        ImageView vh_cropImage;
        BuyCropsViewHolder(View view) {
            super(view);
            vh_cropImage = view.findViewById(R.id.item_buy_crop_image);
            vh_cropName = view.findViewById(R.id.item_buy_crop_name);
            vh_sellerQuantity = view.findViewById(R.id.item_buy_crop_sellers);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CropDetail.class);
                    intent.putExtra("cropName",crops.get(getAdapterPosition()).getCropName());
                    intent.putExtra("imageLink", crops.get(getAdapterPosition()).getImageLink());
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}
