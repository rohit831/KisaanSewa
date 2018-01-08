package com.gw.kisansewa.models;

public class FarmerDetails
{
    private String name;
    private String mobileNo;
    private String area;
    private String city;
    private String state;
    private String password;
    private String image;

    public FarmerDetails()
    {

    }

    public FarmerDetails(String name, String mobileNo, String area, String city, String state, String password) {
        this.name = name;
        this.mobileNo = mobileNo;
        this.area = area;
        this.city = city;
        this.state = state;
        this.password = password;
    }

    public FarmerDetails(String name, String mobileNo, String area, String city, String state, String password, String image) {
        this.name = name;
        this.mobileNo = mobileNo;
        this.area = area;
        this.city = city;
        this.state = state;
        this.password = password;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress(){
        return area.concat(", ").concat(city)
                            .concat(", ").concat(state);

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
