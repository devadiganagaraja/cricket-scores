package edu.cricket.api.cricketscores.task;

import edu.cricket.api.cricketscores.domain.EventAggregate;
import edu.cricket.api.cricketscores.domain.QEventAggregate;
import edu.cricket.api.cricketscores.repository.EventRepository;
import edu.cricket.api.cricketscores.rest.response.MatchCommentary;
import edu.cricket.api.cricketscores.rest.response.model.*;
import edu.cricket.api.cricketscores.rest.response.model.League;
import edu.cricket.api.cricketscores.rest.service.PlayerNameService;
import edu.cricket.api.cricketscores.rest.service.TeamNameService;
import edu.cricket.api.cricketscores.rest.source.model.*;
import edu.cricket.api.cricketscores.rest.source.model.Competitor;
import edu.cricket.api.cricketscores.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class EventsListingTask {

    @Autowired
    EventScoreCardTask eventScoreCardTask;

    @Autowired
    LeagueListingTask leagueListingTask;

    @Autowired
    PlayerNameService playerNameService;

    @Autowired
    TeamNameService teamNameService;

    private static final Logger log = LoggerFactory.getLogger(EventsListingTask.class);

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    Map<String,Event> liveEvents;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    QEventAggregate qEventAggregate;


    @Autowired
    public Map<String, MatchCommentary> eventsCommsCache;


    public void setEvents(){
        Map<String, Event> eventMap = new ConcurrentHashMap<>();
        getEvents().forEach($ref ->  {
            Event event = null;
            try {
                log.info("$ref :{}", $ref);
                event = getEventData($ref);
                leagueListingTask.updateLeagueForEvent(event);
                if (null != event) {
                    eventMap.put(event.getEventId(), event);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            log.info("event :{}", event);

        });
        log.info("eventMap :{}", eventMap.size());


        Set<String> nonLiveEvents = new HashSet<>();
        liveEvents.keySet().forEach(event -> {

            if( !eventMap.containsKey(event)){
                nonLiveEvents.add(event);
            }
        });
        nonLiveEvents.forEach(eventId -> {
            liveEvents.remove(eventId);
            eventsCommsCache.remove(eventId);
        });

        log.info("finally eventMap :{}", eventMap.size());
        liveEvents.putAll(eventMap);
        log.info("finally liveEvents.keySet() :{}", liveEvents.keySet());

    }

    private List<String> getEvents(){
        EventListing eventListing = restTemplate.getForObject("http://core.espnuk.org/v2/sports/cricket/events", EventListing.class);

        return eventListing.getItems().stream().map(ref -> ref.get$ref()).collect(Collectors.toList());
    }



    public Event getEventData(String $ref) {
        try {

            EventDetail eventDetail = restTemplate.getForObject($ref, EventDetail.class);

            Competition competition = eventDetail.getCompetitions().get(0);
            Event event = new Event();
            int intClassId = competition.getCompetitionClass().getInternationalClassId();
            if (intClassId > 10) return null;
            event.setInternationalClassId(intClassId);
            int genClassId = competition.getCompetitionClass().getGeneralClassId();
            if (intClassId == 0 && genClassId > 10) return null;
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
                event.setTeam1(team1);

                edu.cricket.api.cricketscores.rest.response.model.Competitor team2 = new edu.cricket.api.cricketscores.rest.response.model.Competitor();
                team2.setTeamName(getEventTeam(competitorList.get(1).getTeam().get$ref()));
                team2.setScore(getEventScore(competitorList.get(1).getScore().get$ref()));
                event.setTeam2(team2);
            }
            return event;
        }catch (Exception e) {
            return null;
        }
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

    public Set<Event> getEventsInfo(String deltaDays){
        int delta = 0;
        try {
            delta = Integer.parseInt(deltaDays);
        }catch (Exception e){
            log.error("deltaDays is not parsable to int : {}", e.getMessage());
        }

        Set<Event> eventInfos =  new TreeSet<>();

        Date date = org.apache.commons.lang.time.DateUtils.addDays(new Date(), delta);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        date = calendar.getTime();

            Iterable<EventAggregate> eventItr = eventRepository.findAll((qEventAggregate.eventInfo.startDate.lt(date).and(qEventAggregate.eventInfo.endDate.goe(date)))
                                                                        .or(qEventAggregate.eventInfo.startDate.eq(date)));
        if(null != eventItr){
            eventItr.forEach(eventAggregate -> {
                eventInfos.add(eventAggregate.getEventInfo());
            });
        }
        return eventInfos;

    }

    public List<League> getLiveEvents(){

        List<Event> eventInfos =  new ArrayList<>(liveEvents.values());
        Set<League> leagues = new HashSet<>();
        if(null != eventInfos) {
            eventInfos.forEach(event -> {
                League eventLeague = new League();
                eventLeague.setLeagueId(event.getLeagueId());

                if(leagues.contains(eventLeague)){
                    leagues.forEach(league -> {
                        if (league.getLeagueId() == event.getLeagueId()) {
                            league.getEventSet().add(event);
                        }
                    });
                }else {
                    League newLeague = new League();
                    newLeague.setLeagueId(event.getLeagueId());
                    newLeague.setLeagueName(event.getLeagueName());
                    newLeague.setLeagueYear(event.getLeagueYear());
                    newLeague.setClassId(event.getInternationalClassId() > 0 ? event.getInternationalClassId() : event.getGeneralClassId());
                    newLeague.setEventSet(new HashSet<>());
                    newLeague.getEventSet().add(event);
                    leagues.add(newLeague);
                }
            });
        }
        List<League> leaguesList =  new ArrayList<>(leagues);
        leaguesList.sort(Comparator.comparing(League::getClassId));
        return leaguesList;
    }

}
