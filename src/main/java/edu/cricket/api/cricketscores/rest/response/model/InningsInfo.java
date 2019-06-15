package edu.cricket.api.cricketscores.rest.response.model;

public class InningsInfo {
    private String runs;
    private String wickets;
    private String overs;
    private String overLimit;
    private String runRate;
    private String target;
    private String legByes;
    private String byes;
    private String noBalls;
    private String wides;
    private String lead;

    public String getLead() {
        return lead;
    }

    public void setLead(String lead) {
        this.lead = lead;
    }

    public String getRuns() {
        return runs;
    }

    public void setRuns(String runs) {
        this.runs = runs;
    }

    public String getWickets() {
        return wickets;
    }

    public void setWickets(String wickets) {
        this.wickets = wickets;
    }

    public String getOvers() {
        return overs;
    }

    public void setOvers(String overs) {
        this.overs = overs;
    }

    public String getOverLimit() {
        return overLimit;
    }

    public void setOverLimit(String overLimit) {
        this.overLimit = overLimit;
    }

    public String getRunRate() {
        return runRate;
    }

    public void setRunRate(String runRate) {
        this.runRate = runRate;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getLegByes() {
        return legByes;
    }

    public void setLegByes(String legByes) {
        this.legByes = legByes;
    }

    public String getByes() {
        return byes;
    }

    public void setByes(String byes) {
        this.byes = byes;
    }

    public String getNoBalls() {
        return noBalls;
    }

    public void setNoBalls(String noBalls) {
        this.noBalls = noBalls;
    }

    public String getWides() {
        return wides;
    }

    public void setWides(String wides) {
        this.wides = wides;
    }
}
