//package com.gw.kisansewa;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.util.DisplayMetrics;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//import android.widget.TextView;
//
//import com.gw.kisansewa.authentication.FarmerLogin;
//import com.gw.kisansewa.buyProduct.BuyProducts;
//import com.gw.kisansewa.purchasedProduct.Purchased;
//import com.gw.kisansewa.sellProduct.SellProducts;
//import com.gw.kisansewa.soldProduct.Sold;
//
//public class HomeScreen extends AppCompatActivity {
//
//    private Button buyProducts;
//    private Button sellProducts;
//    private Button marketStatus;
//    private TextView optionsButton;
//    final Context context=this;
//    private PopupWindow popupWindow;
//    private LayoutInflater inflater;
//    LinearLayout linearLayout;
//    String userMobileNo=new String();
//
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.home_screen);
//        initialize();
//    }
//
//    public void initialize()
//    {
//        buyProducts=(Button)findViewById(R.id.buyProducts);
//        sellProducts=(Button)findViewById(R.id.sellProducts);
//        marketStatus=(Button)findViewById(R.id.marketStatus);
//        optionsButton=(TextView)findViewById(R.id.optionsButton);
//        linearLayout=(LinearLayout)findViewById(R.id.homeScreen);
//        userMobileNo=getIntent().getStringExtra("mobileNo");
//
//    }
//
//    public void buyProductsOnClick(View view)
//    {
//        Intent intent=new Intent(this,BuyProducts.class);
//        intent.putExtra("mobileNo",userMobileNo);
//        startActivity(intent);
//    }
//
//    public void sellProductsOnclick(View view)
//    {
//        Intent intent=new Intent(this,SellProducts.class);
//        intent.putExtra("mobileNo",userMobileNo);
//        startActivity(intent);
//    }
//    public void marketStatusOnClick(View view)
//    {
//        Intent browserIntent= new Intent(Intent.ACTION_VIEW, Uri.parse("http://farmer.gov.in/marketprice.html"));
//
//        startActivity(browserIntent);
//    }
//
//    public void optionsButtonOnClick(View view)
//    {
//
//
//        inflater=(LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//        final ViewGroup container=(ViewGroup)inflater.inflate(R.layout.pop_up_window,null);
//
//        DisplayMetrics displayMetrics=new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height=displayMetrics.heightPixels;
//        int width=displayMetrics.widthPixels;
//
//        popupWindow=new PopupWindow(container,(int)(width*.45),(int)(height*.25));
//        popupWindow.showAtLocation(linearLayout, Gravity.TOP | Gravity.RIGHT,(int)(width*0.0),(int)(height*0.13));
//
//
//        linearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//            }
//        });
//
//        TextView purchasedBtn,logOutBtn,soldBtn;
//        purchasedBtn=(TextView)container.findViewById(R.id.purchasedBtn);
//        logOutBtn=(TextView)container.findViewById(R.id.logOutBtn);
//        soldBtn=(TextView)container.findViewById(R.id.soldBtn);
//
//        purchasedBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent=new Intent(context,Purchased.class);
//                intent.putExtra("mobileNo",userMobileNo);
//                startActivity(intent);
//                popupWindow.dismiss();
//            }
//        });
//
//        logOutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                SharedPreferences sharedPreferences=getSharedPreferences(FarmerLogin.FarmerPreferences,Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor=sharedPreferences.edit();
//                editor.clear();
//                editor.commit();
//
//                Intent intent=new Intent(context,FarmerLogin.class);
//                startActivity(intent);
//                finish();
//                popupWindow.dismiss();
//            }
//        });
//
//        soldBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(context,Sold.class);
//                intent.putExtra("mobileNo",userMobileNo);
//                startActivity(intent);
//                popupWindow.dismiss();
//            }
//        });
//
//        container.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                popupWindow.dismiss();
//                return true;
//            }
//        });
//    }
//}
