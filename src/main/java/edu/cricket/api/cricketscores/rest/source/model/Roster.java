package edu.cricket.api.cricketscores.rest.source.model;

import java.util.List;

public class Roster {

    private List<PlayerRoster> entries;

    public List<PlayerRoster> getEntries() {
        return entries;
    }

    public void setEntries(List<PlayerRoster> entries) {
        this.entries = entries;
    }

    @Override
    public String toString() {
        return "Roster{" +
                "entries=" + entries +
                '}';
    }
}
