package edu.cricket.api.cricketscores.rest.source.model;

public class Over {
    private int ball;
    private int balls;
    private boolean complete;
    private boolean maiden;
    private int noBall;
    private int wide;
    private int legByes;
    private int byes;
    private int number;
    private int runs;
    private int wickets;
    private double overs;
    private double actual;
    private double unique;


    public int getBall() {
        return ball;
    }

    public void setBall(int ball) {
        this.ball = ball;
    }

    public int getBalls() {
        return balls;
    }

    public void setBalls(int balls) {
        this.balls = balls;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isMaiden() {
        return maiden;
    }

    public void setMaiden(boolean maiden) {
        this.maiden = maiden;
    }

    public int getNoBall() {
        return noBall;
    }

    public void setNoBall(int noBall) {
        this.noBall = noBall;
    }

    public int getWide() {
        return wide;
    }

    public void setWide(int wide) {
        this.wide = wide;
    }

    public int getLegByes() {
        return legByes;
    }

    public void setLegByes(int legByes) {
        this.legByes = legByes;
    }

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

    public int getRuns() {
        return runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public int getWickets() {
        return wickets;
    }

    public void setWickets(int wickets) {
        this.wickets = wickets;
    }

    public double getOvers() {
        return overs;
    }

    public void setOvers(double overs) {
        this.overs = overs;
    }

    public double getActual() {
        return actual;
    }

    public void setActual(double actual) {
        this.actual = actual;
    }

    public double getUnique() {
        return unique;
    }

    public void setUnique(double unique) {
        this.unique = unique;
    }

    @Override
    public String toString() {
        return "Over{" +
                "ball=" + ball +
                ", balls=" + balls +
                ", complete=" + complete +
                ", maiden=" + maiden +
                ", noBall=" + noBall +
                ", wide=" + wide +
                ", legByes=" + legByes +
                ", byes=" + byes +
                ", number=" + number +
                ", runs=" + runs +
                ", wickets=" + wickets +
                ", overs=" + overs +
                ", actual=" + actual +
                ", unique=" + unique +
                '}';
    }
}
