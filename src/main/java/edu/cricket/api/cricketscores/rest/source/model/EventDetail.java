package edu.cricket.api.cricketscores.rest.source.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDetail {
    private String date;
    private List<Competition> competitions;
    private List<Ref> venues;
    private Ref season;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Competition> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(List<Competition> competitions) {
        this.competitions = competitions;
    }

    public List<Ref> getVenues() {
        return venues;
    }

    public void setVenues(List<Ref> venues) {
        this.venues = venues;
    }

    public Ref getSeason() {
        return season;
    }

    public void setSeason(Ref season) {
        this.season = season;
    }

    @Override
    public String toString() {
        return "EventDetail{" +
                "date='" + date + '\'' +
                ", competitions=" + competitions +
                ", venues=" + venues +
                ", season=" + season +
                '}';
    }
}
