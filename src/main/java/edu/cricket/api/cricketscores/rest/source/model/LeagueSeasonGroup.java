package edu.cricket.api.cricketscores.rest.source.model;

public class LeagueSeasonGroup {
    private String id;
    private String name;
    private String abbreviation;
    private Ref standings;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Ref getStandings() {
        return standings;
    }

    public void setStandings(Ref standings) {
        this.standings = standings;
    }
}
