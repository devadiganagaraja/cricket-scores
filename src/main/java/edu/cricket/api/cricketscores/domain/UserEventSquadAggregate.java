package edu.cricket.api.cricketscores.domain;

import edu.cricket.api.cricketscores.rest.response.model.UserSquadPlayer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "userEventSquad")
public class UserEventSquadAggregate {
    @Id
    private String userEventId;
    private List<UserSquadPlayer> userSquadPlayers;

    public String getUserEventId() {
        return userEventId;
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
