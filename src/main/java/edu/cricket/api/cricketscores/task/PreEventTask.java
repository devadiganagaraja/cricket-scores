package edu.cricket.api.cricketscores.task;

import edu.cricket.api.cricketscores.domain.EventAggregate;
import edu.cricket.api.cricketscores.repository.EventRepository;
import edu.cricket.api.cricketscores.rest.response.model.Event;
import edu.cricket.api.cricketscores.rest.service.PlayerNameService;
import edu.cricket.api.cricketscores.rest.service.TeamNameService;
import edu.cricket.api.cricketscores.rest.source.model.*;
import edu.cricket.api.cricketscores.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PreEventTask {

    private static final Logger log = LoggerFactory.getLogger(PreEventTask.class);

    @Autowired
    Map<String, Boolean> preEvents;

    @Autowired
    EventRepository eventRepository;


    RestTemplate restTemplate = new RestTemplate();


    @Autowired
    TeamNameService teamNameService;


    @Autowired
    PlayerNameService playerNameService;


    @Autowired
    EventSquadsTask eventSquadsTask;


    public void refreshPreEvents() {

        preEvents.keySet().forEach(eventId ->{

            EventAggregate eventAggregate = eventRepository.findById(eventId).orElse(getNewEventAggregate(eventId));
            updateEventInfo(eventAggregate);
            eventRepository.save(eventAggregate);

        });
    }

    private void updateEventInfo(EventAggregate eventAggregate) {
        try {
            String eventId = eventAggregate.getId();
            long sourceEventId = Long.valueOf(eventId)/13;
            String eventRef = "http://core.espnuk.org/v2/sports/cricket/events/"+sourceEventId;

            EventDetail eventDetail = restTemplate.getForObject(eventRef, EventDetail.class);

            Competition competition = eventDetail.getCompetitions().get(0);
            Event event = new Event();
            int intClassId = competition.getCompetitionClass().getInternationalClassId();
            if (intClassId > 10) {
                preEvents.remove(eventId);
                return;
            }
            event.setInternationalClassId(intClassId);
            int genClassId = competition.getCompetitionClass().getGeneralClassId();
            if (intClassId == 0 && genClassId > 10){
                preEvents.remove(eventId);
                return;
            }
            event.setGeneralClassId(genClassId);
            event.setEventId(String.valueOf(Integer.parseInt(competition.getId()) * 13));
            event.setStartDate(DateUtils.getDateFromString(eventDetail.getDate()));
            event.setEndDate(DateUtils.getDateFromString(eventDetail.getEndDate()));
            event.setVenue(getEventVenue(eventDetail.getVenues().get(0).get$ref()));

            setSeason(eventDetail, event);
            event.setType(competition.getCompetitionClass().getEventType());
            event.setNote(competition.getNote());

            MatchStatus matchStatus = restTemplate.getForObject(competition.getStatus().get$ref(), MatchStatus.class);
            if (null != matchStatus) {
                if (null != matchStatus.getFeaturedAthletes()) {
                    matchStatus.getFeaturedAthletes().forEach(featuredAthlete -> {
                        if (featuredAthlete.getAbbreviation().equalsIgnoreCase("POTM")) {
                            event.setManOfTheMatch(playerNameService.getPlayerName(featuredAthlete.getPlayerId()));
                        }
                    });
                }
                event.setDayNumber(matchStatus.getDayNumber());
                event.setPeriod(matchStatus.getPeriod());
                if (null != matchStatus.getType()) {
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
                if(event.getInternationalClassId() > 0) {
                    long sourceTeam1Id = Long.valueOf(team1.getTeamName().split(":")[1]) / 13;
                    team1.setSquad(eventSquadsTask.getLeagueTeamPlayers(event.getLeagueId(), sourceTeam1Id, event));
                }
                event.setTeam1(team1);

                edu.cricket.api.cricketscores.rest.response.model.Competitor team2 = new edu.cricket.api.cricketscores.rest.response.model.Competitor();
                team2.setTeamName(getEventTeam(competitorList.get(1).getTeam().get$ref()));
                team2.setScore(getEventScore(competitorList.get(1).getScore().get$ref()));
                if( event.getInternationalClassId() > 0) {
                    long sourceTeam2Id = Long.valueOf(team2.getTeamName().split(":")[1]) / 13;
                    team2.setSquad(eventSquadsTask.getLeagueTeamPlayers(event.getLeagueId(), sourceTeam2Id, event));
                }
                event.setTeam2(team2);
            }

            eventAggregate.setEventInfo(event);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public EventAggregate getNewEventAggregate(String eventId){
        EventAggregate eventAggregate = new EventAggregate();
        eventAggregate.setId(eventId);
        return eventAggregate;
    }

    private void setSeason(EventDetail eventDetail, Event event) {
        Season season = restTemplate.getForObject(eventDetail.getSeason().get$ref() , Season.class);
        event.setLeagueId(season.getId()*13);
        event.setLeagueName(season.getName());
        event.setLeagueYear(String.valueOf(season.getYear()));
    }

    public String getEventVenue(String $ref){
        Venue venue = restTemplate.getForObject($ref , Venue.class);
        return venue.getFullName();
    }

    public String getEventTeam(String $ref){
        Team team = restTemplate.getForObject($ref , Team.class);
        return teamNameService.getTeamName(team.getId());
    }

    public String getEventScore(String $ref){
        Score score = restTemplate.getForObject($ref , Score.class);
        return  score.getValue();
    }
}
