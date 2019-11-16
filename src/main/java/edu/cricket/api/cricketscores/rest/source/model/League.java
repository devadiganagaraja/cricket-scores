package edu.cricket.api.cricketscores.rest.source.model;

public class League {
    private String name;
    private String shortName;
    private Ref season;
    private Ref teams;
    private Boolean tournament;


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

    public void setTeams(Ref teams) {
        this.teams = teams;
    }

    public Boolean getTournament() {
        return tournament;
    }

    public void setTournament(Boolean tournament) {
        this.tournament = tournament;
    }

    @Override
    public String toString() {
        return "League{" +
                "name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", season=" + season +
                ", teams=" + teams +
                ", tournament=" + tournament +
                '}';
    }
}
