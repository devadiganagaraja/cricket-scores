package edu.cricket.api.cricketscores.task;

import edu.cricket.api.cricketscores.rest.response.model.Event;
import edu.cricket.api.cricketscores.rest.response.model.EventInfo;
import edu.cricket.api.cricketscores.rest.source.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventScoreTask {

    @Autowired
    EventScoreCardTask eventScoreCardTask;

    private static final Logger log = LoggerFactory.getLogger(EventScoreTask.class);
    RestTemplate restTemplate = new RestTemplate();

    public List<Event> getEventScore(){
        List<Event> events = new ArrayList<>();
        getEvents().forEach($ref ->  {
            Event event = getEventData($ref);
            if(null != event)
                events.add(event);
        });
        return events;
    }

    private List<String> getEvents(){
        EventListing eventListing = restTemplate.getForObject("http://core.espnuk.org/v2/sports/cricket/events?eventsback=2", EventListing.class);
        return eventListing.getItems().stream().map(ref -> ref.get$ref()).collect(Collectors.toList());
    }


    public EventInfo getEventInfo(String eventId){
        EventInfo eventInfo = new EventInfo();

        Event event = getEventData("http://core.espnuk.org/v2/sports/cricket/events/"+eventId);
        eventInfo.setEvent(event);
        eventInfo.setScoreCard(eventScoreCardTask.getEventScoreCard("http://core.espnuk.org/v2/sports/cricket/events/"+eventId+"/competitions/"+eventId+"/competitors"));
        return eventInfo;


    }

    private Event getEventData(String $ref) {

        EventDetail eventDetail = restTemplate.getForObject($ref, EventDetail.class);


        Competition competition = eventDetail.getCompetitions().get(0);
        if(competition.getCompetitionClass().getInternationalClassId() > 0) {
            Event event = new Event();
            event.setEventId(competition.getId());
            event.setStartDate(eventDetail.getDate());
            event.setVenue(getEventVenue(eventDetail.getVenues().get(0).get$ref()));
            event.setType(competition.getCompetitionClass().getEventType());
            List<Competitor> competitorList = competition.getCompetitors();
            if (null != competitorList && competitorList.size() == 2) {
                edu.cricket.api.cricketscores.rest.response.model.Competitor team1 = new edu.cricket.api.cricketscores.rest.response.model.Competitor();
                team1.setTeamName(getEventTeam(competitorList.get(0).getTeam().get$ref()));
                team1.setScore(getEventScore(competitorList.get(0).getScore().get$ref()));
                event.setTeam1(team1);

                edu.cricket.api.cricketscores.rest.response.model.Competitor team2 = new edu.cricket.api.cricketscores.rest.response.model.Competitor();
                team2.setTeamName(getEventTeam(competitorList.get(1).getTeam().get$ref()));
                team2.setScore(getEventScore(competitorList.get(1).getScore().get$ref()));
                event.setTeam2(team2);
            }
            return event;
        }
        return null;
    }

    public String getEventVenue(String $ref){
        Venue venue = restTemplate.getForObject($ref , Venue.class);
        return venue.getFullName();
    }

    public String getEventTeam(String $ref){
        Team team = restTemplate.getForObject($ref , Team.class);
        return team.getDisplayName();
    }

    public String getEventScore(String $ref){
        Score score = restTemplate.getForObject($ref , Score.class);
        return  score.getValue();
    }


}
