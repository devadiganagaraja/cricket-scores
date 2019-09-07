package edu.cricket.api.cricketscores.domain;

import edu.cricket.api.cricketscores.rest.response.model.Event;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class LeagueSeason {
    private String leagueYear;
    private Set<Event> eventSet = new HashSet<>();
    private Set<LeagueTeam> teams;
    private Set<Event> postEvents = new HashSet<>();
    private Set<Event> nextEvents = new HashSet<>();
    private Set<Event> liveEvents = new HashSet<>();
    private List<TeamStat> TeamStats;
    private Set<BattingLeader> BattingLeaders;
    private Set<BowlingLeader> BowlingLeaders;


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

    public Set<LeagueTeam> getTeams() {
        return teams;
    }

    public void setTeams(Set<LeagueTeam> teams) {
        this.teams = teams;
    }

    public Set<Event> getPostEvents() {
        return postEvents;
    }

    public void setPostEvents(Set<Event> postEvents) {
        this.postEvents = postEvents;
    }

    public Set<Event> getNextEvents() {
        return nextEvents;
    }

    public void setNextEvents(Set<Event> nextEvents) {
        this.nextEvents = nextEvents;
    }

    public List<TeamStat> getTeamStats() {
        return TeamStats;
    }

    public void setTeamStats(List<TeamStat> teamStats) {
        TeamStats = teamStats;
    }

    public Set<BattingLeader> getBattingLeaders() {
        return BattingLeaders;
    }

    public void setBattingLeaders(Set<BattingLeader> battingLeaders) {
        BattingLeaders = battingLeaders;
    }

    public Set<BowlingLeader> getBowlingLeaders() {
        return BowlingLeaders;
    }

    @Override
    public String toString() {
        return "LeagueSeason{" +
                "leagueYear='" + leagueYear + '\'' +
                ", eventSet=" + eventSet +
                ", teams=" + teams +
                ", postEvents=" + postEvents +
                ", nextEvents=" + nextEvents +
                ", liveEvents=" + liveEvents +
                ", TeamStats=" + TeamStats +
                ", BattingLeaders=" + BattingLeaders +
                ", BowlingLeaders=" + BowlingLeaders +
                '}';
    }

    public void setBowlingLeaders(Set<BowlingLeader> bowlingLeaders) {
        BowlingLeaders = bowlingLeaders;
    }


    public Set<Event> getLiveEvents() {
        return liveEvents;
    }

    public void setLiveEvents(Set<Event> liveEvents) {
        this.liveEvents = liveEvents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeagueSeason that = (LeagueSeason) o;
        return Objects.equals(leagueYear, that.leagueYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leagueYear);
    }
}
