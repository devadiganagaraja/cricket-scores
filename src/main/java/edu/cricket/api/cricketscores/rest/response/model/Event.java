package edu.cricket.api.cricketscores.rest.response.model;

public class Event {
    private String venue;
    private String eventId;
    private String startDate;
    private Competitor team1;
    private Competitor team2;
    private String type;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Competitor getTeam1() {
        return team1;
    }

    public void setTeam1(Competitor team1) {
        this.team1 = team1;
    }

    public Competitor getTeam2() {
        return team2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTeam2(Competitor team2) {
        this.team2 = team2;
    }
}
