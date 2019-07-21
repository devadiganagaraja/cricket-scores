package edu.cricket.api.cricketscores.rest.service;

import edu.cricket.api.cricketscores.rest.source.model.Athlete;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class PlayerNameService {

    ConcurrentMap<Long,String> playerNameCache = new ConcurrentHashMap<>();

    RestTemplate restTemplate = new RestTemplate();

    public long getPlayerId(long sourcePlayerId){
        return sourcePlayerId*13;

    }


    public String getPlayerName(Long sourcePlayerName){
        long playerId = getPlayerId(sourcePlayerName);
        try {

            if(playerNameCache.containsKey(playerId)){
                return playerNameCache.get(playerId);
            }else{
                Athlete athlete = restTemplate.getForObject("http://core.espnuk.org/v2/sports/cricket/athletes/"+sourcePlayerName, Athlete.class);
                String displayNameWithId = athlete.getDisplayName()+":"+(playerId);
                playerNameCache.putIfAbsent(playerId, displayNameWithId);
                return displayNameWithId;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "null:0";
    }

}
