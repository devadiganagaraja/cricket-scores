package edu.cricket.api.cricketscores.domain;

import java.util.HashMap;
import java.util.Map;

public class LeagueInfo {
    private String leagueName;
    private String tournament;
    private Map<String, LeagueSeason> leagueSeasonMap = new HashMap<>();

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public Map<String, LeagueSeason> getLeagueSeasonMap() {
        return leagueSeasonMap;
    }

    public void setLeagueSeasonMap(Map<String, LeagueSeason> leagueSeasonMap) {
        this.leagueSeasonMap = leagueSeasonMap;
    }

    public String getTournament() {
        return tournament;
    }

    public void setTournament(String tournament) {
        this.tournament = tournament;
    }
}
