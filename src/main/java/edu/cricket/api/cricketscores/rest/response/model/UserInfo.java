package edu.cricket.api.cricketscores.rest.response.model;

public class UserInfo {
    private String userName;
    private String mobileNumber;
    private int statusCode;
    private String status;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userName='" + userName + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", statusCode=" + statusCode +
                ", status=" + status +
                '}';
    }
}
