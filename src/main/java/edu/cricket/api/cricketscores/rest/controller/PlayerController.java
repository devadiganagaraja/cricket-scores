package edu.cricket.api.cricketscores.rest.controller;

import edu.cricket.api.cricketscores.rest.response.model.AthleteInfo;
import edu.cricket.api.cricketscores.task.PlayerDetailTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController {

    @Autowired
    PlayerDetailTask playerDetailTask;

    @CrossOrigin
    @RequestMapping("/players/{playerId}")
    public AthleteInfo getPlayerDetails(@PathVariable(value="playerId") String playerId) {
        return playerDetailTask.getPlayerDetails(playerId);
    }
}
