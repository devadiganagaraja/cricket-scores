package edu.cricket.api.cricketscores.task;

import edu.cricket.api.cricketscores.async.RefreshPostGamesTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class PostGamesTask {

    private static final Logger log = LoggerFactory.getLogger(PostGamesTask.class);


    @Autowired
    TaskExecutor taskExecutor;





    @Autowired
    RefreshPostGamesTask refreshLiveGamesTask;

    public void refreshPostEventsAsync() {
        taskExecutor.execute(refreshLiveGamesTask);
    }



    public void refreshPostEvents() {
        refreshPostEventsAsync();
    }







}
