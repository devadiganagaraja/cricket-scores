package edu.cricket.api.cricketscores.rest.scheduler;

import edu.cricket.api.cricketscores.task.EventsListingTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class EventListingScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EventListingScheduler.class);

    @Autowired
    EventsListingTask eventsListingTask;


    @Scheduled(fixedRate = 600000)
    public void refreshEvents() {
        logger.info("started eventListing refresh job at {}", new Date());
        eventsListingTask.setEvents();
        logger.info("completed eventListing refresh job at {}", new Date());
    }
}
