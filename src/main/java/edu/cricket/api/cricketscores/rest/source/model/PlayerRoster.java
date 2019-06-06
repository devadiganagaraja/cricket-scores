package edu.cricket.api.cricketscores.rest.source.model;

public class PlayerRoster {
    private long playerId;
    private String activeName;
    private String starter;
    private Ref linescores;
    private Ref athlete;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getActiveName() {
        return activeName;
    }

    public void setActiveName(String activeName) {
        this.activeName = activeName;
    }

    public String getStarter() {
        return starter;
    }

    public void setStarter(String starter) {
        this.starter = starter;
    }

    public Ref getLinescores() {
        return linescores;
    }

    public void setLinescores(Ref linescores) {
        this.linescores = linescores;
    }

    public Ref getAthlete() {
        return athlete;
    }

    public void setAthlete(Ref athlete) {
        this.athlete = athlete;
    }

    @Override
    public String toString() {
        return "PlayerRoster{" +
                "playerId=" + playerId +
                ", activeName='" + activeName + '\'' +
                ", starter='" + starter + '\'' +
                ", linescores=" + linescores +
                ", athlete=" + athlete +
                '}';
    }
}
