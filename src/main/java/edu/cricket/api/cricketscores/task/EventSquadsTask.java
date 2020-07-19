package edu.cricket.api.cricketscores.task;

import com.cricketfoursix.cricketdomain.aggregate.GameAggregate;
import com.cricketfoursix.cricketdomain.aggregate.LeagueSquadAggregate;
import com.cricketfoursix.cricketdomain.common.game.GameClass;
import com.cricketfoursix.cricketdomain.common.game.GameInfo;
import com.cricketfoursix.cricketdomain.common.squad.Squad;
import com.cricketfoursix.cricketdomain.common.squad.SquadPlayer;
import com.cricketfoursix.cricketdomain.repository.LeagueSquadRepository;
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

       public List<SquadPlayer> getLeagueTeamPlayers(long teamId, GameAggregate gameAggregate) {

        GameInfo gameInfo = gameAggregate.getGameInfo();
        GameClass gameClass = gameAggregate.getGameInfo().getGameClass();
        Optional<LeagueSquadAggregate> leagueSquadAggregateOptional = leagueSquadRepository.findById(String.valueOf(gameInfo.getLeagueId()));

        String ref = "http://core.espnuk.org/v2/sports/cricket/leagues/" + (gameInfo.getLeagueId()/13) + "/teams/" + (teamId/13) + "/athletes";

        ref = ref+ "?"+gameClass.getType()+"=" + gameClass.getId();

        EventListing athleteListing = restTemplate.getForObject(ref, EventListing.class);

        logger.info("ref : {}, athleteListing:{}", ref);
        List<SquadPlayer>  squadPlayers =  athleteListing.getItems().stream().map($ref -> new SquadPlayer(playerNameService.getPlayerName(Long.valueOf(getPlayerIdFromUrl($ref))))).collect(Collectors.toList());
        LeagueSquadAggregate leagueSquadAggregate = null;
        if(leagueSquadAggregateOptional.isPresent()){
            leagueSquadAggregate = leagueSquadAggregateOptional.get();
            leagueSquadAggregate.getSquadMap().put(teamId, populateSquad(teamId, squadPlayers));
        }else{
            leagueSquadAggregate = new LeagueSquadAggregate();
            leagueSquadAggregate.setId(String.valueOf(gameInfo.getLeagueId()));
            leagueSquadAggregate.getSquadMap().put(teamId, populateSquad(teamId, squadPlayers));
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
        squad.setTeamName(teamNameService.getTeamName(teamId));

        squad.setPlayers(squadPlayers);
        return squad;
    }
}
