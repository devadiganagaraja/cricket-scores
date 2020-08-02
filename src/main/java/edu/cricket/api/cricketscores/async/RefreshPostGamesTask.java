package edu.cricket.api.cricketscores.async;

import com.cricketfoursix.cricketdomain.aggregate.GameAggregate;
import com.cricketfoursix.cricketdomain.common.game.Award;
import com.cricketfoursix.cricketdomain.common.game.GameInfo;
import com.cricketfoursix.cricketdomain.common.game.GameStatus;
import com.cricketfoursix.cricketdomain.common.game.GameSummary;
import com.cricketfoursix.cricketdomain.repository.GameRepository;
import edu.cricket.api.cricketscores.rest.source.model.EventDetail;
import edu.cricket.api.cricketscores.rest.source.model.EventStatus;
import edu.cricket.api.cricketscores.rest.source.model.EventStatusType;
import edu.cricket.api.cricketscores.utils.GameServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;
import java.util.Optional;


@Component
@Scope("prototype")
public class RefreshPostGamesTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RefreshPostGamesTask.class);

    @Autowired
    Map<Long, Boolean> postGames;


    @Autowired
    GameRepository gameRepository;


    @Autowired
    GameServiceUtil gameServiceUtil;


    RestTemplate restTemplate = new RestTemplate();



    @Override
    public void run() {
        postGames.keySet().forEach(gameId -> populatePostGameAggregate(gameId));
        log.info("completed refreshPostEvent job at {}", new Date());
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
            gameServiceUtil.populateGameAggregate(gameAggregate);
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


    public void refreshPostGame(long gameId) {
        populatePostGameAggregate(gameId);
    }
}
