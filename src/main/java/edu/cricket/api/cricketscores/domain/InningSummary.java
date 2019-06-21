package edu.cricket.api.cricketscores.domain;

public class InningSummary {
    private int inningsNo;
    private int totalRuns;
    private int wickets;
    private double oversUnique;
    private String battingTeamName;

    public int getInningsNo() {
        return inningsNo;
    }

    public void setInningsNo(int inningsNo) {
        this.inningsNo = inningsNo;
    }

    public double getOversUnique() {
        return oversUnique;
    }

    public void setOversUnique(double oversUnique) {
        this.oversUnique = oversUnique;
    }

    public int getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(int totalRuns) {
        this.totalRuns = totalRuns;
    }

    public int getWickets() {
        return wickets;
    }

    public void setWickets(int wickets) {
        this.wickets = wickets;
    }

    public String getBattingTeamName() {
        return battingTeamName;
    }

    public void setBattingTeamName(String battingTeamName) {
        this.battingTeamName = battingTeamName;
    }
}
