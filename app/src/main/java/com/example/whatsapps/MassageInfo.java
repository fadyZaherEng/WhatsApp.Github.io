package com.example.whatsapps;

public class MassageInfo {
    String UserName,CurrentDate,CurrentTime,inputMassage;

    public MassageInfo(String userName, String currentDate, String currentTime, String inputMassage) {
        UserName = userName;
        CurrentDate = currentDate;
        CurrentTime = currentTime;
        this.inputMassage = inputMassage;
    }

    public MassageInfo() {
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getCurrentDate() {
        return CurrentDate;
    }

    public void setCurrentDate(String currentDate) {
        CurrentDate = currentDate;
    }

    public String getCurrentTime() {
        return CurrentTime;
    }

    public void setCurrentTime(String currentTime) {
        CurrentTime = currentTime;
    }

    public String getInputMassage() {
        return inputMassage;
    }

    public void setInputMassage(String inputMassage) {
        this.inputMassage = inputMassage;
    }
}
