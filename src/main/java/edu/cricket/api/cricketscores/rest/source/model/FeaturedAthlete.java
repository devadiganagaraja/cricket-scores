package edu.cricket.api.cricketscores.rest.source.model;

public class FeaturedAthlete {
    private String abbreviation;
    private long playerId;

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }
}
