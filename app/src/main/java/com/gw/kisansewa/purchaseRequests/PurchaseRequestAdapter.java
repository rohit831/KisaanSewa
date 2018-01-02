package com.gw.kisansewa.purchaseRequests;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gw.kisansewa.R;
import com.gw.kisansewa.api.RequestAPI;
import com.gw.kisansewa.apiGenerator.RequestGenerator;
import com.gw.kisansewa.models.FarmerDetails;
import com.gw.kisansewa.models.RequestDetails;

import java.util.ArrayList;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                    cancelRequestClicked();
                }
            });

            vh_viewSeller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewSellerClicked();
                }
            });
        }

        void viewSellerClicked()
        {
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

            RequestAPI requestAPI = RequestGenerator.createService(RequestAPI.class);
            Call<FarmerDetails> viewFarmerCall = requestAPI.getFarmerDetails(requestDetails.get(position).getSellerMobileNo());
            viewFarmerCall.enqueue(new Callback<FarmerDetails>() {
                @Override
                public void onResponse(Call<FarmerDetails> call, Response<FarmerDetails> response) {
                    if(response.code() == 200){
                        FarmerDetails farmer = response.body();
                        sName.setText(farmer.getName());
                        sMobileNo.setText(farmer.getMobileNo());
                        String area = farmer.getArea();
                        String city = farmer.getCity();
                        String state = farmer.getState();
                        sAddress.setText(area.concat(", ").concat(city).concat(", ")
                                .concat(state));

                        sCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel"
                                        , sMobileNo.getText().toString(), null)));
                            }
                        });

                        sMessage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms",
                                        sMobileNo.getText().toString(), null)));
                            }
                        });
                        customDialog.create();
                        customDialog.show();
                    }
                }

                @Override
                public void onFailure(Call<FarmerDetails> call, Throwable t) {
                    ((PurchaseRequest)context).showSnack(context.getString(R.string.cannot_connect_to_server));
                }
            });

        }

        void cancelRequestClicked()
        {
            final int position = getAdapterPosition();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getString(R.string.request_sure_to_cancel));
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RequestAPI requestAPI = RequestGenerator.createService(RequestAPI.class);
                    Call<Void> cancelRequestCall = requestAPI.cancelRequest(requestDetails.get(position).getSellerMobileNo(),
                            requestDetails.get(position).getBuyerMobileNo(),
                            requestDetails.get(position).getCropName());
                    cancelRequestCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response){
                            if(response.code()==200){
                                requestDetails.remove(position);
                                if(requestDetails.size() == 0)
                                {
                                    ((PurchaseRequest)context).noRequestsCurrently();
                                }
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, requestDetails.size());
                            }
                            else {
                                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            ((PurchaseRequest)context).showSnack(context.getString(R.string.check_network_connection));
                        }
                    });
                }
            });

            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}
