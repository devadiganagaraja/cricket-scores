package edu.cricket.api.cricketscores.rest.response.model;

import com.cricketfoursix.cricketdomain.common.squad.PlayerPoints;

public class UserSquadPlayer{
    private String playerName;
    private String teamName;
    private boolean isCaptain;
    private boolean isVoiceCaptain;
    private PlayerPoints points;


    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }


    public boolean isCaptain() {
        return isCaptain;
    }

    public void setCaptain(boolean captain) {
        isCaptain = captain;
    }

    public boolean isVoiceCaptain() {
        return isVoiceCaptain;
    }

    public void setVoiceCaptain(boolean voiceCaptain) {
        isVoiceCaptain = voiceCaptain;
    }

    public PlayerPoints getPoints() {
        return points;
    }

    public void setPoints(PlayerPoints points) {
        this.points = points;
    }
}
