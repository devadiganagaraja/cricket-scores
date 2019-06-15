package edu.cricket.api.cricketscores.task;

import edu.cricket.api.cricketscores.rest.response.model.Event;
import edu.cricket.api.cricketscores.rest.service.PlayerNameService;
import edu.cricket.api.cricketscores.rest.source.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class EventsListingTask {

    @Autowired
    EventScoreCardTask eventScoreCardTask;

    @Autowired
    PlayerNameService playerNameService;

    private static final Logger log = LoggerFactory.getLogger(EventsListingTask.class);

    RestTemplate restTemplate = new RestTemplate();


    @Autowired
    Map<String,Event> liveEvents;


    public void setLiveEvents(){
        Map<String, Event> eventMap = new ConcurrentHashMap<>();
        getEvents().forEach($ref ->  {
            Event event = getEventData($ref);
            if(null != event) {
                eventMap.put(event.getEventId(), event);
            }
        });
        liveEvents.clear();
        liveEvents.putAll(eventMap);
    }

    private List<String> getEvents(){
        EventListing eventListing = restTemplate.getForObject("http://core.espnuk.org/v2/sports/cricket/events?eventsback=2", EventListing.class);
        return eventListing.getItems().stream().map(ref -> ref.get$ref()).collect(Collectors.toList());
    }



    private Event getEventData(String $ref) {

        EventDetail eventDetail = restTemplate.getForObject($ref, EventDetail.class);


        Competition competition = eventDetail.getCompetitions().get(0);
        if(competition.getCompetitionClass().getInternationalClassId() > 0) {
            Event event = new Event();
            event.setEventId(String.valueOf(Integer.parseInt(competition.getId())*13));
            event.setStartDate(eventDetail.getDate());
            event.setVenue(getEventVenue(eventDetail.getVenues().get(0).get$ref()));
            event.setType(competition.getCompetitionClass().getEventType());
            event.setNote(competition.getNote());

            MatchStatus matchStatus = restTemplate.getForObject(competition.getStatus().get$ref(), MatchStatus.class);
            if(null != matchStatus){
                if(null != matchStatus.getFeaturedAthletes()){
                    matchStatus.getFeaturedAthletes().forEach(featuredAthlete -> {
                        if(featuredAthlete.getAbbreviation().equalsIgnoreCase("POTM")){
                            event.setManOfTheMatch(playerNameService.getPlayerName(featuredAthlete.getPlayerId()));
                        }
                    });
                }
                event.setDayNumber(matchStatus.getDayNumber());
                event.setPeriod(matchStatus.getPeriod());
                if(null != matchStatus.getType()){
                    event.setDescription(matchStatus.getType().getDescription());
                    event.setDescription(matchStatus.getType().getDescription());
                    event.setDetail(matchStatus.getType().getDetail());
                    event.setState(matchStatus.getType().getState());
                }
            }

            List<Competitor> competitorList = competition.getCompetitors();
            if (null != competitorList && competitorList.size() == 2) {
                edu.cricket.api.cricketscores.rest.response.model.Competitor team1 = new edu.cricket.api.cricketscores.rest.response.model.Competitor();
                team1.setTeamName(getEventTeam(competitorList.get(0).getTeam().get$ref()));
                team1.setScore(getEventScore(competitorList.get(0).getScore().get$ref()));
                team1.setWinner(competitorList.get(0).isWinner());
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

    public List<Event> getLiveEvents(){
        return new ArrayList<>(liveEvents.values());
    }

}
