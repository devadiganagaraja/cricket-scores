package edu.cricket.api.cricketscores.rest.source.model;

import java.util.List;

public class EventStatus {
    private List<FeaturedAthlete> featuredAthletes;
    private String summary;

    private EventStatusType type;


    public List<FeaturedAthlete> getFeaturedAthletes() {
        return featuredAthletes;
    }

    public void setFeaturedAthletes(List<FeaturedAthlete> featuredAthletes) {
        this.featuredAthletes = featuredAthletes;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public EventStatusType getType() {
        return type;
    }

    public void setType(EventStatusType type) {
        this.type = type;
    }
}
