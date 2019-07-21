package edu.cricket.api.cricketscores.rest.response.model;

import java.util.ArrayList;
import java.util.List;

public class UserSquad {

    List<UserSquadPlayer> userSquadPlayers = new ArrayList<>();
    int totalPoints = 100;

    public List<UserSquadPlayer> getUserSquadPlayers() {
        return userSquadPlayers;
    }

    public void setUserSquadPlayers(List<UserSquadPlayer> userSquadPlayers) {
        this.userSquadPlayers = userSquadPlayers;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }
}
