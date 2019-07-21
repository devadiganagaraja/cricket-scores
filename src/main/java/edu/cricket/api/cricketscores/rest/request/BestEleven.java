package edu.cricket.api.cricketscores.rest.request;

public class BestEleven {
    private String playerIds;
    private String userName;
    private String password;


    public String getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(String playerIds) {
        this.playerIds = playerIds;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "BestEleven{" +
                "playerIds='" + playerIds + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
