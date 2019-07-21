package edu.cricket.api.cricketscores.rest.response.model;

public class SquadPlayer {
    private String playerName;
    private int weightage = 9;

    public SquadPlayer(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getWeightage() {
        return weightage;
    }

    public void setWeightage(int weightage) {
        this.weightage = weightage;
    }

    @Override
    public String toString() {
        return "SquadPlayer{" +
                "playerName='" + playerName + '\'' +
                ", weightage=" + weightage +
                '}';
    }
}
