package edu.cricket.api.cricketscores.rest.source.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Competition {
    private List<Competitor> competitors;
    private String id;
    private String note;
    private Ref status;

    private Ref details;
    private Ref tiebreaker;

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

    public String getNote() {
        return note;
    }

    public Ref getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return "Competition{" +
                "competitors=" + competitors +
                ", id='" + id + '\'' +
                ", note='" + note + '\'' +
                ", status=" + status +
                ", details=" + details +
                ", tiebreaker=" + tiebreaker +
                ", competitionClass=" + competitionClass +
                '}';
    }

    public void setDetails(Ref details) {
        this.details = details;
    }

    public Ref getTiebreaker() {
        return tiebreaker;
    }

    public void setTiebreaker(Ref tiebreaker) {
        this.tiebreaker = tiebreaker;
    }

    public void setNote(String note) {
        this.note = note;
    }


    public Ref getStatus() {
        return status;
    }

    public void setStatus(Ref status) {
        this.status = status;
    }

}