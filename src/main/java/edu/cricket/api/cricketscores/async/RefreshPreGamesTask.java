package edu.cricket.api.cricketscores.async;

import com.cricketfoursix.cricketdomain.aggregate.GameAggregate;
import com.cricketfoursix.cricketdomain.common.game.*;
import com.cricketfoursix.cricketdomain.repository.GameRepository;
import edu.cricket.api.cricketscores.rest.source.model.*;
import edu.cricket.api.cricketscores.task.EventSquadsTask;
import org.apache.commons.lang.StringUtils;
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
public class RefreshPreGamesTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RefreshPreGamesTask.class);




    RestTemplate restTemplate = new RestTemplate();


    @Autowired
    Map<Long, Boolean> preGames;

    @Autowired
    GameRepository gameRepository;


    @Autowired
    EventSquadsTask eventSquadsTask;



    @Override
    public void run() {

        preGames.keySet().forEach(gameId ->{
            GameAggregate gameAggregate = gameRepository.findById(gameId).orElse(getNewGameAggregate(gameId));
            populatePreGameAggregate(gameAggregate);
            gameRepository.save(gameAggregate);
        });
        log.info("completed refreshPreEvent job at {}", new Date());

    }

    public GameAggregate populatePreGameAggregate(GameAggregate gameAggregate) {

        EventDetail event = null;
        log.info("gameAggregate.getId() ==>"+gameAggregate.getId());
        try {
            event = restTemplate.getForObject("http://new.core.espnuk.org/v2/sports/cricket/events/" + (gameAggregate.getId() / 13), EventDetail.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        log.info("event::::{},event"+ event);

        gameAggregate.setName(event.getName());
        populateGameInfo(event, gameAggregate);
        populateGameClassVenueAndMatchNote(event, gameAggregate);
        populateCompetitors(event, gameAggregate);

        if (null != event && null != event.getCompetitions() && event.getCompetitions().size() > 0) {
            Competition competition = event.getCompetitions().get(0);

            log.info("competition::::{},competition");
            populateTossNote(gameAggregate, competition);
        }
        populateGameStatus(gameAggregate);
        gameRepository.save(gameAggregate);
        return gameAggregate;
    }



    private void populateCompetitors(EventDetail event, GameAggregate gameAggregate) {
        if(null != event.getCompetitions().get(0).getCompetitors()) {
            event.getCompetitions().get(0).getCompetitors().forEach(competitor -> {

                log.info("competitorcompetitorcompetitorcompetitor+>"+competitor);
                if(competitor.getOrder() == 1){
                    com.cricketfoursix.cricketdomain.common.game.Competitor competitor1 = new com.cricketfoursix.cricketdomain.common.game.Competitor();
                    competitor1.setId(Long.valueOf(competitor.getId())*13);
                    gameAggregate.getGameInfo().setCompetitor1(competitor1.getId());
                    competitor1.setLineScoreRef(competitor.getLinescores().get$ref());
                    competitor1.setRosterRef(competitor.getRoster().get$ref());
                    competitor1.setSquad(eventSquadsTask.getLeagueTeamPlayers( competitor1.getId(), gameAggregate));

                    gameAggregate.setCompetitor1(competitor1);
                }else{
                    com.cricketfoursix.cricketdomain.common.game.Competitor competitor2 = new com.cricketfoursix.cricketdomain.common.game.Competitor();
                    competitor2.setId(Long.valueOf(competitor.getId())*13);
                    gameAggregate.getGameInfo().setCompetitor2(competitor2.getId());
                    competitor2.setLineScoreRef(competitor.getLinescores().get$ref());
                    competitor2.setRosterRef(competitor.getRoster().get$ref());
                    competitor2.setSquad(eventSquadsTask.getLeagueTeamPlayers(competitor2.getId(), gameAggregate));

                    gameAggregate.setCompetitor2(competitor2);
                }
            });
        }
    }


    private void populateGameClassVenueAndMatchNote(EventDetail event, GameAggregate gameAggregate) {
        if(null != event.getCompetitions() && event.getCompetitions().size() > 0) {
            Competition competition = event.getCompetitions().get(0);
            gameAggregate.setGameStatusApiRef(competition.getStatus().get$ref());
            populateGameClass(gameAggregate, competition);
            Venue venue =competition.getVenue();
            if(null != venue) {
                gameAggregate.getGameInfo().setVenue(venue.getFullName());
            }
            gameAggregate.getGameInfo().setNote(competition.getNote());
        }
    }

    private void populateGameClass(GameAggregate gameAggregate, Competition competition) {
        EventClass eventClass = competition.getEventClass();
        log.info("eventClass====>>>>"+eventClass);
        if(null != eventClass) {
            GameClass gameClass = new GameClass();
            if(Integer.valueOf(eventClass.getInternationalClassId()) > 0) {
                gameClass.setId(Integer.valueOf(eventClass.getInternationalClassId()));
                gameClass.setType("internationalClassId");
            }else{
                gameClass.setId(Integer.valueOf(eventClass.getGeneralClassId()));
                gameClass.setType("generalClassId");
            }

            gameClass.setName(eventClass.getName());
            gameClass.setShortName(eventClass.getEventType());
            gameAggregate.getGameInfo().setGameClass(gameClass);
        }
    }

    private void populateGameInfo(EventDetail event, GameAggregate gameAggregate) {
        GameInfo gameInfo = new GameInfo();
        gameInfo.setGameId(gameAggregate.getId());
        gameInfo.setName(event.getName());
        gameInfo.setDate(event.getDate());
        gameInfo.setEndDate(event.getEndDate());
        setSeason(event, gameInfo);
        gameAggregate.setGameInfo(gameInfo);
    }


    private void populateGameStatus(GameAggregate gameAggregate) {
        EventStatus eventStatus = restTemplate.getForObject(gameAggregate.getGameStatusApiRef(), EventStatus.class);
        log.info("eventStatus:: {}",eventStatus);


        if (null != eventStatus) {

            populateGameStatusType(gameAggregate, eventStatus);
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


    private void populateTossNote(GameAggregate gameAggregate, Competition competition) {
        if(null != competition.getNotes()){
            Optional<Note> noteOptional = competition.getNotes().stream().filter(note -> note.getType().equalsIgnoreCase("toss")).findFirst();
            if(noteOptional.isPresent()){
                Note note = noteOptional.get();
                log.info("note::::{},note");

                String toss = note.getText();
                log.info("toss::::{},toss");
                if(StringUtils.isNotBlank(toss)){
                    String [] tossArray = toss.split(",");
                    if(tossArray.length > 0) {
                        gameAggregate.getGameInfo().setToss(tossArray[0] + " won the toss.");
                        if(tossArray.length > 1) {
                            if(tossArray[1].toLowerCase().contains("bat")) {
                                gameAggregate.getGameInfo().setToss(gameAggregate.getGameInfo().getToss() + " Opted to Bat.");
                            }else{
                                gameAggregate.getGameInfo().setToss(gameAggregate.getGameInfo().getToss()+  " Opted to Bowl.");
                            }

                        }
                    }

                }

            }
        }
    }



    public GameAggregate getNewGameAggregate(Long gameId){
        GameAggregate gameAggregate = new GameAggregate();
        gameAggregate.setId(gameId);
        gameAggregate.setGameInfo(new GameInfo());
        return gameAggregate;
    }

    private void setSeason(EventDetail eventDetail, GameInfo gameInfo) {
        Season season = restTemplate.getForObject(eventDetail.getSeason().get$ref() , Season.class);
        gameInfo.setLeagueId(season.getId()*13);
        gameInfo.setLeagueName(season.getName());
        gameInfo.setSeason(season.getYear());
    }



}
