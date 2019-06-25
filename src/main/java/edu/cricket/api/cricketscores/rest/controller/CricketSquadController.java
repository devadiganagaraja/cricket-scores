package edu.cricket.api.cricketscores.rest.controller;

import edu.cricket.api.cricketscores.rest.response.model.Event;
import edu.cricket.api.cricketscores.rest.response.model.EventBestEleven;
import edu.cricket.api.cricketscores.rest.response.model.Squad;
import edu.cricket.api.cricketscores.task.EventScoreCardTask;
import edu.cricket.api.cricketscores.task.EventSquadsTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class CricketSquadController {


    private static final Logger logger = LoggerFactory.getLogger(CricketSquadController.class);



    @Autowired
    EventSquadsTask eventSquadsTask;


    @Autowired
    Map<String,Event> liveEvents;

    @CrossOrigin
    @RequestMapping("/bestEleven/event/{eventId}")
    public EventBestEleven getBestEleven(@PathVariable(value="eventId") String eventId) {

        EventBestEleven eventBestEleven = new EventBestEleven();
        Event event = liveEvents.get(eventId);
        eventBestEleven.setEvent(event);
        Squad squad1 = new Squad();
        squad1.setTeamName(event.getTeam1().getTeamName());
        long sourceTeam1Id = Long.valueOf(event.getTeam1().getTeamName().split(":")[1])/13;

        squad1.setPlayers(eventSquadsTask.getLeagueTeamPlayers(event.getLeagueId(), sourceTeam1Id, event.getInternationalClassId()));

        eventBestEleven.setSquad1(squad1);
        Squad squad2 = new Squad();
        squad2.setTeamName(event.getTeam2().getTeamName());
        long sourceTeam2Id = Long.valueOf(event.getTeam2().getTeamName().split(":")[1])/13;
        squad2.setPlayers(eventSquadsTask.getLeagueTeamPlayers(event.getLeagueId(), sourceTeam2Id, event.getInternationalClassId()));
        eventBestEleven.setSquad2(squad2);
        return eventBestEleven;
    }
}
