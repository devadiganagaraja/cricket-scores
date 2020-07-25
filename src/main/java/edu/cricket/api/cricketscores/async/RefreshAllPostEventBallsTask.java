package edu.cricket.api.cricketscores.async;


import edu.cricket.api.cricketscores.utils.BbbServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
@Scope("prototype")
public class RefreshAllPostEventBallsTask implements Runnable {


    private static final Logger logger = LoggerFactory.getLogger(RefreshAllPostEventBallsTask.class);

    @Autowired
    Map<Long, Boolean> postGames;




    @Autowired
    BbbServiceUtil bbbServiceUtil;


    @Override
    public void run() {
        postGames.keySet().forEach(gameId -> {
            bbbServiceUtil.persistAllBallsForGame(gameId);
        });
        logger.info("completed post event balls refresh job at {}", new Date());


    }


}
