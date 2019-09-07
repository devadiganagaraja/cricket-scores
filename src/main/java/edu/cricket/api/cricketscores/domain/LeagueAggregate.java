package edu.cricket.api.cricketscores.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "leagues")
public class LeagueAggregate {
    @Id
    private String id;

    private LeagueInfo leagueInfo;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LeagueInfo getLeagueInfo() {
        return leagueInfo;
    }

    public void setLeagueInfo(LeagueInfo leagueInfo) {
        this.leagueInfo = leagueInfo;
    }
}
