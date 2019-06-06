package edu.cricket.api.cricketscores.rest.response.model;

import java.util.Objects;

public class BatsmanCard implements Comparable<BatsmanCard>{

    private long playerId;
    private String playerName;
    private String balls;
    private String runs;
    private String battingDescription;
    private Integer position;
    private boolean batted;
    private boolean out;
    private boolean live;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getBalls() {
        return balls;
    }

    public void setBalls(String balls) {
        this.balls = balls;
    }

    public String getRuns() {
        return runs;
    }

    public void setRuns(String runs) {
        this.runs = runs;
    }

    public String getBattingDescription() {
        return battingDescription;
    }

    public void setBattingDescription(String battingDescription) {
        this.battingDescription = battingDescription;
    }


    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public boolean isBatted() {
        return batted;
    }

    public void setBatted(boolean batted) {
        this.batted = batted;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public boolean isOut() {
        return out;
    }

    public void setOut(boolean out) {
        this.out = out;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BatsmanCard that = (BatsmanCard) o;
        return playerId == that.playerId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(playerId);
    }


    @Override
    public int compareTo(BatsmanCard o) {
        return this.position.compareTo(o.position);
    }
}
