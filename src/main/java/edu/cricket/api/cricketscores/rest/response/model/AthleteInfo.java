package edu.cricket.api.cricketscores.rest.response.model;

import java.util.List;

public class AthleteInfo {
    private String athleteName;
    private int age;
    private String battingStyle;
    private String bowlingStyle;
    private String country;
    private String playerType;

    private List<PlayerStats> playerStats;

    public String getAthleteName() {
        return athleteName;
    }

    public void setAthleteName(String athleteName) {
        this.athleteName = athleteName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBattingStyle() {
        return battingStyle;
    }

    public void setBattingStyle(String battingStyle) {
        this.battingStyle = battingStyle;
    }

    public String getBowlingStyle() {
        return bowlingStyle;
    }

    public void setBowlingStyle(String bowlingStyle) {
        this.bowlingStyle = bowlingStyle;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPlayerType() {
        return playerType;
    }

    public void setPlayerType(String playerType) {
        this.playerType = playerType;
    }

    public List<PlayerStats> getPlayerStats() {
        return playerStats;
    }

    public void setPlayerStats(List<PlayerStats> playerStats) {
        this.playerStats = playerStats;
    }

    @Override
    public String toString() {
        return "AthleteInfo{" +
                "athleteName='" + athleteName + '\'' +
                ", age=" + age +
                ", battingStyle='" + battingStyle + '\'' +
                ", bowlingStyle='" + bowlingStyle + '\'' +
                ", country='" + country + '\'' +
                ", playerType='" + playerType + '\'' +
                ", playerStats=" + playerStats +
                '}';
    }
}
