package edu.cricket.api.cricketscores.rest.response.model;

import java.util.List;

public class Squad {
    private String teamName;
    private List<String> players;

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    @Override
    public String toString() {
        return "Squad{" +
                "teamName='" + teamName + '\'' +
                ", players=" + players +
                '}';
    }
}
