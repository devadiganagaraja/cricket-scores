package edu.cricket.api.cricketscores.rest.scheduler;

import edu.cricket.api.cricketscores.task.EventsListingTask;
import edu.cricket.api.cricketscores.task.LeagueListingTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class LeagueScheduler {

    private static final Logger logger = LoggerFactory.getLogger(LeagueScheduler.class);


    @Autowired
    LeagueListingTask leagueListingTask;

    @Scheduled(fixedRate = 72000)
    public void refreshLiveLeagues() {
        leagueListingTask.refreshLiveLeagues();
        logger.info("completed leagueListing refresh job at {}", new Date());
    }
}
