package edu.cricket.api.cricketscores.domain;

public class BowlerSummary {
    private String bowlerName;
    private int bowlerBalls;
    private double bowlerOvers;
    private int bowlerRuns;
    private int bowlerWickets;

    public String getBowlerName() {
        return bowlerName;
    }

    public void setBowlerName(String bowlerName) {
        this.bowlerName = bowlerName;
    }

    public int getBowlerBalls() {
        return bowlerBalls;
    }

    public void setBowlerBalls(int bowlerBalls) {
        this.bowlerBalls = bowlerBalls;
    }

    public int getBowlerRuns() {
        return bowlerRuns;
    }

    public void setBowlerRuns(int bowlerRuns) {
        this.bowlerRuns = bowlerRuns;
    }

    public int getBowlerWickets() {
        return bowlerWickets;
    }

    public double getBowlerOvers() {
        return bowlerOvers;
    }

    public void setBowlerOvers(double bowlerOvers) {
        this.bowlerOvers = bowlerOvers;
    }

    public void setBowlerWickets(int bowlerWickets) {
        this.bowlerWickets = bowlerWickets;
    }
}
