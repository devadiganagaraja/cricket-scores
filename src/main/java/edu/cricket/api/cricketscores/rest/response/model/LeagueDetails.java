package edu.cricket.api.cricketscores.rest.response.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.cricket.api.cricketscores.domain.LeagueSeason;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeagueDetails {
    private String leagueId;
    private String leagueName;
    private List<LeagueSeason> leagueSeasons;


    public String getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(String leagueId) {
        this.leagueId = leagueId;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public List<LeagueSeason> getLeagueSeasons() {
        return leagueSeasons;
    }

    public void setLeagueSeasons(List<LeagueSeason> leagueSeasons) {
        this.leagueSeasons = leagueSeasons;
    }
}
