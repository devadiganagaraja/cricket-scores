package edu.cricket.api.cricketscores.rest.source.model;

public class LineScoreStatistics {
    private Splits splits;

    private Ref team;

    public Splits getSplits() {
        return splits;
    }

    public void setSplits(Splits splits) {
        this.splits = splits;
    }

    public Ref getTeam() {
        return team;
    }

    public void setTeam(Ref team) {
        this.team = team;
    }
}
