package edu.cricket.api.cricketscores.rest.source.model;

public class Innings {
    private  int byes;
    private int number;
    private int balls;
    private int noBalls;
    private int wickets;
    private int legByes;
    private int ballLimit;
    private int target;
    private int fallOfWickets;
    private int trailBy;
    private int leadBy;
    private int remainingRuns;
    private double remainingOvers;
    private int runs;
    private int wides;
    private int totalRuns;


    public int getByes() {
        return byes;
    }

    public void setByes(int byes) {
        this.byes = byes;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getBalls() {
        return balls;
    }

    public void setBalls(int balls) {
        this.balls = balls;
    }

    public int getNoBalls() {
        return noBalls;
    }

    public void setNoBalls(int noBalls) {
        this.noBalls = noBalls;
    }

    public int getWickets() {
        return wickets;
    }

    public void setWickets(int wickets) {
        this.wickets = wickets;
    }

    public int getLegByes() {
        return legByes;
    }

    public void setLegByes(int legByes) {
        this.legByes = legByes;
    }

    public int getBallLimit() {
        return ballLimit;
    }

    public void setBallLimit(int ballLimit) {
        this.ballLimit = ballLimit;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getFallOfWickets() {
        return fallOfWickets;
    }

    public void setFallOfWickets(int fallOfWickets) {
        this.fallOfWickets = fallOfWickets;
    }

    public int getTrailBy() {
        return trailBy;
    }

    public void setTrailBy(int trailBy) {
        this.trailBy = trailBy;
    }

    public int getLeadBy() {
        return leadBy;
    }

    public void setLeadBy(int leadBy) {
        this.leadBy = leadBy;
    }

    public int getRemainingRuns() {
        return remainingRuns;
    }

    public void setRemainingRuns(int remainingRuns) {
        this.remainingRuns = remainingRuns;
    }

    public double getRemainingOvers() {
        return remainingOvers;
    }

    public void setRemainingOvers(double remainingOvers) {
        this.remainingOvers = remainingOvers;
    }

    public int getRuns() {
        return runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public int getWides() {
        return wides;
    }

    public void setWides(int wides) {
        this.wides = wides;
    }

    public int getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(int totalRuns) {
        this.totalRuns = totalRuns;
    }


    @Override
    public String toString() {
        return "Innings{" +
                "byes=" + byes +
                ", number=" + number +
                ", balls=" + balls +
                ", noBalls=" + noBalls +
                ", wickets=" + wickets +
                ", legByes=" + legByes +
                ", ballLimit=" + ballLimit +
                ", target=" + target +
                ", fallOfWickets=" + fallOfWickets +
                ", trailBy=" + trailBy +
                ", leadBy=" + leadBy +
                ", remainingRuns=" + remainingRuns +
                ", remainingOvers=" + remainingOvers +
                ", runs=" + runs +
                ", wides=" + wides +
                ", totalRuns=" + totalRuns +
                '}';
    }
}
