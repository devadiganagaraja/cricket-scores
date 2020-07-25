package edu.cricket.api.cricketscores.async;

import edu.cricket.api.cricketscores.rest.source.model.BallDetail;
import edu.cricket.api.cricketscores.rest.source.model.EventListing;
import edu.cricket.api.cricketscores.utils.BbbServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Scope("prototype")
public class RefreshLiveEventBallsTask implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(RefreshLiveEventBallsTask.class);

    @Autowired
    Map<Long, Boolean> liveGames;


    @Autowired
    BbbServiceUtil bbbServiceUtil;

    @Override
    public void run() {
        bbbServiceUtil.persistLiveBallsPolling(liveGames);
    }


}
