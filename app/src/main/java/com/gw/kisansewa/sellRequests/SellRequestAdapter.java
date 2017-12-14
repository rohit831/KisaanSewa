package com.gw.kisansewa.sellRequests;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gw.kisansewa.R;
import com.gw.kisansewa.api.RequestAPI;
import com.gw.kisansewa.apiGenerator.RequestGenerator;
import com.gw.kisansewa.models.FarmerDetails;
import com.gw.kisansewa.models.Orders;
import com.gw.kisansewa.models.RequestDetails;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellRequestAdapter extends RecyclerView.Adapter<SellRequestAdapter.SellRequestHolder>{

    ArrayList<RequestDetails> requestDetails;
    ArrayList<String> buyerNames;
    Context context;

    public SellRequestAdapter(ArrayList<RequestDetails> requestDetails,ArrayList<String> buyerNames, Context context) {
        this.requestDetails = requestDetails;
        this.buyerNames = buyerNames;
        this.context = context;
    }

    @Override
    public SellRequestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sell_request_item,parent,false);
        return new SellRequestHolder(view);
    }

    @Override
    public void onBindViewHolder(SellRequestHolder holder, int position) {
        holder.vh_buyerName.setText(buyerNames.get(position));
        holder.vh_cropName.setText(requestDetails.get(position).getCropName());
        holder.vh_quantity.setText(requestDetails.get(position).getCropQuantity());
        holder.vh_price.setText(requestDetails.get(position).getCropPrice());
    }

    @Override
    public int getItemCount() {
        return requestDetails.size();
    }

    public class SellRequestHolder extends RecyclerView.ViewHolder
    {
        TextView vh_buyerName, vh_cropName,vh_quantity, vh_price,
                vh_view_buyer, vh_cancel_request, vh_confirm_request;

        public SellRequestHolder(View view) {
            super(view);
            vh_buyerName = (TextView)view.findViewById(R.id.buyerName_sellRequest);
            vh_cropName= (TextView)view.findViewById(R.id.cropName_sellRequest);
            vh_quantity= (TextView)view.findViewById(R.id.cropQuantity_sellRequest);
            vh_price= (TextView)view.findViewById(R.id.cropPrice_sellRequest);
            vh_view_buyer= (TextView)view.findViewById(R.id.viewBuyer_sellRequest);
            vh_cancel_request= (TextView)view.findViewById(R.id.cancelRequest_sellRequest);
            vh_confirm_request= (TextView)view.findViewById(R.id.confirmRequest_sellRequest);

            vh_cancel_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {cancelRequestClicked();
                }
            });

            vh_view_buyer.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    viewBuyerClicked();
                }
            });

            vh_confirm_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {confirmRequestClicked();
                }
            });
        }

        // cancel the request of a buyer
        private void cancelRequestClicked(){
            final int position = getAdapterPosition();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setTitle("Cancel Request");
            builder.setMessage("Are you sure you want to cancel this request?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RequestAPI requestAPI = RequestGenerator.createService(RequestAPI.class);
                    Call<Void> cancelRequestCall = requestAPI.cancelRequest(requestDetails.get(position).getSellerMobileNo(),
                            requestDetails.get(position).getBuyerMobileNo(),
                            requestDetails.get(position).getCropName());
                    cancelRequestCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.code()==200){
                                requestDetails.remove(position);
                                buyerNames.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, requestDetails.size());
                            }
                            else {
                                Toast.makeText(context, "Oops Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(context, "Unable to connect to server at the moment", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        //view the buyer
        private void viewBuyerClicked(){
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
            Call<FarmerDetails> viewFarmerCall = requestAPI.getFarmerDetails(requestDetails.get(position).getBuyerMobileNo());
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
                    Toast.makeText(context, "Can't connect to the server at the moment!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //confirm the request
        private void confirmRequestClicked(){
            final int position = getAdapterPosition();
            LayoutInflater inflater = LayoutInflater.from(context);
            final View dialogView = inflater.inflate(R.layout.sell_request_confirm_dialog, null);
            final AlertDialog.Builder customDialog = new AlertDialog.Builder(context);
            customDialog.setView(dialogView);

            final TextView totalQuantity;
            final EditText quantity_purchased;

            totalQuantity = (TextView)dialogView.findViewById(R.id.sell_requests_total_quantity);
            quantity_purchased = (EditText)dialogView.findViewById(R.id.quantity_purchased);

            totalQuantity.setText(requestDetails.get(position).getCropQuantity());

            final Orders order = new Orders(requestDetails.get(position).getSellerMobileNo(),
                    requestDetails.get(position).getBuyerMobileNo(),
                    requestDetails.get(position).getCropName(),
                    quantity_purchased.getText().toString(),
                    requestDetails.get(position).getCropPrice());

            customDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(quantity_purchased.getText().toString().equals("0"))
                        Toast.makeText(context, "Quantity cannot be zero", Toast.LENGTH_SHORT).show();

                    else if(Integer.parseInt(quantity_purchased.getText().toString()) >
                            Integer.parseInt(totalQuantity.getText().toString()))
                        Toast.makeText(context, "Enter a valid quantity", Toast.LENGTH_SHORT).show();
                    else{
                        RequestAPI requestAPI = RequestGenerator.createService(RequestAPI.class);
                        Call<Void> confirmOrderCall = requestAPI.confirmOrder(order);
                        confirmOrderCall.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.code() == 200){
                                    Toast.makeText(context, "Response code 200", Toast.LENGTH_SHORT).show();
                                    requestDetails.remove(position);
                                    buyerNames.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position,requestDetails.size());
                                }
                                else{
                                    Toast.makeText(context, "Response code not 200", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(context, "Can't connect to server at the moment", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

            customDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            customDialog.create();
            customDialog.show();
        }
    }
    public void showDialog(ProgressDialog mProgressDialog) {

        if(mProgressDialog != null && !mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    public void hideDialog(ProgressDialog mProgressDialog) {

        if(mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

}
