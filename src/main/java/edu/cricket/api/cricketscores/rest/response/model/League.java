package edu.cricket.api.cricketscores.rest.response.model;

import java.util.List;
import java.util.Objects;

public class League {
    private long leagueId;
    private String leagueName;
    private String leagueStartDate;
    private String leagueEndDate;
    private int leagueYear;
    private List<Event> eventList;

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

    public int getLeagueYear() {
        return leagueYear;
    }

    public void setLeagueYear(int leagueYear) {
        this.leagueYear = leagueYear;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }


    @Override
    public String toString() {
        return "League{" +
                "leagueId=" + leagueId +
                ", leagueName='" + leagueName + '\'' +
                ", leagueStartDate='" + leagueStartDate + '\'' +
                ", leagueEndDate='" + leagueEndDate + '\'' +
                ", leagueYear=" + leagueYear +
                ", eventList=" + eventList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        League league = (League) o;
        return leagueId == league.leagueId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(leagueId);
    }
}
