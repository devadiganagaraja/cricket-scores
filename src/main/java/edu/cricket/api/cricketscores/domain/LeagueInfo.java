package edu.cricket.api.cricketscores.domain;

import java.util.Map;

public class LeagueInfo {
    private String leagueName;
    private String leagueStartDate;
    private String leagueEndDate;
    private Map<String, LeagueSeason> leagueSeasonMap;

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public String getLeagueStartDate() {
        return leagueStartDate;
    }

    public void setLeagueStartDate(String leagueStartDate) {
        this.leagueStartDate = leagueStartDate;
    }

    public String getLeagueEndDate() {
        return leagueEndDate;
    }

    public void setLeagueEndDate(String leagueEndDate) {
        this.leagueEndDate = leagueEndDate;
    }

    public Map<String, LeagueSeason> getLeagueSeasonMap() {
        return leagueSeasonMap;
    }

    public void setLeagueSeasonMap(Map<String, LeagueSeason> leagueSeasonMap) {
        this.leagueSeasonMap = leagueSeasonMap;
    }
}
