package edu.cricket.api.cricketscores.rest.source.model;

import java.util.List;

public class LeagueSeasonGroupStanding {
    private Ref team;
    private List<LeagueSeasonGroupStandingRecord> records;

    public Ref getTeam() {
        return team;
    }

    public void setTeam(Ref team) {
        this.team = team;
    }

    public List<LeagueSeasonGroupStandingRecord> getRecords() {
        return records;
    }

    public void setRecords(List<LeagueSeasonGroupStandingRecord> records) {
        this.records = records;
    }
}
