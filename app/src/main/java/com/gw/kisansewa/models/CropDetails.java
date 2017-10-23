package com.gw.kisansewa.models;



public class CropDetails  {

//    private int cropId;
    private String cropName;
    private String cropQuantity;
    private String cropPrice;
    private String mobileNo;

    public CropDetails()
    {

    }

    public CropDetails(String cropName, String cropQuantity, String cropPrice, String mobileNo) {
        this.cropName = cropName;
        this.cropQuantity = cropQuantity;
        this.cropPrice = cropPrice;
        this.mobileNo=mobileNo;
    }

//    public int getCropId() {
//        return cropId;
//    }
//
//    public void setCropId(int cropId) {
//        this.cropId = cropId;
//    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getCropQuantity() {
        return cropQuantity;
    }

    public void setCropQuantity(String cropQuantity) {
        this.cropQuantity = cropQuantity;
    }

    public String getCropPrice() {
        return cropPrice;
    }

    public void setCropPrice(String cropPrice) {
        this.cropPrice = cropPrice;
    }
}
