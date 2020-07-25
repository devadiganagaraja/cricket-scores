package edu.cricket.api.cricketscores.task;

import com.cricketfoursix.cricketdomain.aggregate.GameAggregate;
import com.cricketfoursix.cricketdomain.repository.GamePlayerPointsRepository;
import edu.cricket.api.cricketscores.async.RefreshEventPlayerPointsTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EventPlayerPointsTask {

    private static final Logger logger = LoggerFactory.getLogger(EventPlayerPointsTask.class);

    @Autowired
    Map<Long, GameAggregate> liveGamesCache;


    @Autowired
    GamePlayerPointsRepository gamePlayerPointsRepository;



    @Autowired
    TaskExecutor taskExecutor;



    @Autowired
    RefreshEventPlayerPointsTask refreshEventPlayerPointsTask;

    public void updateEventPlayerPointsAsync() {
        taskExecutor.execute(refreshEventPlayerPointsTask);
    }

    public void updateEventPlayerPoints() {
        updateEventPlayerPointsAsync();

    }


}
