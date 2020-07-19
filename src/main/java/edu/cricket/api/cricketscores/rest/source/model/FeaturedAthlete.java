package edu.cricket.api.cricketscores.rest.source.model;

public class FeaturedAthlete {
    private String displayName;
    private String playerId;
    private Ref team;


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public Ref getTeam() {
        return team;
    }

    public void setTeam(Ref team) {
        this.team = team;
    }
}
