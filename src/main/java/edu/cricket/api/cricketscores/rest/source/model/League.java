package edu.cricket.api.cricketscores.rest.source.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class League {
    private String id;
    private String name;
    private String shortName;
    private Ref season;
    private Ref teams;
    private String seriesNote;

    @JsonProperty(value = "isTournament")
    private boolean isTournament;
    private List<String> classId;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Ref getSeason() {
        return season;
    }

    public void setSeason(Ref season) {
        this.season = season;
    }

    public Ref getTeams() {
        return teams;
    }

    public String getId() {
        return id;
    }

    public String getSeriesNote() {
        return seriesNote;
    }

    public void setSeriesNote(String seriesNote) {
        this.seriesNote = seriesNote;
    }

    public List<String> getClassId() {
        return classId;
    }

    public void setClassId(List<String> classId) {
        this.classId = classId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTeams(Ref teams) {
        this.teams = teams;
    }


    public boolean isTournament() {
        return isTournament;
    }

    public void setTournament(boolean tournament) {
        isTournament = tournament;
    }

    @Override
    public String toString() {
        return "League{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", season=" + season +
                ", teams=" + teams +
                ", isTournament=" + isTournament +
                ", classId=" + classId +
                '}';
    }
}
