package edu.cricket.api.cricketscores.controller;


import edu.cricket.api.cricketscores.async.RefreshPostGamesTask;
import edu.cricket.api.cricketscores.async.RefreshPreGamesTask;
import edu.cricket.api.cricketscores.task.LeagueIndexingTask;
import edu.cricket.api.cricketscores.task.LiveGamesTask;
import edu.cricket.api.cricketscores.task.PostGamesTask;
import edu.cricket.api.cricketscores.task.PreGamesTask;
import edu.cricket.api.cricketscores.utils.BbbServiceUtil;
import edu.cricket.api.cricketscores.utils.GameServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin
public class GameStatusController {

    @Autowired
    Map<Long, Long> preGames;

    @Autowired
    Map<Long, Long> liveGames;


    @Autowired
    Map<Long, Long> postGames;


    @Autowired
    PreGamesTask preGamesTask;

    @Autowired
    LiveGamesTask liveGamesTask;

    @Autowired
    PostGamesTask postGamesTask;

    @Autowired
    BbbServiceUtil bbbServiceUtil;

    @Autowired
    GameServiceUtil gameServiceUtil;

    @Autowired
    RefreshPreGamesTask refreshPreGamesTask;

    @Autowired
    RefreshPostGamesTask refreshPostGamesTask;

    @Autowired
    LeagueIndexingTask leagueIndexingTask;



    @GetMapping("/refreshLeagueSeason")
    public Boolean refreshLeagueSeason(@RequestParam("leagueId") long leagueId, @RequestParam("seasonId") long seasonId) {

        try {

            gameServiceUtil.updateLeagueEvents(leagueId, refreshPreGamesTask, refreshPostGamesTask, true);

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @GetMapping("/refreshLeagueIndex")
    public Boolean refreshLeagueIndex() {

        try {

            leagueIndexingTask.refreshLeagueIndex();

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }



    @GetMapping("/refreshMatch")
    public Boolean refreshMatch(@RequestParam("gameId") long gameId) {

        try {
            preGamesTask.refreshPreEvent(gameId);

            liveGamesTask.refreshLiveGame(gameId);

            postGamesTask.refreshPostGame(gameId);

            bbbServiceUtil.persistAllBallsForGame(gameId);

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @GetMapping("/gamesInCache")
    public GamesInCache gamesInCache() {
        GamesInCache gamesInCache = new GamesInCache();
        gamesInCache.setPreGames(preGames);
        gamesInCache.setLiveGames(liveGames);
        gamesInCache.setPostGames(postGames);
        return gamesInCache;
    }

    private class GamesInCache {

        private Map<Long, Long> preGames;

        private Map<Long, Long> liveGames;

        private Map<Long, Long> postGames;

        public Map<Long, Long> getPreGames() {
            return preGames;
        }

        public void setPreGames(Map<Long, Long> preGames) {
            this.preGames = preGames;
        }

        public Map<Long, Long> getLiveGames() {
            return liveGames;
        }

        public void setLiveGames(Map<Long, Long> liveGames) {
            this.liveGames = liveGames;
        }

        public Map<Long, Long> getPostGames() {
            return postGames;
        }

        public void setPostGames(Map<Long, Long> postGames) {
            this.postGames = postGames;
        }
    }
}
