package com.gw.kisansewa.orders.purchased;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gw.kisansewa.R;
import com.gw.kisansewa.models.FarmerDetails;
import com.gw.kisansewa.models.Orders;

import java.util.ArrayList;

public class PurchasedFragmentAdapter extends RecyclerView.Adapter<PurchasedFragmentAdapter.PurchasedFragmentHolder>
{
    private Context context;
    private ArrayList<Orders> orders;
    private ArrayList<FarmerDetails> farmers;

    public PurchasedFragmentAdapter(Context context, ArrayList<Orders> orders, ArrayList<FarmerDetails> farmers) {
        this.context = context;
        this.orders = orders;
        this.farmers = farmers;
    }

    @Override
    public PurchasedFragmentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new PurchasedFragmentHolder(view);
    }

    @Override
    public void onBindViewHolder(PurchasedFragmentHolder holder, int position) {
        holder.vh_cropName.setText(orders.get(position).getCropName());
        holder.vh_cropQuantity.setText(orders.get(position).getCropQuantity());
        holder.vh_cropPrice.setText(orders.get(position).getCropPrice());
        holder.vh_name.setText(farmers.get(position).getName());
        holder.vh_mobileNo.setText(farmers.get(position).getMobileNo());
        holder.vh_address.setText(farmers.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class PurchasedFragmentHolder extends RecyclerView.ViewHolder{
        TextView vh_cropName, vh_cropPrice,vh_cropQuantity,
                    vh_name, vh_mobileNo, vh_address,
                        vh_call, vh_message;

        public PurchasedFragmentHolder(View view)
        {
            super(view);
            vh_cropName= (TextView)view.findViewById(R.id.order_cropName);
            vh_cropPrice = (TextView)view.findViewById(R.id.order_cropPrice);
            vh_cropQuantity= (TextView)view.findViewById(R.id.order_cropQuantity);
            vh_name= (TextView)view.findViewById(R.id.order_farmerName);
            vh_mobileNo= (TextView)view.findViewById(R.id.order_farmerNo);
            vh_address= (TextView)view.findViewById(R.id.order_farmerAddress);
            vh_call= (TextView)view.findViewById(R.id.call_farmer);
            vh_message= (TextView)view.findViewById(R.id.message_farmer);

            vh_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(Intent.ACTION_DIAL,
                            Uri.fromParts("tel", vh_mobileNo.getText().toString(), null)));
                }
            });

            vh_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.fromParts("sms", vh_mobileNo.getText().toString(), null)));
                }
            });
        }
    }
}
