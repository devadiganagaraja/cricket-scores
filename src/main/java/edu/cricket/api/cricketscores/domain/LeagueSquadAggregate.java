package edu.cricket.api.cricketscores.domain;

import edu.cricket.api.cricketscores.rest.response.model.Squad;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "league_squad")
public class LeagueSquadAggregate {
    @Id
    private String id;

    private Set<Squad> squads;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<Squad> getSquads() {
        return squads;
    }

    public void setSquads(Set<Squad> squads) {
        this.squads = squads;
    }
}
