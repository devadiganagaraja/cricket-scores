package edu.cricket.api.cricketscores.rest.controller;

import edu.cricket.api.cricketscores.rest.response.model.Event;
import edu.cricket.api.cricketscores.rest.response.model.EventInfo;
import edu.cricket.api.cricketscores.task.EventScoreTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EventScoreController {

    @Autowired
    EventScoreTask eventScoreTask;

    @Autowired
    EventScoreTask EventScoreCardTask;

    @RequestMapping("/scores")
    public List<Event> getEvents( String eventId) {
        return eventScoreTask.getEventScore();
    }

    @RequestMapping("/scores/{event}")
    public EventInfo getEventDetails(@PathVariable(value="event") String eventId) {
        return eventScoreTask.getEventInfo(eventId);
    }
}
