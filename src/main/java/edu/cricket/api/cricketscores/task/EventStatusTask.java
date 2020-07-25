package edu.cricket.api.cricketscores.task;

import edu.cricket.api.cricketscores.async.RefreshEventStatusTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class EventStatusTask {

    private static final Logger log = LoggerFactory.getLogger(EventStatusTask.class);



    @Autowired
    TaskExecutor taskExecutor;


    @Autowired
    RefreshEventStatusTask refreshEventStatusTask;

    public void refreshEventStatusAsync() {
        taskExecutor.execute(refreshEventStatusTask);
    }

}
