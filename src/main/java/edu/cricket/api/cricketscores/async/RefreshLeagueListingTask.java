package edu.cricket.api.cricketscores.async;

import edu.cricket.api.cricketscores.utils.GameServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("prototype")
public class RefreshLeagueListingTask implements Runnable{

    private static final Logger log = LoggerFactory.getLogger(RefreshLeagueListingTask.class);


    @Autowired
    Map<Long, Long> liveGames;


    @Autowired
    Map<Long, Long> preGames;


    @Autowired
    GameServiceUtil gameServiceUtil;


    @Autowired
    RefreshPreGamesTask refreshPreGamesTask;

    @Autowired
    RefreshPostGamesTask refreshPostGamesTask;

    @Override
    public void run() {
        Set<Long> liveLeagues = new HashSet<>();

        liveLeagues.addAll(liveGames.values());
        liveLeagues.addAll(preGames.values());

        log.info("liveLeagues==>"+liveLeagues);

        liveLeagues.stream().forEach(liveLeague ->gameServiceUtil.updateLeagueEvents(liveLeague, refreshPreGamesTask, refreshPostGamesTask, false));
    }






}
