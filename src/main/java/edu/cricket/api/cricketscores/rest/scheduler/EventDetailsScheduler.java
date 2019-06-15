package edu.cricket.api.cricketscores.rest.scheduler;


import edu.cricket.api.cricketscores.rest.response.model.Event;
import edu.cricket.api.cricketscores.rest.response.model.EventInfo;
import edu.cricket.api.cricketscores.task.EventScoreCardTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class EventDetailsScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EventDetailsScheduler.class);

    @Autowired
    EventScoreCardTask eventScoreCardTask;


    @Scheduled(fixedRate = 30000)
    public void refreshLiveEventScreCard() {
        eventScoreCardTask.refreshLiveEventScoreCards();
        logger.info("completed event refresh job at {}", new Date());


    }

}
