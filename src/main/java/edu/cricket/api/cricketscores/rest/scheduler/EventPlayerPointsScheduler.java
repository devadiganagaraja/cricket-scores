package edu.cricket.api.cricketscores.rest.scheduler;

import edu.cricket.api.cricketscores.task.EventPlayerPointsTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class EventPlayerPointsScheduler {

    @Autowired
    EventPlayerPointsTask eventPlayerPointsTask;

    private static final Logger logger = LoggerFactory.getLogger(EventPlayerPointsScheduler.class);


    @Scheduled(fixedRate = 300000, initialDelay = 600000)
    public void refreshLiveLeagues() {
        logger.info("started event player points refresh job at {}", new Date());

        eventPlayerPointsTask.updateEventPlayerPoints();
    }

}
