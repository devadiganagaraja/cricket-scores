package edu.cricket.api.cricketscores.rest.source.model;

import java.util.List;

public class LeagueSeasonGroupStandings {
    private List<LeagueSeasonGroupStanding> standings;

    public List<LeagueSeasonGroupStanding> getStandings() {
        return standings;
    }

    public void setStandings(List<LeagueSeasonGroupStanding> standings) {
        this.standings = standings;
    }
}
