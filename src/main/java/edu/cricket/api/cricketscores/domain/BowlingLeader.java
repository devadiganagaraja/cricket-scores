package edu.cricket.api.cricketscores.domain;

import java.util.Objects;

public class BowlingLeader implements Comparable<BowlingLeader> {
    private long playerId;
    private String playerName;
    private int matches;
    private int runsConceded;
    private float overs;
    private int wickets;
    private int extras;
    private String strikeRate;
    private String average;
    private int maidens;

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

    public int getMaidens() {
        return maidens;
    }

    public void setMaidens(int maidens) {
        this.maidens = maidens;
    }

    public float getOvers() {
        return overs;
    }

    public void setOvers(float overs) {
        this.overs = overs;
    }

    public int getRunsConceded() {
        return runsConceded;
    }

    public void setRunsConceded(int runsConceded) {
        this.runsConceded = runsConceded;
    }

    public int getWickets() {
        return wickets;
    }

    public void setWickets(int wickets) {
        this.wickets = wickets;
    }

    public int getExtras() {
        return extras;
    }

    public void setExtras(int extras) {
        this.extras = extras;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BowlingLeader that = (BowlingLeader) o;
        return playerId == that.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }


    @Override
    public int compareTo(BowlingLeader o) {
        return o.wickets - this.wickets;
    }

    @Override
    public String toString() {
        return "BowlingLeader{" +
                "playerId=" + playerId +
                ", playerName='" + playerName + '\'' +
                ", matches=" + matches +
                ", runsConceded=" + runsConceded +
                ", overs=" + overs +
                ", wickets=" + wickets +
                ", extras=" + extras +
                ", strikeRate='" + strikeRate + '\'' +
                ", average='" + average + '\'' +
                ", maidens=" + maidens +
                '}';
    }
}
