package com.gw.kisansewa.models;

public class Orders {

    private String sellerMobileNo;
    private String buyerMobileNo;
    private String cropName;
    private String cropQuantity;
    private String cropPrice;

    public Orders(String sellerMobileNo, String buyerMobileNo, String cropName, String cropQuantity, String cropPrice) {
        this.sellerMobileNo = sellerMobileNo;
        this.buyerMobileNo = buyerMobileNo;
        this.cropName = cropName;
        this.cropQuantity = cropQuantity;
        this.cropPrice = cropPrice;
    }

    public String getSellerMobileNo() {
        return sellerMobileNo;
    }

    public void setSellerMobileNo(String sellerMobileNo) {
        this.sellerMobileNo = sellerMobileNo;
    }

    public String getBuyerMobileNo() {
        return buyerMobileNo;
    }

    public void setBuyerMobileNo(String buyerMobileNo) {
        this.buyerMobileNo = buyerMobileNo;
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
