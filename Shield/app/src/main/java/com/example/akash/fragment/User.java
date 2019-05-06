package com.example.akash.fragment;

public class User {
    private String mobileNo;
    private  String number;
    private String name;
    private String password;
    private String retype_password;
    private String email;
    private String DOB;
    private String PIN;
    private String date;
    private String time;
    private String post;
    private String gender;
    private String profilePic;
    private Boolean OtpResult;
    private Boolean LogInResult;
    private Boolean Result;
    private Boolean PasswordUpdte;


    public User()
    {
        number="";
        LogInResult=false;
        PasswordUpdte=false;
        gender= "";
        mobileNo = "";
        name = "";
        email = "";
        DOB = "";
        PIN = "";
        password="";
        retype_password="";
        profilePic="";
        date="";
        time="";
        post="";
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public Boolean getPasswordUpdte() {
        return PasswordUpdte;
    }

    public void setPasswordUpdte(Boolean passwordUpdte) {
        PasswordUpdte = passwordUpdte;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Boolean getLogInResult() {
        return LogInResult;
    }

    public void setLogInResult(Boolean logInResult) {
        LogInResult = logInResult;
    }

    public Boolean getOtpResult() {
        return OtpResult;
    }

    public void setOtpResult(Boolean otpResult) {
        OtpResult = otpResult;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public void setName(String Name) {
        this.name = Name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRetype_password() {
        return retype_password;
    }

    public void setRetype_password(String retype_password) {
        this.retype_password = retype_password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String PIN) {
        this.PIN = PIN;
    }

    public Boolean getResult() {
        return Result;
    }

    public void setResult(Boolean result) {
        Result = result;
    }
}
