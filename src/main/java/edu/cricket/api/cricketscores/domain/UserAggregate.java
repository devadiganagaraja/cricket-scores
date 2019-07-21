package edu.cricket.api.cricketscores.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "users")
public class UserAggregate {
    @Id
    private String userName;
    private String userPassword;
    @Indexed
    private String mobileNo;

    private Map<String, List<String>> matchElevens;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Map<String, List<String>> getMatchElevens() {
        return matchElevens;
    }

    public void setMatchElevens(Map<String, List<String>> matchElevens) {
        this.matchElevens = matchElevens;
    }

    @Override
    public String toString() {
        return "UserAggregate{" +
                "userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", matchElevens=" + matchElevens +
                '}';
    }
}
