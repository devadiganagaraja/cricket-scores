package edu.cricket.api.cricketscores.task;

import edu.cricket.api.cricketscores.domain.EventAggregate;
import edu.cricket.api.cricketscores.domain.LeagueSquadAggregate;
import edu.cricket.api.cricketscores.repository.LeagueSquadRepository;
import edu.cricket.api.cricketscores.rest.response.model.Event;
import edu.cricket.api.cricketscores.rest.response.model.EventInfo;
import edu.cricket.api.cricketscores.rest.response.model.Squad;
import edu.cricket.api.cricketscores.rest.response.model.SquadPlayer;
import edu.cricket.api.cricketscores.rest.service.PlayerNameService;
import edu.cricket.api.cricketscores.rest.service.TeamNameService;
import edu.cricket.api.cricketscores.rest.source.model.EventListing;
import edu.cricket.api.cricketscores.rest.source.model.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventSquadsTask {

    private static final Logger logger = LoggerFactory.getLogger(EventSquadsTask.class);


    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    PlayerNameService playerNameService;

    @Autowired
    LeagueSquadRepository leagueSquadRepository;

    @Autowired
    TeamNameService teamNameService;




    private static final Logger log = LoggerFactory.getLogger(EventSquadsTask.class);

    public List<SquadPlayer> getLeagueTeamPlayers(long leagueId, long teamId, Event eventInfo) {
        Optional<LeagueSquadAggregate> leagueSquadAggregateOptional = leagueSquadRepository.findById(String.valueOf(leagueId));
        if(leagueSquadAggregateOptional.isPresent()){
            Optional<Squad> squadOptional = leagueSquadAggregateOptional.get().getSquads().stream().filter(squad -> squad.getTeamName().split(":")[1].equalsIgnoreCase(String.valueOf(teamId*13))).findFirst();
            if(squadOptional.isPresent()) {
                return squadOptional.get().getPlayers();
            }

        }
        String ref = "http://core.espnuk.org/v2/sports/cricket/leagues/" + (leagueId/13) + "/teams/" + teamId + "/athletes";
        if(eventInfo.getInternationalClassId() > 0)
            ref = ref+ "?internationalClassId=" + eventInfo.getInternationalClassId();
        else if(eventInfo.getInternationalClassId() > 0){
            ref = ref+ "?generalClassId=" + eventInfo.getGeneralClassId();
        }

        EventListing athleteListing = restTemplate.getForObject(ref, EventListing.class);

        logger.info("ref : {}, athleteListing:{}", ref);
        List<SquadPlayer>  squadPlayers =  athleteListing.getItems().stream().map($ref -> new SquadPlayer(playerNameService.getPlayerName(Long.valueOf(getPlayerIdFromUrl($ref))))).collect(Collectors.toList());
        LeagueSquadAggregate leagueSquadAggregate = null;
        if(leagueSquadAggregateOptional.isPresent()){
            leagueSquadAggregate = leagueSquadAggregateOptional.get();
            leagueSquadAggregate.getSquads().add(populateSquad(teamId, squadPlayers));
        }else{
            leagueSquadAggregate = new LeagueSquadAggregate();
            leagueSquadAggregate.setId(String.valueOf(leagueId));
            leagueSquadAggregate.setSquads(new HashSet<>());
            leagueSquadAggregate.getSquads().add(populateSquad(teamId, squadPlayers));
        }
        leagueSquadRepository.save(leagueSquadAggregate);

        return squadPlayers;

    }


    private String getPlayerIdFromUrl(Ref $ref){
        try {
            String playerStr =   $ref.get$ref().split("athletes/")[1].split("\\?internationalClassId")[0];
            return playerStr;
        }catch (Exception e){
            return "0";
        }
    }


    private Squad populateSquad(long teamId, List<SquadPlayer> squadPlayers) {
        Squad squad = new Squad();
        squad.setTeamName(teamNameService.getTeamName(teamId));
        squad.setPlayers(squadPlayers);
        return squad;
    }
}
