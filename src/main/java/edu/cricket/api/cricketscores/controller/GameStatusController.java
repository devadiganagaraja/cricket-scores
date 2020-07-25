package edu.cricket.api.cricketscores.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin
public class GameStatusController {

    @Autowired
    Map<Long, Boolean> preGames;

    @Autowired
    Map<Long, Boolean> liveGames;


    @Autowired
    Map<Long, Boolean> postGames;


    @GetMapping("/gamesInCache")
    public GamesInCache gamesInCache() {
        GamesInCache gamesInCache = new GamesInCache();
        gamesInCache.setPreGames(preGames.keySet());
        gamesInCache.setLiveGames(liveGames.keySet());
        gamesInCache.setPostGames(postGames.keySet());
        return gamesInCache;
    }

    private class GamesInCache {

        private Set<Long> preGames;

        private Set<Long> liveGames;

        private Set<Long> postGames;

        public Set<Long> getPreGames() {
            return preGames;
        }

        public void setPreGames(Set<Long> preGames) {
            this.preGames = preGames;
        }

        public Set<Long> getLiveGames() {
            return liveGames;
        }

        public void setLiveGames(Set<Long> liveGames) {
            this.liveGames = liveGames;
        }

        public Set<Long> getPostGames() {
            return postGames;
        }

        public void setPostGames(Set<Long> postGames) {
            this.postGames = postGames;
        }
    }
}
