package edu.cricket.api.cricketscores.rest.response.model;

import java.util.Map;

public class ScoreCard {
    private Map<Integer, InningsScoreCard> inningsScores;

    public Map<Integer, InningsScoreCard> getInningsScores() {
        return inningsScores;
    }

    public void setInningsScores(Map<Integer, InningsScoreCard> inningsScores) {
        this.inningsScores = inningsScores;
    }
}
