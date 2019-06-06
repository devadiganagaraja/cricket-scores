package edu.cricket.api.cricketscores.rest.response.model;

public class EventInfo {
    private Event event;
    private ScoreCard scoreCard;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public ScoreCard getScoreCard() {
        return scoreCard;
    }

    public void setScoreCard(ScoreCard scoreCard) {
        this.scoreCard = scoreCard;
    }
}
