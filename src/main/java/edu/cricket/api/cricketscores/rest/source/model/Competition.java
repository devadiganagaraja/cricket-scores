package edu.cricket.api.cricketscores.rest.source.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Competition {
    private List<Competitor> competitors;
    private String id;

    @JsonProperty(value = "class")
    private CompetitionClass competitionClass;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Competitor> getCompetitors() {
        return competitors;
    }

    public void setCompetitors(List<Competitor> competitors) {
        this.competitors = competitors;
    }


    public CompetitionClass getCompetitionClass() {
        return competitionClass;
    }

    public void setCompetitionClass(CompetitionClass competitionClass) {
        this.competitionClass = competitionClass;
    }

    @Override
    public String toString() {
        return "Competition{" +
                "competitors=" + competitors +
                '}';
    }
}
