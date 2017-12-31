package com.gw.kisansewa.sellProductsTab;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidadvance.topsnackbar.TSnackbar;
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
    LinearLayout snackLayout;


    public  SellProductsRecyclerAdapter(ArrayList<CropDetails> cropDetails, Context context, String userMobileNo
    , LinearLayout snackLayout)
    {
        this.snackLayout = snackLayout;
        this.context=context;
        this.userMobileNo=userMobileNo;
        this.cropDetails=cropDetails;
    }

    @Override
    public SellProductsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout,parent,false);
        return new SellProductsRecyclerViewHolder(view);
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

                    final ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Deleting crop .. ");
                    progressDialog.show();

                    LayoutInflater li=LayoutInflater.from(context);
                    final View dialogView =li.inflate(R.layout.delete_sell_product_dialog,null);
                    final AlertDialog.Builder customDialog=new AlertDialog.Builder(context);
                    customDialog.setView(dialogView);

                    customDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.hide();
                            dialog.cancel();
                        }
                    });

                    customDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            SellProductAPI sellProductAPI = ProductGenerator.createService(SellProductAPI.class);
                            Call<Void> deleteCropCall = sellProductAPI.deleteCrop(userMobileNo, vh_productName.getText().toString());

                            deleteCropCall.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    progressDialog.hide();
                                    if(response.code() == 200){
                                        cropDetails.remove(getAdapterPosition());
                                        notifyItemRemoved(getAdapterPosition());
                                        notifyItemRangeChanged(getAdapterPosition(),cropDetails.size());
                                        showSnack("Item Deleted");
                                    }
                                    else{
                                        showSnack("Something went  wrong!");
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    progressDialog.hide();
                                    showSnack("Unable to connect to server at the moment");
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
                                showSnack("Field cannot be empty!!");
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
                                        showSnack("Unable to connect to server!");
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

        void showSnack(String message) {
            TSnackbar snack = TSnackbar.make(snackLayout,message,TSnackbar.LENGTH_SHORT );
            View snackView = snack.getView();
            snackView.setBackgroundColor(Color.rgb(34,142,17));
            TextView textView = (TextView)snackView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snack.show();
        }
    }

}
