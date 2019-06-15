package edu.cricket.api.cricketscores.rest.source.model;

public class Bowler {
    private Ref athlete;
    private Ref team;
    private int maidens;
    private int balls;
    private int wickets;
    private int overs;
    private int conceded;

    public Ref getAthlete() {
        return athlete;
    }

    public void setAthlete(Ref athlete) {
        this.athlete = athlete;
    }

    public Ref getTeam() {
        return team;
    }

    public void setTeam(Ref team) {
        this.team = team;
    }

    public int getMaidens() {
        return maidens;
    }

    public void setMaidens(int maidens) {
        this.maidens = maidens;
    }

    public int getBalls() {
        return balls;
    }

    public void setBalls(int balls) {
        this.balls = balls;
    }

    public int getWickets() {
        return wickets;
    }

    public void setWickets(int wickets) {
        this.wickets = wickets;
    }

    public int getOvers() {
        return overs;
    }

    public void setOvers(int overs) {
        this.overs = overs;
    }

    public int getConceded() {
        return conceded;
    }

    public void setConceded(int conceded) {
        this.conceded = conceded;
    }
}
