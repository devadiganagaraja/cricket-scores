package edu.cricket.api.cricketscores.async;

import edu.cricket.api.cricketscores.rest.response.MatchCommentary;
import edu.cricket.api.cricketscores.rest.source.model.EventListing;
import edu.cricket.api.cricketscores.rest.source.model.MatchStatus;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class RefreshEventStatusTask implements Runnable{

    private static final Logger log = LoggerFactory.getLogger(RefreshEventStatusTask.class);


    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    Map<Long, Long> preGames;


    @Autowired
    Map<Long, Long> liveGames;


    @Autowired
    Map<Long, Long> postGames;


    @Autowired
    public Map<Long, MatchCommentary> eventsCommsCache;

    @Override
    public void run() {
        EventListing eventListing = restTemplate.getForObject("http://core.espnuk.org/v2/sports/cricket/events?eventsrange=20", EventListing.class);

        Set<Long> allGames = new HashSet<>();
        eventListing.getItems().forEach(ref -> {
            String sourceEventId = ref.get$ref().split("events/")[1];
            Long gameId = Long.valueOf(sourceEventId)*13;
            String matchStatusRef = ref.get$ref()+"/competitions/"+sourceEventId+"/status";
            MatchStatus matchStatus = restTemplate.getForObject(matchStatusRef, MatchStatus.class);
            if(null != matchStatus && null != matchStatus.getType()){
                String sourceLeagueId = ref.get$ref().split("/events")[0].split("leagues/")[1];
                if(StringUtils.isNotEmpty(sourceLeagueId)) {
                    Long leagueId = Long.valueOf(sourceLeagueId)*13;

                    allGames.add(gameId);
                    if ("pre".equalsIgnoreCase(matchStatus.getType().getState())) {
                        preGames.put(gameId, leagueId);
                        liveGames.remove(gameId);
                        postGames.remove(gameId);
                        eventsCommsCache.remove(gameId);

                    } else if ("in".equalsIgnoreCase(matchStatus.getType().getState())) {
                        preGames.remove(gameId);
                        liveGames.put(gameId, leagueId);
                        postGames.remove(gameId);

                    } else if ("post".equalsIgnoreCase(matchStatus.getType().getState())) {
                        preGames.remove(gameId);
                        liveGames.remove(gameId);
                        postGames.put(gameId, leagueId);
                        eventsCommsCache.remove(gameId);
                    }
                }
            }
        });
        filterOldEvent(allGames);
        log.info("refreshEventStatus ::::: preGames : {} ::::: liveGames : {} ::::: postGames : {}", preGames, liveGames, postGames);

        log.info("completed refreshEventStatus job at {}", new Date());

    }


    private void filterOldEvent(Set<Long> allEvents) {
        Set<Long> oldEvents = preGames.keySet().stream().filter(eId -> ! allEvents.contains(eId)).collect(Collectors.toCollection(HashSet::new));
        if(null != oldEvents && oldEvents.size() > 0) {
            oldEvents.forEach(oldEvent -> preGames.remove(oldEvent));
        }

        oldEvents = liveGames.keySet().stream().filter(eId -> ! allEvents.contains(eId)).collect(Collectors.toCollection(HashSet::new));
        if(null != oldEvents && oldEvents.size() > 0)
            oldEvents.forEach(oldEvent -> liveGames.remove(oldEvent));

        oldEvents = postGames.keySet().stream().filter(eId -> ! allEvents.contains(eId)).collect(Collectors.toCollection(HashSet::new));
        if(null != oldEvents && oldEvents.size() > 0)
            oldEvents.forEach(oldEvent -> postGames.remove(oldEvent));
    }
}
