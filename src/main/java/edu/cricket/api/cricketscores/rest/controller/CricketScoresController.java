package edu.cricket.api.cricketscores.rest.controller;

import edu.cricket.api.cricketscores.rest.response.model.EventInfo;
import edu.cricket.api.cricketscores.rest.response.model.League;
import edu.cricket.api.cricketscores.task.EventScoreCardTask;
import edu.cricket.api.cricketscores.task.EventsListingTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class CricketScoresController {

    @Autowired
    EventScoreCardTask eventScoreCardTask;

    @Autowired
    EventsListingTask eventsListingTask;

    @CrossOrigin
    @RequestMapping("/scores")
    public List<League> getEvents() {
        return eventsListingTask.getLiveEvents();
    }

    @CrossOrigin
    @RequestMapping("/scores/{event}")
    public EventInfo getEventDetails(@PathVariable(value="event") String eventId) {
        return eventScoreCardTask.getEventInfo(eventId);
    }
}
