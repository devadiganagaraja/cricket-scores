package edu.cricket.api.cricketscores.rest.controller;

import edu.cricket.api.cricketscores.rest.response.model.Event;
import edu.cricket.api.cricketscores.rest.response.model.League;
import edu.cricket.api.cricketscores.task.EventScoreCardTask;
import edu.cricket.api.cricketscores.task.EventsListingTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class EventController {


    @Autowired
    EventsListingTask eventsListingTask;

    @CrossOrigin
    @RequestMapping("/events")
    public Set<Event> getEvents(@RequestParam(required = false, name = "days") String days) {
        return eventsListingTask.getEventsInfo(days);
    }
}
