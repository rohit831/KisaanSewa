package com.gw.kisansewa;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gw.kisansewa.models.CropDetails;
import com.gw.kisansewa.models.FarmerDetails;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="KisaanSewa.db";
    private static final String TABLE1_FARMER="Farmer";
    private static final String COLUMN_ID="_id";
    private static final String COLUMN_NAME="Name";
    private static final String COLUMN_MOBILENO= "Mobile_No";
    private static final String COLUMN_AREA = "Area";
    private static final String COLUMN_CITY = "City";
    private static final String COLUMN_STATE = "State";
    private static final String COLUMN_PASSWORD = "Password";

    private static final String TABLE2_CROP="Crop";
    private static final String COLUMN_CROP_NAME="Crop_Name";
    private static final String COLUMN_CROP_PRICE= "Crop_Price";
    private static final String COLUMN_CROP_QUANTITY="Crop_Quantity";

    private  String TABLE3_PURCHASE=new String();
    private static final String COLUMN_USER_MOBILENO= "Mobile_no";
    private static final String COLUMN_SELLER_MOBILENO="Seller_Mobile_No";
    private static final String COLUMN_QUANTITY_REQUIRED= "Quantity_required";

    private String TABLE4_SOLD=new String();
    private static final String COLUMN_CUSTOMER_MOBILENO="Customer_Mobile_No";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,DATABASE_NAME, factory,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query1= "CREATE TABLE " + TABLE1_FARMER + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, " + COLUMN_MOBILENO + " TEXT, " + COLUMN_AREA + " TEXT, "
                +  COLUMN_CITY + " TEXT, " + COLUMN_STATE + " TEXT, " + COLUMN_PASSWORD +
                " TEXT );";
        db.execSQL(query1);

        String query2= "CREATE TABLE " + TABLE2_CROP +" ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MOBILENO + " TEXT, " + COLUMN_CROP_NAME + " TEXT, " + COLUMN_CROP_PRICE +
                " TEXT, " + COLUMN_CROP_QUANTITY + " INTEGER );";
        db.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE1_FARMER + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2_CROP + ";" );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE3_PURCHASE + " ;");
        onCreate(db);
    }

    public void createPurchaseTable(String userMobileNo)
    {
        TABLE3_PURCHASE = "PURCHASE_" + userMobileNo;
        SQLiteDatabase db=getWritableDatabase();
        String query= "CREATE TABLE " + TABLE3_PURCHASE + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_MOBILENO + " TEXT, " + COLUMN_SELLER_MOBILENO + " TEXT, " +
                COLUMN_CROP_NAME + " TEXT, " + COLUMN_QUANTITY_REQUIRED + " TEXT, " + COLUMN_CROP_PRICE
                 + " TEXT );";

        db.execSQL(query);
    }

    public void  createSoldTable(String userMobileNo)
    {
        TABLE4_SOLD="SOLD_"+ userMobileNo;
        SQLiteDatabase db=getWritableDatabase();
        String query= "CREATE TABLE " + TABLE4_SOLD + " ( "  + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_MOBILENO + " TEXT, " + COLUMN_CUSTOMER_MOBILENO + " TEXT, " +
                COLUMN_CROP_NAME + " TEXT, " + COLUMN_QUANTITY_REQUIRED + " TEXT, " + COLUMN_CROP_PRICE
                + " TEXT );";
        db.execSQL(query);
    }

    public long addNewFarmerRecord(FarmerDetails farmerDetails)
    {
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values=new ContentValues();

        values.put(COLUMN_NAME, farmerDetails.getName());
        values.put(COLUMN_AREA,farmerDetails.getArea());
        values.put(COLUMN_CITY,farmerDetails.getCity());
        values.put(COLUMN_STATE,farmerDetails.getState());
        values.put(COLUMN_MOBILENO,farmerDetails.getMobileNo());
        values.put(COLUMN_PASSWORD,farmerDetails.getPassword());

        return db.insert(TABLE1_FARMER,null,values);
    }

    public Cursor searchFarmerLogin(String mobileNo,String password)
    {
        Cursor cursor=null;
        SQLiteDatabase db=this.getReadableDatabase();

        String query=  "SELECT * FROM " + TABLE1_FARMER + " WHERE " + COLUMN_MOBILENO + " = '"
                + mobileNo + "' AND " + COLUMN_PASSWORD + " = '" + password + "' ; ";

        cursor= db.rawQuery(query,null);
        return cursor;
    }

    public long addNewProduct(CropDetails cropDetails)
    {
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_MOBILENO,cropDetails.getMobileNo());
        values.put(COLUMN_CROP_NAME,cropDetails.getCropName());
        values.put(COLUMN_CROP_QUANTITY,cropDetails.getCropQuantity());
        values.put(COLUMN_CROP_PRICE,cropDetails.getCropPrice());

        return db.insert(TABLE2_CROP,null,values);
    }

    public ArrayList<CropDetails> retrieveSellerCrops(String mobileNo)
    {
        ArrayList<CropDetails> cropDetails=new ArrayList<CropDetails>();

        SQLiteDatabase db=this.getReadableDatabase();

        String query="SELECT * FROM "+ TABLE2_CROP + " WHERE " +
                COLUMN_MOBILENO + " = '" + mobileNo + "' ;";

        Cursor cursor=null;

        cursor=db.rawQuery(query,null);


            while (cursor.moveToNext()) {
                CropDetails crop=new CropDetails();
                crop.setCropName(cursor.getString(cursor.getColumnIndex(COLUMN_CROP_NAME)));
                crop.setCropPrice(cursor.getString(cursor.getColumnIndex(COLUMN_CROP_PRICE)));
                crop.setCropQuantity(cursor.getString(cursor.getColumnIndex(COLUMN_CROP_QUANTITY)));
                crop.setMobileNo(mobileNo);

                cropDetails.add(crop);
            }

            return cropDetails;
    }

    public String getName(String mobileNo)
    {
        SQLiteDatabase db= getReadableDatabase();
        String name=new String();

        String query="SELECT " + COLUMN_NAME + " FROM " + TABLE1_FARMER + " WHERE " + COLUMN_MOBILENO
                + " = '" + mobileNo + "' ;";

        Cursor cursor=null;
        cursor=db.rawQuery(query,null);
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        }
        return name;
    }

    public ArrayList<String> getSellerNames(ArrayList<CropDetails> cropDetails)
    {
        ArrayList<String> sellerNames=new ArrayList<String>();
        SQLiteDatabase db=getReadableDatabase();
        int count=0;
        while(count< cropDetails.size())
        {
            String sellerName=new String();
            sellerName=  getName((cropDetails.get(count)).getMobileNo());
            sellerNames.add(sellerName);
            count++;
        }
        return sellerNames;
    }

    public ArrayList<CropDetails> getCropsAvailable()
    {
        ArrayList<CropDetails> cropDetails=new ArrayList<CropDetails>();
        SQLiteDatabase db=getReadableDatabase();

        String query = " SELECT * FROM " + TABLE2_CROP + " ;";
        Cursor cursor=null;
        cursor=db.rawQuery(query,null);

        while (cursor.moveToNext())
        {
            CropDetails crop=new CropDetails();
            crop.setMobileNo(cursor.getString(cursor.getColumnIndex(COLUMN_MOBILENO)));
            crop.setCropPrice(cursor.getString(cursor.getColumnIndex(COLUMN_CROP_PRICE)));
            crop.setCropQuantity(cursor.getString(cursor.getColumnIndex(COLUMN_CROP_QUANTITY)));
            crop.setCropName(cursor.getString(cursor.getColumnIndex(COLUMN_CROP_NAME)));

            cropDetails.add(crop);
        }

        return cropDetails;
    }

    public CropDetails getCropDetails(String mobileNo, String productName)
    {
        CropDetails crop=new CropDetails();
        SQLiteDatabase db=getReadableDatabase();

        String query= "SELECT * FROM " + TABLE2_CROP + " WHERE " + COLUMN_MOBILENO +
                 " = '" + mobileNo + "'  AND " + COLUMN_CROP_NAME + " = '" +
                productName + "' ;";
        Cursor cursor=null;
        cursor=db.rawQuery(query,null);

        while (cursor.moveToNext())
        {
            crop.setMobileNo(cursor.getString(cursor.getColumnIndex(COLUMN_MOBILENO)));
            crop.setCropPrice(cursor.getString(cursor.getColumnIndex(COLUMN_CROP_PRICE)));
            crop.setCropQuantity(cursor.getString(cursor.getColumnIndex(COLUMN_CROP_QUANTITY)));
            crop.setCropName(cursor.getString(cursor.getColumnIndex(COLUMN_CROP_NAME)));
        }

        return crop;
    }

    public FarmerDetails getFarmerDetails(String mobileNo)
    {
        FarmerDetails farmerDetails=new FarmerDetails();
        SQLiteDatabase db=getReadableDatabase();

        String query = "SELECT * FROM " + TABLE1_FARMER + " WHERE " + COLUMN_MOBILENO +
                " = '" + mobileNo + "' ;";

        Cursor c=null;
        c=db.rawQuery(query,null);

        while (c.moveToNext())
        {
            farmerDetails.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
            farmerDetails.setMobileNo(c.getString(c.getColumnIndex(COLUMN_MOBILENO)));
            farmerDetails.setArea(c.getString(c.getColumnIndex(COLUMN_AREA)));
            farmerDetails.setCity(c.getString(c.getColumnIndex(COLUMN_CITY)));
            farmerDetails.setState(c.getString(c.getColumnIndex(COLUMN_STATE)));
        }

        return farmerDetails;
    }

    public boolean purchaseProduct(String buyerMobileNo,String sellerMobileNo,String productName,String quantityPurchased,
                                   String productPrice)
    {
        SQLiteDatabase db=getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(COLUMN_USER_MOBILENO,buyerMobileNo);
        values.put(COLUMN_SELLER_MOBILENO,sellerMobileNo);
        values.put(COLUMN_CROP_NAME,productName);
        values.put(COLUMN_QUANTITY_REQUIRED,quantityPurchased);
        values.put(COLUMN_CROP_PRICE,productPrice);

        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_USER_MOBILENO,sellerMobileNo);
        contentValues.put(COLUMN_CUSTOMER_MOBILENO,buyerMobileNo);
        contentValues.put(COLUMN_CROP_NAME,productName);
        contentValues.put(COLUMN_QUANTITY_REQUIRED,quantityPurchased);
        contentValues.put(COLUMN_CROP_PRICE,productPrice);

        TABLE3_PURCHASE = "PURCHASE_" + buyerMobileNo;
        TABLE4_SOLD="SOLD_"+ sellerMobileNo;

        if(db.insert(TABLE3_PURCHASE,null,values)!=-1 && db.insert(TABLE4_SOLD,null,contentValues)!=-1)
            return true;
        else
            return false;
    }

    public ArrayList<CropDetails> getCropPurchased(String userMobileNo)
    {
        ArrayList<CropDetails> cropDetails=new ArrayList<CropDetails>();
        SQLiteDatabase db= this.getReadableDatabase();
        TABLE3_PURCHASE = "PURCHASE_" + userMobileNo;

        String query= "SELECT * FROM " + TABLE3_PURCHASE + " ;";
        Cursor cursor=null;
        cursor=db.rawQuery(query,null);

        while (cursor.moveToNext())
        {
            CropDetails crop=new CropDetails();
            crop.setCropQuantity(cursor.getString(cursor.getColumnIndex(COLUMN_QUANTITY_REQUIRED)));
            crop.setCropName(cursor.getString(cursor.getColumnIndex(COLUMN_CROP_NAME)));
            crop.setMobileNo(cursor.getString(cursor.getColumnIndex(COLUMN_SELLER_MOBILENO)));
            crop.setCropPrice(cursor.getString(cursor.getColumnIndex(COLUMN_CROP_PRICE)));

            cropDetails.add(crop);
        }
        cursor.close();
        return cropDetails;
    }

    public ArrayList<FarmerDetails> getSellerDetails(ArrayList<CropDetails> cropDetails)
    {
        ArrayList<FarmerDetails> farmerDetails=new ArrayList<FarmerDetails>();
        SQLiteDatabase db=getReadableDatabase();
        int count=0;
        String mobileNo=new String();
        Cursor cursor;

        while (count<cropDetails.size())
        {
            FarmerDetails farmer=new FarmerDetails();
            farmer.setMobileNo(cropDetails.get(count).getMobileNo());
            mobileNo=farmer.getMobileNo();

            String query = "SELECT * FROM " + TABLE1_FARMER + " WHERE " + COLUMN_MOBILENO
                    + " = '" + mobileNo + "' ;";

            cursor=null;
            cursor=db.rawQuery(query,null);

            while (cursor.moveToNext())
            {
                farmer.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                farmer.setArea(cursor.getString(cursor.getColumnIndex(COLUMN_AREA)));
                farmer.setCity(cursor.getString(cursor.getColumnIndex(COLUMN_CITY)));
                farmer.setState(cursor.getString(cursor.getColumnIndex(COLUMN_STATE)));
            }

            farmerDetails.add(farmer);
            count++;

        }
        return farmerDetails;
    }

    public ArrayList<String> getBuyerNames (ArrayList<CropDetails> cropDetails)
    {
        ArrayList<String> buyerNames=new ArrayList<String>();
        SQLiteDatabase db=this.getReadableDatabase();
        int count=0;
        String mobileNo=new String();
        Cursor cursor;

        while (count<cropDetails.size())
        {
            String buyerName=new String();
            mobileNo=cropDetails.get(count).getMobileNo();
            String query = "SELECT " + COLUMN_NAME + " FROM " + TABLE1_FARMER + " WHERE " + COLUMN_MOBILENO
                    + " = '" + mobileNo + "' ;";

            cursor=null;
            cursor=db.rawQuery(query,null);
            while (cursor.moveToNext())
            {
                buyerName=cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            }
            buyerNames.add(buyerName);
            count++;
        }
        return buyerNames;
    }

    public ArrayList<CropDetails> getCropSold(String userMobileNo)
    {
        ArrayList<CropDetails> cropDetails=new ArrayList<CropDetails>();
        SQLiteDatabase db=this.getReadableDatabase();
        TABLE4_SOLD ="SOLD_"+ userMobileNo;

        String query= "SELECT * FROM " + TABLE4_SOLD + " ;" ;
        Cursor cursor=null;
        cursor=db.rawQuery(query,null);

        while (cursor.moveToNext())
        {
            CropDetails crop=new CropDetails();
            crop.setCropQuantity(cursor.getString(cursor.getColumnIndex(COLUMN_QUANTITY_REQUIRED)));
            crop.setCropName(cursor.getString(cursor.getColumnIndex(COLUMN_CROP_NAME)));
            crop.setMobileNo(cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMER_MOBILENO)));
            crop.setCropPrice(cursor.getString(cursor.getColumnIndex(COLUMN_CROP_PRICE)));

            cropDetails.add(crop);
        }
        cursor.close();
        db.close();
        return cropDetails;
    }

    public boolean deleteCrop(String userMobileNo, String productName)
    {
        SQLiteDatabase db=getWritableDatabase();
        return db.delete(TABLE2_CROP,COLUMN_MOBILENO + " = '" + userMobileNo + "' AND " +
                COLUMN_CROP_NAME + " = '" + productName + "'",null )>0;
    }

    public boolean changeQuantity(String sellerMobileNo,String productName,String quantityPurchased)
    {
        SQLiteDatabase readableDatabase=getReadableDatabase();
        SQLiteDatabase writableDatabase=getWritableDatabase();

        String originalQuantity=new String ();

        String querySearch="SELECT * FROM " + TABLE2_CROP + " WHERE " + COLUMN_MOBILENO + " = '"
                + sellerMobileNo + "' AND " + COLUMN_CROP_NAME + " = '" + productName + "' ;";

        Cursor cursor=null;
        cursor=readableDatabase.rawQuery(querySearch,null);
        while (cursor.moveToNext())
        {
            originalQuantity=cursor.getString(cursor.getColumnIndex(COLUMN_CROP_QUANTITY));
        }
        cursor.close();

        String remainingQuantity= String.valueOf(Long.parseLong(originalQuantity)-Long.parseLong(quantityPurchased));

        ContentValues values=new ContentValues();
        values.put(COLUMN_CROP_QUANTITY,remainingQuantity);

        if((writableDatabase.update(TABLE2_CROP,values,COLUMN_MOBILENO + " = '" + sellerMobileNo
         + "' AND " + COLUMN_CROP_NAME + " = '" + productName + "' ",null)!=-1))
            return true;
        else
            return false;
    }

    public String get_id(String userMobileNo, String cropName)
    {
        String id=new String() ;
        SQLiteDatabase db=getReadableDatabase();
        String query= " SELECT * FROM " + TABLE2_CROP + " WHERE " + COLUMN_MOBILENO + " = '" + userMobileNo +
                 "' AND " + COLUMN_CROP_NAME + " = '" + cropName + "' ;";
        Cursor cursor=null;

        cursor=db.rawQuery(query,null);
        while (cursor.moveToNext())
        {
            id= cursor.getString(cursor.getColumnIndex(COLUMN_ID));
        }
        cursor.close();
        db.close();
        return  id;
    }

    public boolean updateProductDetails(String id,String productName,String productPrice,String productQuantity)
    {
        SQLiteDatabase writableDatabase=getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(COLUMN_CROP_NAME,productName);
        values.put(COLUMN_CROP_PRICE,productPrice);
        values.put(COLUMN_CROP_QUANTITY,productQuantity);

        if(writableDatabase.update(TABLE2_CROP,values,COLUMN_ID + " = '" + id + "' ",null)!=-1)
        {
            writableDatabase.close();
            return true;
        }

        else
        {
            writableDatabase.close();
            return false;
        }
    }

    public boolean checkSameCrop(String mobileNo, String cropName)
    {
        SQLiteDatabase db=getReadableDatabase();
        String query= "SELECT * FROM " + TABLE2_CROP + " WHERE " + COLUMN_MOBILENO + " = '" + mobileNo +
                "' AND " + COLUMN_CROP_NAME + " = '" + cropName + "' ;";
        Cursor cursor=null;
        cursor=db.rawQuery(query,null);
        return cursor.moveToFirst();
    }

    public boolean checkSameNoFarmer(String mobileNo)
    {
        SQLiteDatabase db=getReadableDatabase();
        String query= "SELECT * FROM " + TABLE1_FARMER + " WHERE " + COLUMN_MOBILENO + " = '"
                + mobileNo + "' ;";
        Cursor cursor=null;
        cursor=db.rawQuery(query,null);
        return cursor.moveToFirst();
    }

    public void removeUsedCrops()
    {
        SQLiteDatabase db=getReadableDatabase();
        db.delete(TABLE2_CROP,COLUMN_CROP_QUANTITY + " = '0'",null);
    }
}
