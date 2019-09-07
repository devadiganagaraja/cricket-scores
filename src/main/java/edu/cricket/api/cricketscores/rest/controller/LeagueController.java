package edu.cricket.api.cricketscores.rest.controller;

import edu.cricket.api.cricketscores.domain.LeagueInfo;
import edu.cricket.api.cricketscores.rest.response.model.LeagueDetails;
import edu.cricket.api.cricketscores.task.LeagueListingTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeagueController {

    @Autowired
    LeagueListingTask leagueListingTask;

    @CrossOrigin
    @RequestMapping("/leagues/{leagueId}")
    public LeagueDetails getLeagueInfo(@PathVariable(value="leagueId") String leagueId) {
        return leagueListingTask.getLeagueInfo(leagueId);
    }
}
