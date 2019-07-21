package edu.cricket.api.cricketscores.rest.response.model;

import java.util.List;

public class Squad {
    private String teamName;
    private List<SquadPlayer> players;

    private int totalPoints;


    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<SquadPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<SquadPlayer> players) {
        this.players = players;
    }


    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    @Override
    public String toString() {
        return "Squad{" +
                "teamName='" + teamName + '\'' +
                ", players=" + players +
                ", totalPoints=" + totalPoints +
                '}';
    }
}
