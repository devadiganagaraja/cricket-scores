package edu.cricket.api.cricketscores.rest.source.model;

public class Batsman {
    private Ref athlete;
    private Ref team;
    private int totalRuns;
    private int faced;
    private int fours;
    private int runs;
    private int sixes;

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

    public int getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(int totalRuns) {
        this.totalRuns = totalRuns;
    }

    public int getFaced() {
        return faced;
    }

    public void setFaced(int faced) {
        this.faced = faced;
    }

    public int getFours() {
        return fours;
    }

    public void setFours(int fours) {
        this.fours = fours;
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
}
