package edu.cricket.api.cricketscores.domain;

public class BatsmanSummary {
    private String batsmanName;
    private int batsmanRuns;
    private int batsmanBalls;

    public String getBatsmanName() {
        return batsmanName;
    }

    public void setBatsmanName(String batsmanName) {
        this.batsmanName = batsmanName;
    }

    public int getBatsmanRuns() {
        return batsmanRuns;
    }

    public void setBatsmanRuns(int batsmanRuns) {
        this.batsmanRuns = batsmanRuns;
    }

    public int getBatsmanBalls() {
        return batsmanBalls;
    }

    public void setBatsmanBalls(int batsmanBalls) {
        this.batsmanBalls = batsmanBalls;
    }
}
