package edu.cricket.api.cricketscores.rest.response.model;

import edu.cricket.api.cricketscores.rest.source.model.Type;

import java.util.Objects;

public class Event {
    private String venue;
    private String eventId;
    private String startDate;
    private String endDate;
    private Competitor team1;
    private Competitor team2;
    private String type;
    private String note;
    private String manOfTheMatch;
    private int period;
    private int dayNumber;
    private String description;
    private String detail;
    private String state;
    private long leagueId;
    private String leagueName;
    private String leagueStartDate;
    private String leagueEndDate;
    private String leagueYear;
    private int internationalClassId;
    private int generalClassId;


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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getManOfTheMatch() {
        return manOfTheMatch;
    }

    public void setManOfTheMatch(String manOfTheMatch) {
        this.manOfTheMatch = manOfTheMatch;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(long leagueId) {
        this.leagueId = leagueId;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public String getLeagueStartDate() {
        return leagueStartDate;
    }

    public void setLeagueStartDate(String leagueStartDate) {
        this.leagueStartDate = leagueStartDate;
    }

    public String getLeagueEndDate() {
        return leagueEndDate;
    }

    public void setLeagueEndDate(String leagueEndDate) {
        this.leagueEndDate = leagueEndDate;
    }

    public String getLeagueYear() {
        return leagueYear;
    }

    public void setLeagueYear(String leagueYear) {
        this.leagueYear = leagueYear;
    }


    public int getInternationalClassId() {
        return internationalClassId;
    }

    public void setInternationalClassId(int internationalClassId) {
        this.internationalClassId = internationalClassId;
    }



    public String getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return "Event{" +
                "venue='" + venue + '\'' +
                ", eventId='" + eventId + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", team1=" + team1 +
                ", team2=" + team2 +
                ", type='" + type + '\'' +
                ", note='" + note + '\'' +
                ", manOfTheMatch='" + manOfTheMatch + '\'' +
                ", period=" + period +
                ", dayNumber=" + dayNumber +
                ", description='" + description + '\'' +
                ", detail='" + detail + '\'' +
                ", state='" + state + '\'' +
                ", leagueId=" + leagueId +
                ", leagueName='" + leagueName + '\'' +
                ", leagueStartDate='" + leagueStartDate + '\'' +
                ", leagueEndDate='" + leagueEndDate + '\'' +
                ", leagueYear='" + leagueYear + '\'' +
                ", internationalClassId=" + internationalClassId +
                ", generalClassId=" + generalClassId +
                '}';
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(eventId, event.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    public int getGeneralClassId() {
        return generalClassId;
    }

    public void setGeneralClassId(int generalClassId) {
        this.generalClassId = generalClassId;
    }

}
