package edu.cricket.api.cricketscores.async;


import com.cricketfoursix.cricketdomain.aggregate.GameAggregate;
import com.cricketfoursix.cricketdomain.common.game.GameInfo;
import com.cricketfoursix.cricketdomain.common.game.GameStatus;
import edu.cricket.api.cricketscores.rest.response.MatchCommentary;
import edu.cricket.api.cricketscores.rest.source.model.EventListing;
import edu.cricket.api.cricketscores.rest.source.model.EventStatus;
import edu.cricket.api.cricketscores.rest.source.model.EventStatusType;
import edu.cricket.api.cricketscores.utils.GameServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class RefreshEventsListingTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(RefreshEventsListingTask.class);

    @Autowired
    Map<Long, GameAggregate> liveEvents;

    RestTemplate restTemplate = new RestTemplate();


    @Autowired
    RefreshLeagueListingTask refreshLeagueListingTask;


    @Autowired
    GameServiceUtil gameServiceUtil;

    @Autowired
    RefreshPreGamesTask refreshPreGamesTask;



    @Autowired
    public Map<Long, MatchCommentary> eventsCommsCache;


    @Autowired
    RefreshPostGamesTask refreshPostGamesTask;




    @Override
    public void run() {

        Map<Long, GameAggregate> eventMap = new ConcurrentHashMap<>();
        getEvents().forEach(sourceEventId ->  {
            GameAggregate gameAggregate = null;
            try {
                log.info("sourceEventId :{}", sourceEventId);
                gameAggregate = getGameAggregate(sourceEventId);
                refreshLeagueListingTask.updateLeagueForEvent(gameAggregate);
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

        EventListing eventListing = restTemplate.getForObject("http://new.core.espnuk.org/v2/sports/cricket/events", EventListing.class);

        List<String> sourceEventIdList =  eventListing.getItems().stream().map(ref -> ref.get$ref().split("events/")[1]).collect(Collectors.toList());
        return sourceEventIdList;
    }




    public GameAggregate getGameAggregate(String sourceEventId) {
        try {

            GameStatus gameStatus = getGameStatus(sourceEventId);

            Long gameId = Long.valueOf(sourceEventId)*13;
            GameAggregate gameAggregate = new GameAggregate();
            gameAggregate.setGameInfo(new GameInfo());
            gameAggregate.setId(gameId);

            if(GameStatus.pre.equals(gameStatus)){
                return  refreshPreGamesTask.populatePreGameAggregate(gameAggregate);

            }else if(GameStatus.live.equals(gameStatus)){
                return gameServiceUtil.populateGameAggregate(gameAggregate);

            }else if(GameStatus.post.equals(gameStatus)){
                return refreshPostGamesTask.populatePostGameAggregate(gameId);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    private GameStatus getGameStatus(String sourceEventId) {
        EventStatus eventStatus = restTemplate.getForObject("http://new.core.espnuk.org/v2/sports/cricket/events/"+sourceEventId+"/competitions/"+ sourceEventId+"/status", EventStatus.class);

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


}
