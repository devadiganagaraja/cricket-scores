package edu.cricket.api.cricketscores.rest.response;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

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


    public int getBallCount() {
        AtomicInteger ballCount = new AtomicInteger(0);
        if(null != inningsCommentary){
            inningsCommentary.forEach(iCommentary -> {
                if(null != iCommentary && null != iCommentary.getOverCommentarySet()){
                    iCommentary.getOverCommentarySet().forEach(overCommentary -> {
                        if(null != overCommentary && null != overCommentary.getBallCommentarySet()){
                            ballCount.addAndGet(overCommentary.getBallCommentarySet().size());
                        }
                    });
                }
            });

        }
        return ballCount.get();
    }


    @Override
    public String toString() {
        return "MatchCommentary{" +
                "eventId='" + eventId + '\'' +
                ", inningsCommentary=" + inningsCommentary +
                '}';
    }
}
