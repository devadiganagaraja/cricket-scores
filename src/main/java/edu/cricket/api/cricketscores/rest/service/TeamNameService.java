package edu.cricket.api.cricketscores.rest.service;

import edu.cricket.api.cricketscores.rest.source.model.Athlete;
import edu.cricket.api.cricketscores.rest.source.model.Team;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class TeamNameService {

    ConcurrentMap<Long,String> teamNameCache = new ConcurrentHashMap<>();

    RestTemplate restTemplate = new RestTemplate();


    public long getTeamId(long sourceTeamId){
        return sourceTeamId*13;

    }
    public String getTeamName(Long sourceTeamId){
        long teamId = getTeamId(sourceTeamId);
        try {

            if (teamNameCache.containsKey(teamId)) {
                return teamNameCache.get(teamId);
            } else {
                Team team = restTemplate.getForObject("http://core.espnuk.org/v2/sports/cricket/teams/" + sourceTeamId, Team.class);
                String displayNameWithId = team.getDisplayName() + ":" + teamId;

                teamNameCache.putIfAbsent(teamId, displayNameWithId);
                return displayNameWithId;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "null:0";
    }


    public String getTeamNameByTeamId(Long teamId){
        try {

            if (teamNameCache.containsKey(teamId)) {
                return teamNameCache.get(teamId);
            } else {
                Team team = restTemplate.getForObject("http://core.espnuk.org/v2/sports/cricket/teams/" + (teamId/13), Team.class);
                String displayNameWithId = team.getDisplayName() + ":" + teamId;

                teamNameCache.putIfAbsent(teamId, displayNameWithId);
                return displayNameWithId;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "null:0";
    }
}
