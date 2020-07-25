package edu.cricket.api.cricketscores.rest.scheduler;

import edu.cricket.api.cricketscores.task.PreGamesTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PreEventScheduler {
    private static final Logger logger = LoggerFactory.getLogger(LiveEventScheduler.class);

    @Autowired
    PreGamesTask preGamesTask;

    @Scheduled(fixedRate = 1200000, initialDelay = 60000)
    public void refreshPreEvent() {
        logger.info("starting refreshPreEvent job at {}", new Date());
        preGamesTask.refreshPreEvents();
    }

}
