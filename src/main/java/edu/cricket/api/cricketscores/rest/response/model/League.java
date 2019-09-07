package edu.cricket.api.cricketscores.rest.response.model;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class League {
    private long leagueId;
    private String leagueName;
    private String leagueStartDate;
    private String leagueEndDate;
    private String leagueYear;
    private Set<Event> eventSet;
    private int classId;

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


    public Set<Event> getEventSet() {
        return eventSet;
    }

    public void setEventSet(Set<Event> eventSet) {
        this.eventSet = eventSet;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    @Override
    public String toString() {
        return "League{" +
                "leagueId=" + leagueId +
                ", leagueName='" + leagueName + '\'' +
                ", leagueStartDate='" + leagueStartDate + '\'' +
                ", leagueEndDate='" + leagueEndDate + '\'' +
                ", leagueYear=" + leagueYear +
                ", eventSet=" + eventSet +
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
