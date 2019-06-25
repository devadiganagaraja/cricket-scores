package edu.cricket.api.cricketscores.rest.response.model;

public class PlayerStats {
    private String formatName;
    private String matches;
    private String runs;
    private String wickets;
    private String highScore;
    private String bestBowling;
    private String battingStrikeRate;
    private String bowlingStrikeRate;
    private String battingAverage;
    private String bowlingAverage;
    private String economyRate;

    public String getFormatName() {
        return formatName;
    }

    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }

    public String getMatches() {
        return matches;
    }

    public void setMatches(String matches) {
        this.matches = matches;
    }

    public String getRuns() {
        return runs;
    }

    public void setRuns(String runs) {
        this.runs = runs;
    }

    public String getWickets() {
        return wickets;
    }

    public void setWickets(String wickets) {
        this.wickets = wickets;
    }

    public String getHighScore() {
        return highScore;
    }

    public void setHighScore(String highScore) {
        this.highScore = highScore;
    }

    public String getBestBowling() {
        return bestBowling;
    }

    public void setBestBowling(String bestBowling) {
        this.bestBowling = bestBowling;
    }

    public String getBattingStrikeRate() {
        return battingStrikeRate;
    }

    public void setBattingStrikeRate(String battingStrikeRate) {
        this.battingStrikeRate = battingStrikeRate;
    }

    public String getBowlingStrikeRate() {
        return bowlingStrikeRate;
    }

    public void setBowlingStrikeRate(String bowlingStrikeRate) {
        this.bowlingStrikeRate = bowlingStrikeRate;
    }

    public String getBattingAverage() {
        return battingAverage;
    }

    public void setBattingAverage(String battingAverage) {
        this.battingAverage = battingAverage;
    }

    public String getBowlingAverage() {
        return bowlingAverage;
    }

    public void setBowlingAverage(String bowlingAverage) {
        this.bowlingAverage = bowlingAverage;
    }

    public String getEconomyRate() {
        return economyRate;
    }

    public void setEconomyRate(String economyRate) {
        this.economyRate = economyRate;
    }

    @Override
    public String toString() {
        return "PlayerStats{" +
                "formatName='" + formatName + '\'' +
                ", matches='" + matches + '\'' +
                ", runs='" + runs + '\'' +
                ", wickets='" + wickets + '\'' +
                ", highScore='" + highScore + '\'' +
                ", bestBowling='" + bestBowling + '\'' +
                ", battingStrikeRate='" + battingStrikeRate + '\'' +
                ", bowlingStrikeRate='" + bowlingStrikeRate + '\'' +
                ", battingAverage='" + battingAverage + '\'' +
                ", bowlingAverage='" + bowlingAverage + '\'' +
                ", economyRate='" + economyRate + '\'' +
                '}';
    }
}
