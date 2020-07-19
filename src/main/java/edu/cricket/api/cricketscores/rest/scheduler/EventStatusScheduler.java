package edu.cricket.api.cricketscores.rest.scheduler;

import edu.cricket.api.cricketscores.task.EventStatusTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class EventStatusScheduler {
    private static final Logger logger = LoggerFactory.getLogger(EventStatusScheduler.class);

    @Autowired
    EventStatusTask eventStatusTask;

    @Scheduled(fixedRate = 900000)
    public void refreshEventStatus() {
        logger.info("starting refreshLiveEvent job at {}", new Date());
        eventStatusTask.refreshEventStatus();
        logger.info("completed refreshLiveEvent job at {}", new Date());
    }
}
