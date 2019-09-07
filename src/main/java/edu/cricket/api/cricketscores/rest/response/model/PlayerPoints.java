package edu.cricket.api.cricketscores.rest.response.model;

public class PlayerPoints {
    private float points;
    private int runs;
    private float runsPoints;
    private int wickets;
    private float wicketsPoints;
    private int sixes;
    private float sixesPoints;
    private int fours;
    private float foursPoints;
    private int maidens;
    private float maidensPoints;
    private int catches;
    private float catchesPoints;
    private int stumped;
    private float stumpedPoints;


    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = points;
    }

    public int getRuns() {
        return runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public float getRunsPoints() {
        return runsPoints;
    }

    public void setRunsPoints(float runsPoints) {
        this.runsPoints = runsPoints;
    }

    public int getWickets() {
        return wickets;
    }

    public void setWickets(int wickets) {
        this.wickets = wickets;
    }

    public float getWicketsPoints() {
        return wicketsPoints;
    }

    public void setWicketsPoints(float wicketsPoints) {
        this.wicketsPoints = wicketsPoints;
    }

    public int getSixes() {
        return sixes;
    }

    public void setSixes(int sixes) {
        this.sixes = sixes;
    }

    public float getSixesPoints() {
        return sixesPoints;
    }

    public void setSixesPoints(float sixesPoints) {
        this.sixesPoints = sixesPoints;
    }

    public int getFours() {
        return fours;
    }

    public void setFours(int fours) {
        this.fours = fours;
    }

    public float getFoursPoints() {
        return foursPoints;
    }

    public void setFoursPoints(float foursPoints) {
        this.foursPoints = foursPoints;
    }

    public int getMaidens() {
        return maidens;
    }

    public void setMaidens(int maidens) {
        this.maidens = maidens;
    }

    public float getMaidensPoints() {
        return maidensPoints;
    }

    public void setMaidensPoints(float maidensPoints) {
        this.maidensPoints = maidensPoints;
    }

    public int getCatches() {
        return catches;
    }

    public void setCatches(int catches) {
        this.catches = catches;
    }

    public float getCatchesPoints() {
        return catchesPoints;
    }

    public void setCatchesPoints(float catchesPoints) {
        this.catchesPoints = catchesPoints;
    }

    public int getStumped() {
        return stumped;
    }

    public void setStumped(int stumped) {
        this.stumped = stumped;
    }

    public float getStumpedPoints() {
        return stumpedPoints;
    }

    public void setStumpedPoints(float stumpedPoints) {
        this.stumpedPoints = stumpedPoints;
    }


    @Override
    public String toString() {
        return "PlayerPoints{" +
                "points=" + points +
                ", runs=" + runs +
                ", runsPoints=" + runsPoints +
                ", wickets=" + wickets +
                ", wicketsPoints=" + wicketsPoints +
                ", sixes=" + sixes +
                ", sixesPoints=" + sixesPoints +
                ", fours=" + fours +
                ", foursPoints=" + foursPoints +
                ", maidens=" + maidens +
                ", maidensPoints=" + maidensPoints +
                ", catches=" + catches +
                ", catchesPoints=" + catchesPoints +
                ", stumped=" + stumped +
                ", stumpedPoints=" + stumpedPoints +
                '}';
    }
}
