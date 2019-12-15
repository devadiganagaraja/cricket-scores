package edu.cricket.api.cricketscores.rest.scheduler;

import edu.cricket.api.cricketscores.task.PostEventTask;
import edu.cricket.api.cricketscores.task.PreEventTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PostEventScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PostEventScheduler.class);

    @Autowired
    PostEventTask postEventTask;

    @Scheduled(fixedRate = 1800000)
    public void refreshPostEvent() {
        logger.info("starting PostEventScheduler job at {}", new Date());
        postEventTask.refreshPostEvent();
        logger.info("completed PostEventScheduler job at {}", new Date());
    }
}
