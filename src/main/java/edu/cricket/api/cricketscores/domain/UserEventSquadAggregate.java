package edu.cricket.api.cricketscores.domain;

import edu.cricket.api.cricketscores.rest.response.model.UserSquadPlayer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "userEventSquad")
public class UserEventSquadAggregate {
    @Id
    private String userEventId;
    private String eventId;
    private String userName;
    private List<UserSquadPlayer> userSquadPlayers;

    public String getUserEventId() {
        return userEventId;
    }


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEventId(String userEventId) {
        this.userEventId = userEventId;
    }

    public List<UserSquadPlayer> getUserSquadPlayers() {
        return userSquadPlayers;
    }

    public void setUserSquadPlayers(List<UserSquadPlayer> userSquadPlayers) {
        this.userSquadPlayers = userSquadPlayers;
    }
}
