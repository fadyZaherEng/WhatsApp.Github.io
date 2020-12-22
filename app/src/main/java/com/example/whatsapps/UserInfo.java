package com.example.whatsapps;

public class UserInfo {
    String UserName,UserStatus,ProfileImage,id;
    public  UserInfo(){

    }

    public UserInfo(String userName, String userStatus, String profileImage,String id) {
        UserName = userName;
        UserStatus = userStatus;
        ProfileImage = profileImage;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserStatus() {
        return UserStatus;
    }

    public void setUserStatus(String userStatus) {
        UserStatus = userStatus;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }
}
