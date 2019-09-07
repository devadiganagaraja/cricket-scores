package edu.cricket.api.cricketscores.rest.response.model;

import java.util.ArrayList;
import java.util.List;

public class UserSquad {

    List<UserSquadPlayer> userSquadPlayers = new ArrayList<>();
    float totalPoints;

    public List<UserSquadPlayer> getUserSquadPlayers() {
        return userSquadPlayers;
    }

    public void setUserSquadPlayers(List<UserSquadPlayer> userSquadPlayers) {
        this.userSquadPlayers = userSquadPlayers;
    }

    public float getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(float totalPoints) {
        this.totalPoints = totalPoints;
    }
}
