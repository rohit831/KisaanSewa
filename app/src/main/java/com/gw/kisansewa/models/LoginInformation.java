package com.gw.kisansewa.models;

public class LoginInformation {
    private String mobileNo;
    private String password;

    public LoginInformation(String mobileNo, String password) {
        this.mobileNo = mobileNo;
        this.password = password;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
