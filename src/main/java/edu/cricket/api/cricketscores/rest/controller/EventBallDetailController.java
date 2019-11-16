package edu.cricket.api.cricketscores.rest.controller;


import edu.cricket.api.cricketscores.rest.response.MatchCommentary;
import edu.cricket.api.cricketscores.task.EventBallsTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class EventBallDetailController {



    @Autowired
    EventBallsTask eventBallsTask;


    @CrossOrigin
    @RequestMapping("/comms/{eventId}")
    public MatchCommentary getEventComms(@PathVariable(value="eventId") String eventId) {
        return eventBallsTask.fetchBallDetailsForMatch(eventId);
    }
}
