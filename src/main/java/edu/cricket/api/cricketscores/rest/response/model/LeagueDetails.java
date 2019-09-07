package edu.cricket.api.cricketscores.rest.response.model;

import edu.cricket.api.cricketscores.domain.LeagueSeason;

import java.util.List;

public class LeagueDetails {
    private String leagueName;
    private String leagueStartDate;
    private String leagueEndDate;
    private List<LeagueSeason> leagueSeasons;

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

    public List<LeagueSeason> getLeagueSeasons() {
        return leagueSeasons;
    }

    public void setLeagueSeasons(List<LeagueSeason> leagueSeasons) {
        this.leagueSeasons = leagueSeasons;
    }
}
