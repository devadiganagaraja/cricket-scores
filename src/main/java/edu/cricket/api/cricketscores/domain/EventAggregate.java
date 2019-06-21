package edu.cricket.api.cricketscores.domain;

import edu.cricket.api.cricketscores.rest.response.model.Event;
import edu.cricket.api.cricketscores.rest.response.model.ScoreCard;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "events")
public class EventAggregate {
    @Id
    private String id;

    private Event eventInfo;
    private ScoreCard scoreCard;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Event getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(Event eventInfo) {
        this.eventInfo = eventInfo;
    }

    public ScoreCard getScoreCard() {
        return scoreCard;
    }

    public void setScoreCard(ScoreCard scoreCard) {
        this.scoreCard = scoreCard;
    }

    @Override
    public String toString() {
        return "EventAggregate{" +
                "id=" + id +
                ", eventInfo=" + eventInfo +
                ", scoreCard=" + scoreCard +
                '}';
    }
}
