package edu.cricket.api.cricketscores.rest.source.model;

import java.util.List;

public class MatchStatus {
    private String $ref;
    private List<FeaturedAthlete> featuredAthletes;
    private int period;
    private int dayNumber;
    private Type type;

    public String get$ref() {
        return $ref;
    }

    public void set$ref(String $ref) {
        this.$ref = $ref;
    }

    public List<FeaturedAthlete> getFeaturedAthletes() {
        return featuredAthletes;
    }

    public void setFeaturedAthletes(List<FeaturedAthlete> featuredAthletes) {
        this.featuredAthletes = featuredAthletes;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
