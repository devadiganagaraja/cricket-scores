package edu.cricket.api.cricketscores.domain;

import edu.cricket.api.cricketscores.rest.response.model.PlayerPoints;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "event_player_points")
public class EventPlayerPointsAggregate {
    @Id
    private String id;

    private Map<Long, PlayerPoints> playerPointsMap;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<Long, PlayerPoints> getPlayerPointsMap() {
        return playerPointsMap;
    }

    public void setPlayerPointsMap(Map<Long, PlayerPoints> playerPointsMap) {
        this.playerPointsMap = playerPointsMap;
    }

    @Override
    public String toString() {
        return "EventPlayerPointsAggregate{" +
                "id='" + id + '\'' +
                ", playerPointsMap=" + playerPointsMap +
                '}';
    }
}
