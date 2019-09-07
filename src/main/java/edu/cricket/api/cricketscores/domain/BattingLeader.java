package edu.cricket.api.cricketscores.domain;

import java.util.Objects;

public class BattingLeader implements Comparable<BattingLeader>{
    private long playerId;
    private String playerName;
    private int matches;
    private int runs;
    private int balls;
    private int sixes;
    private int fours;
    private String strikeRate;
    private String average;

    public int getBalls() {
        return balls;
    }

    public void setBalls(int balls) {
        this.balls = balls;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getMatches() {
        return matches;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }

    public int getRuns() {
        return runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public int getSixes() {
        return sixes;
    }

    public void setSixes(int sixes) {
        this.sixes = sixes;
    }

    public int getFours() {
        return fours;
    }

    public void setFours(int fours) {
        this.fours = fours;
    }

    public String getStrikeRate() {
        return strikeRate;
    }

    public void setStrikeRate(String strikeRate) {
        this.strikeRate = strikeRate;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }


    @Override
    public String toString() {
        return "BattingLeader{" +
                "playerId=" + playerId +
                ", playerName='" + playerName + '\'' +
                ", matches=" + matches +
                ", runs=" + runs +
                ", balls=" + balls +
                ", sixes=" + sixes +
                ", fours=" + fours +
                ", strikeRate='" + strikeRate + '\'' +
                ", average='" + average + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BattingLeader that = (BattingLeader) o;
        return playerId == that.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }

    @Override
    public int compareTo(BattingLeader o) {
        return o.runs - this.runs;
    }
}
