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


    public String getPlayerName(Long playerId){
        if(playerNameCache.containsKey(playerId)){
            return playerNameCache.get(playerId);
        }else{
            Athlete athlete = restTemplate.getForObject("http://core.espnuk.org/v2/sports/cricket/athletes/"+playerId, Athlete.class);
            playerNameCache.putIfAbsent(playerId, athlete.getDisplayName()+(playerId*13));
            return athlete.getDisplayName();
        }
    }

}
