package com.gw.kisansewa.purchaseRequests;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gw.kisansewa.R;
import com.gw.kisansewa.models.CropDetails;
import com.gw.kisansewa.models.RequestDetails;

import java.util.ArrayList;

public class PurchaseRequestAdapter extends RecyclerView.Adapter<PurchaseRequestAdapter.PurchaseRequestViewHolder>
{
    ArrayList<RequestDetails> requestDetails;
    Context context;

    public PurchaseRequestAdapter(ArrayList<RequestDetails> requestDetails, Context context) {
        this.requestDetails = requestDetails;
        this.context = context;
    }

    @Override
    public PurchaseRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_request_item, parent, false);
        return new PurchaseRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PurchaseRequestViewHolder holder, int position) {
        holder.vh_cropName.setText(requestDetails.get(position).getCropName());
        holder.vh_cropQuantity.setText(requestDetails.get(position).getCropQuantity());
        holder.vh_cropPrice.setText(requestDetails.get(position).getCropPrice());
    }

    @Override
    public int getItemCount() {
        return requestDetails.size();
    }

    public  class PurchaseRequestViewHolder extends RecyclerView.ViewHolder{
        TextView vh_cropName, vh_cropPrice, vh_cropQuantity, vh_viewSeller, vh_cancelRequest;
        public PurchaseRequestViewHolder(View view) {
            super(view);
            vh_cropName = (TextView)view.findViewById(R.id.cropName_purchaseRequest);
            vh_cropPrice = (TextView)view.findViewById(R.id.cropPrice_purchaseRequest);
            vh_cropQuantity= (TextView)view.findViewById(R.id.cropQuantity_purchaseRequest);
            vh_viewSeller= (TextView)view.findViewById(R.id.viewSeller_purchaseRequest);
            vh_cancelRequest= (TextView)view.findViewById(R.id.cancelRequest_purchaseRequest);

            vh_cancelRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelRequestClicked(v);
                }
            });

            vh_viewSeller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewSellerClicked(v);
                }
            });
        }

        void viewSellerClicked(View v)
        {
            Toast.makeText(context, "Clicked View Seller", Toast.LENGTH_SHORT).show();
            LayoutInflater li = LayoutInflater.from(context);
            final View dialogView = li.inflate(R.layout.seller_detail, null);
            final AlertDialog.Builder customDialog = new AlertDialog.Builder(context);
            customDialog.setView(dialogView);
            int position = getAdapterPosition();

//            Referencing all the elements
            final TextView sName, sMobileNo, sAddress, sCall, sMessage;
            sName = (TextView)dialogView.findViewById(R.id.sellerName_sellerDetail);
            sMobileNo = (TextView)dialogView.findViewById(R.id.sellerNo_sellerDetail);
            sAddress = (TextView)dialogView.findViewById(R.id.sellerAddress_sellerDetail);
            sCall = (TextView)dialogView.findViewById(R.id.call_sellerDetail);
            sMessage = (TextView)dialogView.findViewById(R.id.message_sellerDetail);


        }

        void cancelRequestClicked(View v)
        {
            Toast.makeText(context, "Cancel Request clicked", Toast.LENGTH_SHORT).show();
        }
    }
}
