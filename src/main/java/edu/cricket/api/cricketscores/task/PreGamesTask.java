package edu.cricket.api.cricketscores.task;

import com.cricketfoursix.cricketdomain.aggregate.GameAggregate;
import com.cricketfoursix.cricketdomain.common.game.GameClass;
import com.cricketfoursix.cricketdomain.common.game.GameInfo;
import com.cricketfoursix.cricketdomain.common.game.GameStatus;
import com.cricketfoursix.cricketdomain.repository.GameRepository;
import edu.cricket.api.cricketscores.rest.service.PlayerNameService;
import edu.cricket.api.cricketscores.rest.service.TeamNameService;
import edu.cricket.api.cricketscores.rest.source.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class PreGamesTask {

    private static final Logger log = LoggerFactory.getLogger(PreGamesTask.class);

    @Autowired
    Map<Long, Boolean> preEvents;

    @Autowired
    GameRepository gameRepository;


    RestTemplate restTemplate = new RestTemplate();


    @Autowired
    TeamNameService teamNameService;


    @Autowired
    PlayerNameService playerNameService;


    @Autowired
    EventSquadsTask eventSquadsTask;


    public void refreshPreEvents() {

        preEvents.keySet().forEach(gameId ->{
            GameAggregate gameAggregate = gameRepository.findById(gameId).orElse(getNewGameAggregate(gameId));
            populatePreGameAggregate(gameAggregate);
            gameRepository.save(gameAggregate);
        });
    }

    public GameAggregate populatePreGameAggregate(GameAggregate gameAggregate) {
        EventDetail event = restTemplate.getForObject("http://new.core.espnuk.org/v2/sports/cricket/events/" + (gameAggregate.getId()/13) , EventDetail.class);

        log.info("event::::{},event");

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
                if(competitor.getOrder() == 1){
                    com.cricketfoursix.cricketdomain.common.game.Competitor competitor1 = new com.cricketfoursix.cricketdomain.common.game.Competitor();
                    competitor1.setId(Long.valueOf(competitor.getId())*13);
                    competitor1.setLineScoreRef(competitor.getLinescores().get$ref());
                    competitor1.setRosterRef(competitor.getRoster().get$ref());
                    competitor1.setSquad(eventSquadsTask.getLeagueTeamPlayers( Long.valueOf(competitor.getId()), gameAggregate));

                    gameAggregate.setCompetitor1(competitor1);
                }else{
                    com.cricketfoursix.cricketdomain.common.game.Competitor competitor2 = new com.cricketfoursix.cricketdomain.common.game.Competitor();
                    competitor2.setId(Long.valueOf(competitor.getId())*13);
                    competitor2.setLineScoreRef(competitor.getLinescores().get$ref());
                    competitor2.setRosterRef(competitor.getRoster().get$ref());
                    competitor2.setSquad(eventSquadsTask.getLeagueTeamPlayers(Long.valueOf(competitor.getId()), gameAggregate));

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

/*
    private void updatePreGameInfo(GameAggregate gameAggregate) {
        try {
            Long gameId = gameAggregate.getId();
            long sourceEventId = gameId/13;
            String eventRef = "http://core.espnuk.org/v2/sports/cricket/events/"+sourceEventId;

            EventDetail eventDetail = restTemplate.getForObject(eventRef, EventDetail.class);

            Competition competition = eventDetail.getCompetitions().get(0);
            Event event = new Event();
            int intClassId = competition.getCompetitionClass().getInternationalClassId();
            if (intClassId > 10) {
                preEvents.remove(gameId);
                return;
            }
            event.setInternationalClassId(intClassId);
            int genClassId = competition.getCompetitionClass().getGeneralClassId();
            if (intClassId == 0 && genClassId > 10){
                preEvents.remove(gameId);
                return;
            }
            event.setGeneralClassId(genClassId);
            event.setEventId(String.valueOf(Integer.parseInt(competition.getId()) * 13));
            event.setStartDate(DateUtils.getDateFromString(eventDetail.getDate()));
            event.setEndDate(DateUtils.getDateFromString(eventDetail.getEndDate()));
            event.setVenue(getEventVenue(eventDetail.getVenues().get(0).get$ref()));

            setSeason(eventDetail, event);
            event.setType(competition.getCompetitionClass().getEventType());
            event.setNote(competition.getNote());

            MatchStatus matchStatus = restTemplate.getForObject(competition.getStatus().get$ref(), MatchStatus.class);
            if (null != matchStatus) {
                event.setPeriod(matchStatus.getPeriod());
                if (null != matchStatus.getType()) {
                    event.setDescription(matchStatus.getType().getDescription());
                    event.setDescription(matchStatus.getType().getDescription());
                    event.setDetail(matchStatus.getType().getDetail());
                    event.setState(matchStatus.getType().getState());
                }
            }

            List<Competitor> competitorList = competition.getCompetitors();
            if (null != competitorList && competitorList.size() == 2) {
                edu.cricket.api.cricketscores.rest.response.model.Competitor team1 = new edu.cricket.api.cricketscores.rest.response.model.Competitor();
                team1.setTeamName(getEventTeam(competitorList.get(0).getTeam().get$ref()));
                team1.setScore(getEventScore(competitorList.get(0).getScore().get$ref()));
                team1.setWinner(competitorList.get(0).isWinner());
                long sourceTeam1Id = Long.valueOf(team1.getTeamName().split(":")[1]) / 13;
                team1.setSquad(eventSquadsTask.getLeagueTeamPlayers(event.getLeagueId(), sourceTeam1Id, event));
                event.setTeam1(team1);

                edu.cricket.api.cricketscores.rest.response.model.Competitor team2 = new edu.cricket.api.cricketscores.rest.response.model.Competitor();
                team2.setTeamName(getEventTeam(competitorList.get(1).getTeam().get$ref()));
                team2.setScore(getEventScore(competitorList.get(1).getScore().get$ref()));
                long sourceTeam2Id = Long.valueOf(team2.getTeamName().split(":")[1]) / 13;
                team2.setSquad(eventSquadsTask.getLeagueTeamPlayers(event.getLeagueId(), sourceTeam2Id, event));
                event.setTeam2(team2);
            }

            gameAggregate.setEventInfo(event);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }*/

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
/*
    public String getEventVenue(String $ref){
        Venue venue = restTemplate.getForObject($ref , Venue.class);
        return venue.getFullName();
    }

    public String getEventTeam(String $ref){
        Team team = restTemplate.getForObject($ref , Team.class);
        return teamNameService.getTeamName(team.getId());
    }

    public String getEventScore(String $ref){
        Score score = restTemplate.getForObject($ref , Score.class);
        return  score.getValue();
    }*/
}
