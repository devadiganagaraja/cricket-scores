package edu.cricket.api.cricketscores.task;

import edu.cricket.api.cricketscores.rest.response.model.Event;
import edu.cricket.api.cricketscores.rest.response.model.SquadPlayer;
import edu.cricket.api.cricketscores.rest.service.PlayerNameService;
import edu.cricket.api.cricketscores.rest.source.model.EventListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventSquadsTask {

    private static final Logger logger = LoggerFactory.getLogger(EventSquadsTask.class);


    @Autowired
    Map<String,Event> liveEvents;

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    PlayerNameService playerNameService;




    private static final Logger log = LoggerFactory.getLogger(EventSquadsTask.class);

    public List<SquadPlayer> getLeagueTeamPlayers(long leagueId, long teamId, int classId) {
        String ref = "http://core.espnuk.org/v2/sports/cricket/leagues/"+leagueId+"/teams/"+teamId+"/athletes?internationalClassId="+classId;
        EventListing athleteListing = restTemplate.getForObject(ref , EventListing.class);

        logger.info("ref : {}, athleteListing:{}",ref);
        return athleteListing.getItems().stream().map($ref -> new SquadPlayer(playerNameService.getPlayerName(Long.valueOf($ref.get$ref().split("athletes/")[1])))).collect(Collectors.toList());
    }
}
