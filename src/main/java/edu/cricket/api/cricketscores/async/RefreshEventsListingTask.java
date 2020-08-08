package edu.cricket.api.cricketscores.async;


import com.cricketfoursix.cricketdomain.aggregate.GameAggregate;
import edu.cricket.api.cricketscores.rest.response.MatchCommentary;
import edu.cricket.api.cricketscores.rest.source.model.EventListing;
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
    RefreshPreGamesTask refreshPreGamesTask;

    @Autowired
    RefreshPostGamesTask refreshPostGamesTask;


    @Autowired
    GameServiceUtil gameServiceUtil;

    @Autowired
    public Map<Long, MatchCommentary> eventsCommsCache;

    @Override
    public void run() {

        Map<Long, GameAggregate> eventMap = new ConcurrentHashMap<>();
        getEvents().forEach(sourceEventId ->  {
            GameAggregate gameAggregate = null;
            try {
                log.info("sourceEventId :{}", sourceEventId);
                gameAggregate = gameServiceUtil.getGameAggregate(sourceEventId, refreshPreGamesTask, refreshPostGamesTask);
                log.info("gameAggregate :{}", gameAggregate);
                gameServiceUtil.updateLeagueForEvent(gameAggregate);
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










}
