package edu.cricket.api.cricketscores.rest.response.model;

public class Competitor {
    private String teamName;
    private String score;

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
}
