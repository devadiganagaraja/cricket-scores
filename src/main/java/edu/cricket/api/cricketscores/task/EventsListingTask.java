package edu.cricket.api.cricketscores.task;

import edu.cricket.api.cricketscores.async.RefreshEventsListingTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;


@Service
public class EventsListingTask {


    private static final Logger log = LoggerFactory.getLogger(EventsListingTask.class);



    @Autowired
    TaskExecutor taskExecutor;




    @Autowired
    RefreshEventsListingTask refreshEventsListingTask;

    public void setEventsAsync() {
        taskExecutor.execute(refreshEventsListingTask);
    }


    public void setEvents() {
        setEventsAsync();
    }

}
