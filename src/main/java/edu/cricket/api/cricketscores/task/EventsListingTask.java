package edu.cricket.api.cricketscores.task;

import com.cricketfoursix.cricketdomain.aggregate.GameAggregate;
import com.cricketfoursix.cricketdomain.common.game.GameInfo;
import com.cricketfoursix.cricketdomain.common.game.GameStatus;
import com.cricketfoursix.cricketdomain.repository.GameRepository;
import edu.cricket.api.cricketscores.rest.response.MatchCommentary;
import edu.cricket.api.cricketscores.rest.response.model.*;
import edu.cricket.api.cricketscores.rest.service.PlayerNameService;
import edu.cricket.api.cricketscores.rest.service.TeamNameService;
import edu.cricket.api.cricketscores.rest.source.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class EventsListingTask {

    @Autowired
    LeagueListingTask leagueListingTask;

    @Autowired
    PlayerNameService playerNameService;

    @Autowired
    PreGamesTask preGamesTask;

    @Autowired
    LiveGamesTask liveGamesTask;

    @Autowired
    PostGamesTask postGamesTask;

    @Autowired
    TeamNameService teamNameService;


    GameRepository gameRepository;

    private static final Logger log = LoggerFactory.getLogger(EventsListingTask.class);

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    Map<Long, GameAggregate> liveEvents;



    @Autowired
    EventSquadsTask eventSquadsTask;


    @Autowired
    public Map<String, MatchCommentary> eventsCommsCache;


    public void setEvents(){
        Map<Long, GameAggregate> eventMap = new ConcurrentHashMap<>();
        getEvents().forEach(sourceEventId ->  {
            GameAggregate gameAggregate = null;
            try {
                log.info("sourceEventId :{}", sourceEventId);
                gameAggregate = getGameAggregate(sourceEventId);
                leagueListingTask.updateLeagueForEvent(gameAggregate);
                if (null != gameAggregate) {
                    eventMap.put(gameAggregate.getId(), gameAggregate);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            log.info("event :{}", gameAggregate);

        });
        log.info("eventMap :{}", eventMap.size());


        Set<Long> nonLiveEvents = new HashSet<>();
        liveEvents.keySet().forEach(event -> {

            if( !eventMap.containsKey(event)){
                nonLiveEvents.add(event);
            }
        });
        nonLiveEvents.forEach(eventId -> {
            liveEvents.remove(eventId);
            eventsCommsCache.remove(eventId);
        });

        log.info("finally eventMap :{}", eventMap.size());
        //liveEvents.putAll(eventMap);
        log.info("finally liveEvents.keySet() :{}", liveEvents.keySet());

    }

    private List<String> getEvents(){
        EventListing eventListing = restTemplate.getForObject("http://core.espnuk.org/v2/sports/cricket/events", EventListing.class);

        return eventListing.getItems().stream().map(ref -> ref.get$ref().split("events/")[1]).collect(Collectors.toList());
    }



    public GameAggregate getGameAggregate(String sourceEventId) {
        try {

            GameStatus gameStatus = getGameStatus(sourceEventId);

            Long gameId = Long.valueOf(sourceEventId)*13;
            GameAggregate gameAggregate = new GameAggregate();
            gameAggregate.setGameInfo(new GameInfo());
            gameAggregate.setId(gameId);

            if(GameStatus.pre.equals(gameStatus)){
                return  preGamesTask.populatePreGameAggregate(gameAggregate);

            }else if(GameStatus.live.equals(gameStatus)){
                return liveGamesTask.populateGameAggregate(gameAggregate);

            }else if(GameStatus.post.equals(gameStatus)){
                return postGamesTask.populatePostGameAggregate(gameId);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    private GameStatus getGameStatus(String sourceEventId) {
        EventStatus eventStatus = restTemplate.getForObject("http://core.espnuk.org/v2/sports/cricket/events/"+sourceEventId+"/competitions/"+ sourceEventId+"/status", EventStatus.class);

        GameStatus gameStatus = GameStatus.cancled;
        if(null !=eventStatus.getType()) {
            EventStatusType eventStatusType = eventStatus.getType();
            log.info("eventStatusType:: {}", eventStatusType);

            if ("post".equalsIgnoreCase(eventStatusType.getState())) {
                gameStatus = GameStatus.post;
            } else if ("pre".equalsIgnoreCase(eventStatusType.getState())) {
                gameStatus = GameStatus.pre;
            } else if ("in".equalsIgnoreCase(eventStatusType.getState())) {
                gameStatus = GameStatus.live;
            } else if ("scheduled".equalsIgnoreCase(eventStatusType.getState())) {
                gameStatus = GameStatus.future;
            }
        }
        return gameStatus;
    }

    /*private void setSeason(EventDetail eventDetail, Game event) {
        Season season = restTemplate.getForObject(eventDetail.getSeason().get$ref() , Season.class);
        event.setLeagueId(season.getId()*13);
        event.setLeagueName(season.getName());
        event.setLeagueYear(String.valueOf(season.getYear()));
    }

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

   /* public Set<Event> getEventsInfo(String deltaDays){
        int delta = 0;
        try {
            delta = Integer.parseInt(deltaDays);
        }catch (Exception e){
            log.error("deltaDays is not parsable to int : {}", e.getMessage());
        }

        Set<Event> eventInfos =  new TreeSet<>();

        Date date = org.apache.commons.lang.time.DateUtils.addDays(new Date(), delta);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        date = calendar.getTime();

            Iterable<EventAggregate> eventItr = eventRepository.findAll((qEventAggregate.eventInfo.startDate.lt(date).and(qEventAggregate.eventInfo.endDate.goe(date)))
                                                                        .or(qEventAggregate.eventInfo.startDate.eq(date)));
        if(null != eventItr){
            eventItr.forEach(eventAggregate -> {
                eventInfos.add(eventAggregate.getEventInfo());
            });
        }
        return eventInfos;

    }*/
/*

    public List<League> getLiveEvents(){

        log.info("liveEvents.valuesse -> {}", liveEvents.values());

        List<EventAggregate> eventInfos =  new ArrayList<>(liveEvents.values());
        Set<League> leagues = new HashSet<>();
        if(null != eventInfos) {
            eventInfos.forEach(event -> {

                log.info("event -> {}", event);
                League eventLeague = new League();
                eventLeague.setLeagueId(event.getEventInfo().getLeagueId());

                if(leagues.contains(eventLeague)){
                    leagues.forEach(league -> {
                        if (league.getLeagueId() == event.getEventInfo().getLeagueId()) {
                            league.getEventSet().add(event.getEventInfo());
                        }
                    });
                }else {
                    League newLeague = new League();
                    newLeague.setLeagueId(event.getEventInfo().getLeagueId());
                    newLeague.setLeagueName(event.getEventInfo().getLeagueName());
                    newLeague.setLeagueYear(event.getEventInfo().getLeagueYear());
                    newLeague.setClassId(event.getEventInfo().getInternationalClassId() > 0 ? event.getEventInfo().getInternationalClassId() : event.getEventInfo().getGeneralClassId());
                    newLeague.setEventSet(new HashSet<>());
                    newLeague.getEventSet().add(event.getEventInfo());
                    leagues.add(newLeague);
                }
            });
        }
        List<League> leaguesList =  new ArrayList<>(leagues);
        leaguesList.sort(Comparator.comparing(League::getClassId));
        return leaguesList;
    }
*/

}
