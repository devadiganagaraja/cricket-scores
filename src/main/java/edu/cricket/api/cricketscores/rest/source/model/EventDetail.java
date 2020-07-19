package edu.cricket.api.cricketscores.rest.source.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDetail {
    private Date date;
    private Date endDate;
    private List<Competition> competitions;
    private Ref season;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<Competition> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(List<Competition> competitions) {
        this.competitions = competitions;
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
                ", season=" + season +
                '}';
    }
}
