package edu.cricket.api.cricketscores.rest.scheduler;


import edu.cricket.api.cricketscores.task.LiveEventTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class LiveEventScheduler {

    private static final Logger logger = LoggerFactory.getLogger(LiveEventScheduler.class);

    @Autowired
    LiveEventTask liveEventTask;

    @Scheduled(fixedRate = 30000)
    public void refreshLiveEvent() {
        logger.info("starting refreshLiveEvent job at {}", new Date());
        liveEventTask.refreshLiveEvents();
        logger.info("completed refreshLiveEvent job at {}", new Date());
    }
}