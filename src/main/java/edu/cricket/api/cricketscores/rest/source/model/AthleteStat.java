package edu.cricket.api.cricketscores.rest.source.model;

public class AthleteStat {
    private Splits splits;

    public Splits getSplits() {
        return splits;
    }

    public void setSplits(Splits splits) {
        this.splits = splits;
    }

    @Override
    public String toString() {
        return "AthleteStat{" +
                "splits=" + splits +
                '}';
    }
}
