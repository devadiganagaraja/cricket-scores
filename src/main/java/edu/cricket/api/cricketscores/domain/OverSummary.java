package edu.cricket.api.cricketscores.domain;

public class OverSummary {
    private boolean complete;
    private boolean maiden;
    private int totalRuns;
    private int overNo;
    private double oversUnique;

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

    public int getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(int totalRuns) {
        this.totalRuns = totalRuns;
    }

    public int getOverNo() {
        return overNo;
    }

    public void setOverNo(int overNo) {
        this.overNo = overNo;
    }

    public double getOversUnique() {
        return oversUnique;
    }

    public void setOversUnique(double oversUnique) {
        this.oversUnique = oversUnique;
    }
}
