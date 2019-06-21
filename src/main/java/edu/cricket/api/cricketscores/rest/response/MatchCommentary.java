package edu.cricket.api.cricketscores.rest.response;

import java.util.Set;
import java.util.TreeSet;

public class MatchCommentary {
    private String eventId;
    private Set<InningsCommentary> inningsCommentary = new TreeSet<>();

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Set<InningsCommentary> getInningsCommentary() {
        return inningsCommentary;
    }

    public void setInningsCommentary(Set<InningsCommentary> inningsCommentary) {
        this.inningsCommentary = inningsCommentary;
    }


    @Override
    public String toString() {
        return "MatchCommentary{" +
                "eventId='" + eventId + '\'' +
                ", inningsCommentary=" + inningsCommentary +
                '}';
    }
}
