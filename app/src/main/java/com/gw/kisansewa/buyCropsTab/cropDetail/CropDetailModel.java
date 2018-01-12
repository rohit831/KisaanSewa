package com.gw.kisansewa.buyCropsTab.cropDetail;

public class CropDetailModel {

    private String name;
    private String mobileNo;
    private String city;
    private String price;

    public CropDetailModel(String mobileNo,String name, String city, String price) {
        this.name = name;
        this.city = city;
        this.price = price;
        this.mobileNo = mobileNo;
    }

    public CropDetailModel() {
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
