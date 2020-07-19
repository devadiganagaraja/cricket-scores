package edu.cricket.api.cricketscores.task;

import com.cricketfoursix.cricketdomain.aggregate.GameAggregate;
import com.cricketfoursix.cricketdomain.common.game.Award;
import com.cricketfoursix.cricketdomain.common.game.GameInfo;
import com.cricketfoursix.cricketdomain.common.game.GameStatus;
import com.cricketfoursix.cricketdomain.common.game.GameSummary;
import com.cricketfoursix.cricketdomain.repository.GameRepository;
import edu.cricket.api.cricketscores.rest.service.PlayerNameService;
import edu.cricket.api.cricketscores.rest.service.TeamNameService;
import edu.cricket.api.cricketscores.rest.source.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class PostGamesTask {

    private static final Logger log = LoggerFactory.getLogger(PostGamesTask.class);

    @Autowired
    Map<Long, Boolean> postEvents;


    @Autowired
    TeamNameService teamNameService;


    @Autowired
    GameRepository gameRepository;

    @Autowired
    LiveGamesTask liveGamesTask;


    @Autowired
    PlayerNameService playerNameService;

    RestTemplate restTemplate = new RestTemplate();


    public void refreshPostEvent() {

        postEvents.keySet().forEach(gameId ->{
            populatePostGameAggregate(gameId);


        });
    }

    public GameAggregate populatePostGameAggregate(Long gameId) {
        GameAggregate gameAggregate = null;

        try {

            Optional<GameAggregate> gameAggregateOptional = gameRepository.findById(gameId);


            if(gameAggregateOptional.isPresent()){
                gameAggregate = gameAggregateOptional.get();
            }else{
                gameAggregate = new GameAggregate();
                gameAggregate.setId(gameId);
                gameAggregate.setGameInfo(new GameInfo());
            }
            EventDetail event = restTemplate.getForObject("http://new.core.espnuk.org/v2/sports/cricket/events/" + (gameAggregate.getId()/13) , EventDetail.class);

            populateMatchNote(gameAggregate, event);
            populateGameStatus(gameAggregate);
            populateWinner(event, gameAggregate);
            gameRepository.save(gameAggregate);

        }catch (Exception e){
            liveGamesTask.populateGameAggregate(gameAggregate);
        }
        return gameAggregate;
    }


    private void populateWinner(EventDetail event, GameAggregate gameAggregate) {
        if(null != event.getCompetitions().get(0).getCompetitors()) {
            event.getCompetitions().get(0).getCompetitors().forEach(competitor -> {
                if(competitor.getOrder() == 1){
                    gameAggregate.getCompetitor1().setWinner(competitor.isWinner());
                }else{
                    gameAggregate.getCompetitor2().setWinner(competitor.isWinner());
                }
            });
        }
    }


    private void populateMatchNote(GameAggregate gameAggregate, EventDetail event){
        gameAggregate.getGameInfo().setNote(event.getCompetitions().get(0).getNote());
    }






    private void populateGameStatus(GameAggregate gameAggregate) {
        EventStatus eventStatus = restTemplate.getForObject(gameAggregate.getGameStatusApiRef(), EventStatus.class);
        log.info("eventStatus:: {}",eventStatus);


        if (null != eventStatus) {
            populateGameStatusType(gameAggregate, eventStatus);
            populateAwards(gameAggregate, eventStatus);
        }
    }


    private void populateAwards(GameAggregate gameAggregate, EventStatus eventStatus) {
        if(gameAggregate.getGameInfo().getGameStatus().equals(GameStatus.post)) {
            if (null != eventStatus.getFeaturedAthletes()) {
                eventStatus.getFeaturedAthletes().forEach(featuredAthletes -> {
                    Award award = new Award();
                    award.setPlayerId(Long.valueOf(featuredAthletes.getPlayerId()) * 13);
                    award.setTeamId(Long.valueOf(featuredAthletes.getTeam().get$ref().split("teams/")[1]) * 13);
                    if (null == gameAggregate.getGameInfo().getGameSummary())
                        gameAggregate.getGameInfo().setGameSummary(new GameSummary());
                    if ("Player Of The Match".equalsIgnoreCase(featuredAthletes.getDisplayName())) {
                        gameAggregate.getGameInfo().getGameSummary().setPlayerOfMatch(award);
                    } else if ("Player Of The Series".equalsIgnoreCase(featuredAthletes.getDisplayName())) {
                        gameAggregate.getGameInfo().getGameSummary().setPlayerOfSeries(award);
                    }
                });
            }
        }
    }
    private void populateGameStatusType(GameAggregate gameAggregate, EventStatus eventStatus) {

        if(null !=eventStatus.getType()){
            EventStatusType eventStatusType = eventStatus.getType();
            log.info("eventStatusType:: {}", eventStatusType);
            GameStatus gameStatus = GameStatus.cancled;
            if("post".equalsIgnoreCase(eventStatusType.getState())){
                gameStatus = GameStatus.post;
            }else if("pre".equalsIgnoreCase(eventStatusType.getState())){
                gameStatus = GameStatus.pre;
            }else if("in".equalsIgnoreCase(eventStatusType.getState())){
                gameStatus = GameStatus.live;
            }else if("scheduled".equalsIgnoreCase(eventStatusType.getState())){
                gameStatus = GameStatus.future;
            }

            gameAggregate.getGameInfo().setGameStatus(gameStatus);
        }
    }




}
