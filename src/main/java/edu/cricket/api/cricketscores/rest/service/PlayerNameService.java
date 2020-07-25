package edu.cricket.api.cricketscores.rest.service;

import edu.cricket.api.cricketscores.rest.source.model.Athlete;
import edu.cricket.api.cricketscores.rest.source.model.Style;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
public class PlayerNameService {

    RestTemplate restTemplate = new RestTemplate();

    public long getPlayerId(long sourcePlayerId){
        return sourcePlayerId*13;

    }

    public long getPlayerId(String playerName){
        if(StringUtils.isNotBlank(playerName)){
            return  Long.valueOf(playerName.split(":")[1]);
        }
        return 0;

    }

    public String getPlayerName(Long sourcePlayerName){
        long playerId = getPlayerId(sourcePlayerName);
        try {

            Athlete athlete = restTemplate.getForObject("http://new.core.espnuk.org/v2/sports/cricket/athletes/"+sourcePlayerName, Athlete.class);
            String displayNameWithId = athlete.getDisplayName()+":"+(playerId);
            if(null != athlete.getPosition()){
                displayNameWithId  = displayNameWithId.concat(":").concat(getBattingStyle(athlete.getPosition().getName()));
            }
            return displayNameWithId;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "null:0:null";
    }

    public static  String getBattingStyle(String type) {
        if(StringUtils.isNotBlank(type)){
            if(type.toLowerCase().contains("batsman")) return "Batsman";
            if(type.toLowerCase().contains("bowler")) return "Bowler";
            if(type.toLowerCase().contains("wicketkeeper")) return "Wicketkeeper";

        }
        return "Allrounder";

    }



}
