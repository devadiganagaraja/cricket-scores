package edu.cricket.api.cricketscores.task;

import edu.cricket.api.cricketscores.rest.source.model.EventListing;
import edu.cricket.api.cricketscores.rest.source.model.MatchStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventStatusTask {

    private static final Logger log = LoggerFactory.getLogger(EventStatusTask.class);


    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    Map<String, Boolean> preEvents;


    @Autowired
    Map<String, Boolean> liveEvents;


    @Autowired
    Map<String, Boolean> postEvents;

    public void refreshEventStatus() {
        EventListing eventListing = restTemplate.getForObject("http://core.espnuk.org/v2/sports/cricket/events", EventListing.class);

        Set<String> allEvents = new HashSet<>();
        eventListing.getItems().forEach(ref -> {
            String sourceEventId = ref.get$ref().split("events/")[1];
            String eventId = String.valueOf(Long.valueOf(sourceEventId)*13);
            String matchStatusRef = "http://core.espnuk.org/v2/sports/cricket/events/"+sourceEventId+"/competitions/"+sourceEventId+"/status";
            MatchStatus matchStatus = restTemplate.getForObject(matchStatusRef, MatchStatus.class);
            if(null != matchStatus && null != matchStatus.getType()){

                allEvents.add(eventId);
                if("pre".equalsIgnoreCase(matchStatus.getType().getState())){
                    ut(eventId, true);
                    liveEvents.remove(eventId);
                    postEvents.remove(eventId);

                }else if("in".equalsIgnoreCase(matchStatus.getType().getState())){
                    preEvents.remove(eventId);
                    liveEvents.put(eventId, true);
                    postEvents.remove(eventId);

                }else if("post".equalsIgnoreCase(matchStatus.getType().getState())){
                    preEvents.remove(eventId);
                    liveEvents.remove(eventId);
                    postEvents.put(eventId, true);

                }
            }
        });
        filterOldEvent(allEvents);
        log.info("refreshEventStatus ::::: preEvents : {} ::::: liveEvents : {} ::::: postEvents : {}", preEvents, liveEvents, postEvents);

    }

    private void filterOldEvent(Set<String> allEvents) {
        Set<String> oldEvents = preEvents.keySet().stream().filter(eId -> ! allEvents.contains(eId)).collect(Collectors.toCollection(HashSet::new));
        if(null != oldEvents && oldEvents.size() > 0) {
            oldEvents.forEach(oldEvent -> preEvents.remove(oldEvent));
        }

        oldEvents = liveEvents.keySet().stream().filter(eId -> ! allEvents.contains(eId)).collect(Collectors.toCollection(HashSet::new));
        if(null != oldEvents && oldEvents.size() > 0)
            oldEvents.forEach(oldEvent -> liveEvents.remove(oldEvent));

        oldEvents = postEvents.keySet().stream().filter(eId -> ! allEvents.contains(eId)).collect(Collectors.toCollection(HashSet::new));
        if(null != oldEvents && oldEvents.size() > 0)
            oldEvents.forEach(oldEvent -> postEvents.remove(oldEvent));
    }
}
