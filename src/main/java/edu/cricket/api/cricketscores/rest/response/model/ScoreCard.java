package edu.cricket.api.cricketscores.rest.response.model;

import com.cricketfoursix.cricketdomain.common.game.InningsScoreCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoreCard {
    private String eventId;
    private List<String> competitors = new ArrayList<>();
    private Map<Integer, InningsScoreCard> inningsScores;

    public Map<Integer, InningsScoreCard> getInningsScores() {
        return inningsScores;
    }

    public void setInningsScores(Map<Integer, InningsScoreCard> inningsScores) {
        this.inningsScores = inningsScores;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public List<String> getCompetitors() {
        return competitors;
    }

    public void setCompetitors(List<String> competitors) {
        this.competitors = competitors;
    }
}
