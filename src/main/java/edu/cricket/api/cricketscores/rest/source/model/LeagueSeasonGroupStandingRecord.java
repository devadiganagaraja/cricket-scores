package edu.cricket.api.cricketscores.rest.source.model;

import java.util.List;

public class LeagueSeasonGroupStandingRecord {
    private List<LeagueSeasonGroupStandingRecordStat> stats;

    public List<LeagueSeasonGroupStandingRecordStat> getStats() {
        return stats;
    }

    public void setStats(List<LeagueSeasonGroupStandingRecordStat> stats) {
        this.stats = stats;
    }
}
