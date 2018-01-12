package com.gw.kisansewa.buyCropsTab;

/**
 * Created by rohit on 11/1/18.
 */

public class CropsModel {

    private String cropName;
    private int sellerQuantity;
    private String imageLink;

    public CropsModel(String cropName, int sellerQuantity, String imageLink) {
        this.cropName = cropName;
        this.sellerQuantity = sellerQuantity;
        this.imageLink = imageLink;
    }

    public CropsModel() {
    }

    public CropsModel(String cropName, int sellerQuantity) {
        this.cropName = cropName;
        this.sellerQuantity = sellerQuantity;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public int getSellerQuantity() {
        return sellerQuantity;
    }

    public void setSellerQuantity(int sellerQuantity) {
        this.sellerQuantity = sellerQuantity;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
