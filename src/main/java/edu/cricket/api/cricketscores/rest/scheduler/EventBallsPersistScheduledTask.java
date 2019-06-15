package edu.cricket.api.cricketscores.rest.scheduler;

import edu.cricket.api.cricketscores.rest.response.model.Event;
import edu.cricket.api.cricketscores.rest.source.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventBallsPersistScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(EventBallsPersistScheduledTask.class);

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");


    RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRate = 30000)
    public void scheduleTaskWithFixedRate() {
        String today= new SimpleDateFormat("yyyyMMdd").format(new Date());
        getEvents(today).forEach(eventref -> {
            getEventData(eventref);
        });

        logger.info("Fixed Rate Task :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()) );
    }


    private List<String> getEvents(String date){
        EventListing eventListing = restTemplate.getForObject("http://core.espnuk.org/v2/sports/cricket/events?date="+date, EventListing.class);
        return eventListing.getItems().stream().map(ref -> ref.get$ref()).collect(Collectors.toList());
    }

    private Event getEventData(String $ref) {

        EventDetail eventDetail = restTemplate.getForObject($ref, EventDetail.class);


        Competition competition = eventDetail.getCompetitions().get(0);
        if(null != competition.getDetails().get$ref()){
            EventListing  bbbListing = restTemplate.getForObject(competition.getDetails().get$ref(), EventListing.class);
            bbbListing.getItems().forEach(ref -> processBallData(ref.get$ref()));
        }
        return null;
    }

    private Event processBallData(String $ref) {
        BallDetail ballDetail = restTemplate.getForObject($ref, BallDetail.class);
        return null;

    }

}
