package edu.cricket.api.cricketscores.task;

import edu.cricket.api.cricketscores.async.RefreshLeagueIndexTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class LeagueIndexingTask {

    private static final Logger log = LoggerFactory.getLogger(LeagueListingTask.class);


    @Autowired
    TaskExecutor taskExecutor;

    @Autowired
    RefreshLeagueIndexTask refreshLeagueIndexTask;

    public void refreshLeagueIndexAsync() {
        taskExecutor.execute(refreshLeagueIndexTask);
    }


    public void refreshLeagueIndex() {
        refreshLeagueIndexAsync();

    }

}
