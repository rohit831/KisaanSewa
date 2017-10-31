package com.gw.kisansewa.SellProductsTab;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gw.kisansewa.R;
import com.gw.kisansewa.api.SellProductAPI;
import com.gw.kisansewa.apiGenerator.ProductGenerator;
import com.gw.kisansewa.models.CropDetails;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellProductsRecyclerAdapter extends RecyclerView.Adapter<SellProductsRecyclerAdapter.SellProductsRecyclerViewHolder> {

    ArrayList<CropDetails> cropDetails;
    Context context;
    String userMobileNo;


    public  SellProductsRecyclerAdapter(ArrayList<CropDetails> cropDetails,Context context,String userMobileNo)
    {
        this.context=context;
        this.userMobileNo=userMobileNo;
        this.cropDetails=cropDetails;
    }

    @Override
    public SellProductsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout,parent,false);
        SellProductsRecyclerViewHolder holder=new SellProductsRecyclerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SellProductsRecyclerViewHolder holder, int position) {
        holder.vh_productName.setText(cropDetails.get(position).getCropName());
        holder.vh_productQuantity.setText(cropDetails.get(position).getCropQuantity().concat(" kgs remaining"));
        holder.vh_productPrice.setText(cropDetails.get(position).getCropPrice());
    }

    @Override
    public int getItemCount() {
        return cropDetails.size();
    }



    public  class SellProductsRecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView vh_productName,vh_productPrice,vh_productQuantity;
        public SellProductsRecyclerViewHolder(final View view)
        {
            super(view);
            vh_productName=(TextView)view.findViewById(R.id.productName);
            vh_productPrice=(TextView)view.findViewById(R.id.productPrice);
            vh_productQuantity=(TextView)view.findViewById(R.id.productQuantity);

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {



                    LayoutInflater li=LayoutInflater.from(context);
                    final View dialogView =li.inflate(R.layout.delete_sell_product_dialog,null);
                    final AlertDialog.Builder customDialog=new AlertDialog.Builder(context);
                    customDialog.setView(dialogView);

                    customDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    customDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context,"Item Deleted",Toast.LENGTH_SHORT).show();

                            SellProductAPI sellProductAPI = ProductGenerator.createService(SellProductAPI.class);
                            Call<Void> deleteCropCall = sellProductAPI.deleteCrop(userMobileNo, vh_productName.getText().toString());

                            deleteCropCall.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if(response.code() == 200){
                                        cropDetails.remove(getAdapterPosition());
                                        notifyItemRemoved(getAdapterPosition());
                                        notifyItemRangeChanged(getAdapterPosition(),cropDetails.size());
                                    }
                                    else{
                                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(context, "Unable to connect to server!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    customDialog.create();
                    customDialog.show();

                    return true;
                }
            });


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater li=LayoutInflater.from(context);
                    final View dialogView =li.inflate(R.layout.edit_product_dialog,null);
                    final AlertDialog.Builder customDialog =new AlertDialog.Builder(context);
                    customDialog.setView(dialogView);
                    int position =getAdapterPosition();

                    final EditText editProductName,editProductQuantity,editProductPrice;
                    editProductName=(EditText)dialogView.findViewById(R.id.editProductName);
                    editProductPrice=(EditText)dialogView.findViewById(R.id.editProductPrice);
                    editProductQuantity=(EditText)dialogView.findViewById(R.id.editProductQuantity);

                    editProductName.setText(cropDetails.get(position).getCropName());
                    editProductPrice.setText(cropDetails.get(position).getCropPrice());
                    editProductQuantity.setText(cropDetails.get(position).getCropQuantity());

                    customDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    customDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(editProductName.getText().toString().equals("") || editProductPrice.getText().toString().equals("")
                                    || editProductQuantity.getText().toString().equals(""))
                                Toast.makeText(context,"Field cannot be empty!!",Toast.LENGTH_SHORT).show();
                            else
                            {
                                SellProductAPI sellProductAPI = ProductGenerator.createService(SellProductAPI.class);
                                CropDetails cropDetails = new CropDetails(editProductName.getText().toString(),
                                        editProductQuantity.getText().toString(), editProductPrice.getText().toString(),
                                        userMobileNo);
                                Call<Void> editCropCall = sellProductAPI.editCrop(userMobileNo, vh_productName.getText().toString(),cropDetails );
                                editCropCall.enqueue(new Callback<Void>() {

                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if(response.code() == 200) {
                                            notifyItemChanged(getAdapterPosition());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(context, "Unable to connect to server!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });

                    customDialog.create();
                    customDialog.show();
                }
            });

        }
    }
}
