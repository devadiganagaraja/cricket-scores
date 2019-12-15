package edu.cricket.api.cricketscores.rest.scheduler;

import edu.cricket.api.cricketscores.task.EventBallsTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EventBallsPersistScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(EventBallsPersistScheduledTask.class);

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");



    @Autowired
    EventBallsTask eventBallsTask;

    @Scheduled(fixedRate = 30000)
    public void scheduleTaskWithFixedRate() {
        eventBallsTask.refreshLiveEventBalls();
    }



}
