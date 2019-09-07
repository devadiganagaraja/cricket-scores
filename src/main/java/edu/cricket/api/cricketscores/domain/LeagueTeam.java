package edu.cricket.api.cricketscores.domain;

import java.util.HashSet;
import java.util.Set;

public class LeagueTeam {
    private String displayName;

    private long id;

    private Set<String> won = new HashSet<>();

    private Set<String> lost = new HashSet<>();;

    private Set<String> drawn = new HashSet<>();;


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<String> getWon() {
        return won;
    }

    public void setWon(Set<String> won) {
        this.won = won;
    }

    public Set<String> getLost() {
        return lost;
    }

    public void setLost(Set<String> lost) {
        this.lost = lost;
    }

    public Set<String> getDrawn() {
        return drawn;
    }

    public void setDrawn(Set<String> drawn) {
        this.drawn = drawn;
    }
}
