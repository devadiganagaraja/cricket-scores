package edu.cricket.api.cricketscores.rest.response.model;

import java.util.List;

public class Competitor {
    private String teamName;
    private String score;
    private boolean winner;
    private List<SquadPlayer> squad;

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public List<SquadPlayer> getSquad() {
        return squad;
    }

    public void setSquad(List<SquadPlayer> squad) {
        this.squad = squad;
    }
}
