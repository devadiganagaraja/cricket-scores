package edu.cricket.api.cricketscores.rest.scheduler;

import edu.cricket.api.cricketscores.task.LeagueIndexingTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class LeagueIndexScheduler {
    private static final Logger logger = LoggerFactory.getLogger(LeagueIndexScheduler.class);


    @Autowired
    LeagueIndexingTask leagueIndexingTask;


    @Scheduled(fixedRate = 86400000, initialDelay = 300000)
    public void refreshEventsAndLeagues() {
        logger.info("starting refreshLeagueIndex job at {}", new Date());
        leagueIndexingTask.refreshLeagueIndex();
        logger.info("completed refreshLeagueIndex job at {}", new Date());
    }

}
