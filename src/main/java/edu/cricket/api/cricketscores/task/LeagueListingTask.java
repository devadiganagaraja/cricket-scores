package edu.cricket.api.cricketscores.task;

import edu.cricket.api.cricketscores.domain.*;
import edu.cricket.api.cricketscores.repository.EventRepository;
import edu.cricket.api.cricketscores.repository.LeagueRepository;
import edu.cricket.api.cricketscores.rest.response.model.*;
import edu.cricket.api.cricketscores.rest.source.model.EventListing;
import edu.cricket.api.cricketscores.rest.source.model.Ref;
import edu.cricket.api.cricketscores.rest.source.model.Season;
import edu.cricket.api.cricketscores.utils.CommonUtils;
import edu.cricket.api.cricketscores.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class LeagueListingTask {
    private static final Logger log = LoggerFactory.getLogger(LeagueListingTask.class);

    @Autowired
    LeagueRepository leagueRepository;

    @Autowired
    EventRepository eventRepository;


    @Autowired
    public Map<String, ScoreCard> eventsScoreCardCache;

    @Autowired
    Map<String,Event> liveEvents;

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private EventsListingTask eventsListingTask;

    @Autowired
    private EventScoreCardTask scoreCardTask;



    public void updateLeagueForEvent(Event event) {
        if(null != event) {
            long leagueId = event.getLeagueId();
            LeagueAggregate leagueAggregate;
            Optional<LeagueAggregate> leagueAggregateOptional = leagueRepository.findById(String.valueOf(leagueId));
            if (leagueAggregateOptional.isPresent()) {
                leagueAggregate = leagueAggregateOptional.get();
                if (leagueAggregate.getLeagueInfo().getLeagueSeasonMap().containsKey(event.getLeagueYear())) {
                    LeagueSeason leagueSeason = leagueAggregate.getLeagueInfo().getLeagueSeasonMap().get(event.getLeagueYear());
                    leagueSeason.getEventSet().add(event);
                    updateLastEvent(event, leagueSeason);
                    updateNextEvent(event, leagueSeason);
                    updateLiveEvent(event, leagueSeason);
                    populateLeaders(leagueSeason);
                    populateLeagueTeam(leagueSeason, event);
                } else {
                    setNewLeagueSeason(event, leagueAggregate);
                }
            }else{
                leagueAggregate = new LeagueAggregate();
                leagueAggregate.setId(String.valueOf(leagueId));
                LeagueInfo leagueInfo = new LeagueInfo();
                leagueInfo.setLeagueName(event.getLeagueName());
                leagueAggregate.setLeagueInfo(leagueInfo);
                setNewLeagueSeason(event, leagueAggregate);
            }
            leagueRepository.save(leagueAggregate);
        }
    }

    private void setNewLeagueSeason(Event event, LeagueAggregate leagueAggregate) {
        LeagueSeason leagueSeason = new LeagueSeason();
        leagueSeason.setBattingLeaders(new TreeSet<>());
        leagueSeason.setBowlingLeaders(new TreeSet<>());
        leagueSeason.setEventSet(new HashSet<>());
        updateLastEvent(event, leagueSeason);
        updateNextEvent(event, leagueSeason);
        updateLiveEvent(event, leagueSeason);

        leagueSeason.getEventSet().add(event);
        populateLeaders(leagueSeason);
        populateLeagueTeam(leagueSeason, event);

        leagueAggregate.getLeagueInfo().getLeagueSeasonMap().put(event.getLeagueYear(), leagueSeason);
    }

    private void populateLeagueTeam(LeagueSeason leagueSeason, Event event) {
        if(null == leagueSeason.getTeams()) leagueSeason.setTeams(new HashSet<>());
        LeagueTeam team;
        Competitor competitor1 = event.getTeam1();
        Competitor competitor2 = event.getTeam2();
        long team1Id = Long.valueOf(competitor1.getTeamName().split(":")[1]);
        Optional<LeagueTeam> team1Optional = leagueSeason.getTeams().stream().filter(leagueTeam -> leagueTeam.getId() == team1Id).findFirst();
        if(team1Optional.isPresent()){
            team = team1Optional.get();
            pushResult(event, team, competitor1, competitor2);
        }else{
            team = new LeagueTeam();
            team.setId(team1Id);
            team.setDisplayName(competitor1.getTeamName());
            pushResult(event, team, competitor1, competitor2);
            leagueSeason.getTeams().add(team);
        }


        long team2Id = Long.valueOf(competitor2.getTeamName().split(":")[1]);
        Optional<LeagueTeam> team2Optional = leagueSeason.getTeams().stream().filter(leagueTeam -> leagueTeam.getId() == team2Id).findFirst();
        if(team2Optional.isPresent()){
            team = team2Optional.get();
            pushResult(event, team, competitor2, competitor1);
        }else{
            team = new LeagueTeam();
            team.setId(team2Id);
            team.setDisplayName(competitor2.getTeamName());
            pushResult(event, team, competitor2, competitor1);
            leagueSeason.getTeams().add(team);
        }
    }

    private void pushResult(Event event, LeagueTeam team, Competitor competitor1, Competitor competitor2) {
        if("post".equalsIgnoreCase(event.getState())) {
            if (competitor1.isWinner()) {
                team.getWon().add(event.getEventId());
            } else if (competitor2.isWinner()) {
                team.getLost().add(event.getEventId());
            } else {
                team.getDrawn().add(event.getEventId());
            }
        }
    }

    private void populateLeaders(LeagueSeason leagueSeason) {
        Map<Long, BattingLeader> battingLeaderMap = new HashMap<>();
        Map<Long, BowlingLeader> bowlingLeaderMap = new HashMap<>();
        if(null != leagueSeason.getEventSet()){

            leagueSeason.getEventSet().stream().filter(event -> ! "pre".equalsIgnoreCase(event.getState())).forEach(event -> {
                ScoreCard scoreCard = null;
                if(eventsScoreCardCache.containsKey(event.getEventId())){
                    scoreCard = eventsScoreCardCache.get(event.getEventId());
                }else{
                    Optional<EventAggregate> eventAggregateOpt = eventRepository.findById(event.getEventId());
                    if(eventAggregateOpt.isPresent()){
                        scoreCard = eventAggregateOpt.get().getScoreCard();
                    }
                }
                if(null != scoreCard){
                    scoreCard.getInningsScores().values().forEach(inningsScoreCard -> {
                        BattingCard battingCard = inningsScoreCard.getBattingCard();
                        if(null != battingCard){
                            battingCard.getBatsmanCardSet().forEach(batsmanCard -> {
                                if(battingLeaderMap.containsKey(batsmanCard.getPlayerId())){
                                    BattingLeader battingLeader = battingLeaderMap.get(batsmanCard.getPlayerId());
                                    battingLeader.setMatches(battingLeader.getMatches()+1);
                                    battingLeader.setRuns(battingLeader.getRuns()+ CommonUtils.getIntegerFromString(batsmanCard.getRuns()));
                                    battingLeader.setBalls(battingLeader.getBalls()+ CommonUtils.getIntegerFromString(batsmanCard.getBalls()));
                                    battingLeader.setSixes(battingLeader.getSixes()+ CommonUtils.getIntegerFromString(batsmanCard.getSixes()));
                                    battingLeader.setFours(battingLeader.getFours()+ CommonUtils.getIntegerFromString(batsmanCard.getFours()));

                                }else{
                                    BattingLeader battingLeader = new BattingLeader();
                                    battingLeader.setPlayerId(batsmanCard.getPlayerId());
                                    battingLeader.setPlayerName(batsmanCard.getPlayerName());
                                    battingLeader.setMatches(1);
                                    battingLeader.setRuns(CommonUtils.getIntegerFromString(batsmanCard.getRuns()));
                                    battingLeader.setBalls(CommonUtils.getIntegerFromString(batsmanCard.getBalls()));
                                    battingLeader.setFours(CommonUtils.getIntegerFromString(batsmanCard.getFours()));
                                    battingLeader.setSixes(CommonUtils.getIntegerFromString(batsmanCard.getSixes()));
                                    battingLeaderMap.put(batsmanCard.getPlayerId(), battingLeader);
                                }
                            });
                        }


                        BowlingCard bowlingCard = inningsScoreCard.getBowlingCard();
                        if(null != bowlingCard){
                            bowlingCard.getBowlerCardSet().forEach(bowlerCard -> {
                                if(bowlingLeaderMap.containsKey(bowlerCard.getPlayerId())){
                                    BowlingLeader bowlingLeader = bowlingLeaderMap.get(bowlerCard.getPlayerId());
                                    bowlingLeader.setMatches(bowlingLeader.getMatches()+1);
                                    bowlingLeader.setRunsConceded(bowlingLeader.getRunsConceded()+ CommonUtils.getIntegerFromString(bowlerCard.getConceded()));
                                    bowlingLeader.setWickets(bowlingLeader.getWickets()+ CommonUtils.getIntegerFromString(bowlerCard.getWickets()));
                                    bowlingLeader.setOvers(bowlingLeader.getOvers()+ CommonUtils.getFloatFromString(bowlerCard.getOvers()));
                                    bowlingLeader.setMaidens(bowlingLeader.getMaidens()+ CommonUtils.getIntegerFromString(bowlerCard.getMaidens()));
                                    bowlingLeader.setExtras(bowlingLeader.getExtras()+ CommonUtils.getIntegerFromString(bowlerCard.getWides()+ CommonUtils.getIntegerFromString(bowlerCard.getNoballs())+ CommonUtils.getIntegerFromString(bowlerCard.getByes())+ CommonUtils.getIntegerFromString(bowlerCard.getLegbyes())));

                                }else{
                                    BowlingLeader bowlingLeader = new BowlingLeader();
                                    bowlingLeader.setPlayerId(bowlerCard.getPlayerId());
                                    bowlingLeader.setPlayerName(bowlerCard.getPlayerName());
                                    bowlingLeader.setMatches(1);
                                    bowlingLeader.setRunsConceded(CommonUtils.getIntegerFromString(bowlerCard.getConceded()));
                                    bowlingLeader.setWickets(CommonUtils.getIntegerFromString(bowlerCard.getWickets()));
                                    bowlingLeader.setOvers(CommonUtils.getFloatFromString(bowlerCard.getOvers()));
                                    bowlingLeader.setMaidens(CommonUtils.getIntegerFromString(bowlerCard.getMaidens()));
                                    bowlingLeader.setExtras(CommonUtils.getIntegerFromString(bowlerCard.getWides()+ CommonUtils.getIntegerFromString(bowlerCard.getNoballs())+ CommonUtils.getIntegerFromString(bowlerCard.getByes())+ CommonUtils.getIntegerFromString(bowlerCard.getLegbyes())));
                                    bowlingLeaderMap.put(bowlerCard.getPlayerId(), bowlingLeader);
                                }
                            });
                        }
                    });
                }

            });
        }


        Set<BattingLeader> battingLeaders = new TreeSet<>();
        battingLeaderMap.values().forEach(battingLeader -> {
            battingLeader.setStrikeRate(String.valueOf(battingLeader.getBalls() > 0 ? battingLeader.getRuns()*100/battingLeader.getBalls() : "-"));
            battingLeader.setAverage(String.valueOf(battingLeader.getMatches() > 0 ? battingLeader.getRuns()/battingLeader.getMatches() : "-"));
            battingLeaders.add(battingLeader);
        });
        leagueSeason.setBattingLeaders(battingLeaders);


        Set<BowlingLeader> bowlingLeaders = new TreeSet<>();
        bowlingLeaderMap.values().forEach(bowlingLeader -> {
            bowlingLeader.setStrikeRate(String.valueOf(bowlingLeader.getWickets() > 0 ?bowlingLeader.getRunsConceded()/bowlingLeader.getWickets() : "-"));
            bowlingLeader.setAverage(String.valueOf(bowlingLeader.getOvers() > 0 ? bowlingLeader.getRunsConceded()/bowlingLeader.getOvers() : "-" ));
            bowlingLeaders.add(bowlingLeader);
        });
        leagueSeason.setBowlingLeaders(bowlingLeaders);


    }

    private void updateLastEvent(Event event, LeagueSeason leagueSeason) {
        if("post".equalsIgnoreCase(event.getState())) {
            log.info("update post eventInfo : {}",event);
            leagueSeason.getPostEvents().add(event);
            leagueSeason.getLiveEvents().remove(event);
            leagueSeason.getNextEvents().remove(event);
        }
    }

    private void updateNextEvent(Event event, LeagueSeason leagueSeason) {
        if("pre".equalsIgnoreCase(event.getState())) {
            log.info("update pre eventInfo : {}",event);

            leagueSeason.getPostEvents().remove(event);
            leagueSeason.getLiveEvents().remove(event);
            leagueSeason.getNextEvents().add(event);
        }
    }

    private void updateLiveEvent(Event event, LeagueSeason leagueSeason) {
        leagueSeason.getLiveEvents().remove(event);
        if ("in".equalsIgnoreCase(event.getState())) {
            log.info("update live eventInfo : {}",event);

            leagueSeason.getPostEvents().remove(event);
            leagueSeason.getLiveEvents().add(event);
            leagueSeason.getNextEvents().remove(event);
        }
    }


    public void refreshEventsAndLeagues() {

        log.info("liveEvents :: "+liveEvents.size());
        liveEvents.values().forEach(event -> {
            EventListing eventListing = restTemplate.getForObject("http://core.espnuk.org/v2/sports/cricket/leagues/"+(event.getLeagueId()/13)+"/events?eventsrange=100&limit=100", EventListing.class);
            eventListing.getItems().stream().map(ref -> ref.get$ref()).forEach($ref -> {
                Event eventInfo = eventsListingTask.getEventData($ref);
                if(null != eventInfo && !liveEvents.containsKey(eventInfo.getEventId())){
                    Optional<EventAggregate> eventAggregateOptional = eventRepository.findById(eventInfo.getEventId());
                    EventAggregate eventAggregate;
                    if(eventAggregateOptional.isPresent()) {
                        eventAggregate = eventAggregateOptional.get();
                        eventAggregate.setEventInfo(eventInfo);
                            eventAggregate.setScoreCard(scoreCardTask.getEventScoreCard(eventInfo.getEventId()));
                    }else{
                        eventAggregate = new EventAggregate();
                        eventAggregate.setId(eventInfo.getEventId());
                        eventAggregate.setEventInfo(eventInfo);
                        eventAggregate.setScoreCard(scoreCardTask.getEventScoreCard(eventInfo.getEventId()));
                    }

                    eventRepository.save(eventAggregate);
                }
                updateLeagueForEvent(eventInfo);
            });
        });
    }



    public void refreshLeagues(String leagueId) {
        log.info("refreshLeagues : {}", 1);
        edu.cricket.api.cricketscores.rest.source.model.League league = restTemplate.getForObject("http://core.espnuk.org/v2/sports/cricket/leagues/"+leagueId+"?limit=10000", edu.cricket.api.cricketscores.rest.source.model.League.class);
        if (null != league) {
            Ref seasonRef = league.getSeason();
            Season season = restTemplate.getForObject(seasonRef.get$ref(), Season.class);
            String seasonYear = String.valueOf(season.getYear());

            if (null != season) {
                Optional<LeagueAggregate> leagueAggregateOptional = leagueRepository.findById(leagueId);
                LeagueAggregate leagueAggregate;
                if (leagueAggregateOptional.isPresent()) {
                    leagueAggregate = leagueAggregateOptional.get();

                    if (null != leagueAggregate.getLeagueInfo()) {
                        Map<String, LeagueSeason> leagueSeasonMap = leagueAggregate.getLeagueInfo().getLeagueSeasonMap();
                        if (null != leagueSeasonMap) {
                            LeagueSeason leagueSeason;
                            if (leagueSeasonMap.containsKey(seasonYear)) {
                                leagueSeason = leagueSeasonMap.get(seasonYear);
                            } else {
                                leagueSeason = new LeagueSeason();
                                leagueSeason.setLeagueYear(seasonYear);
                            }
                            populateLeagueSeasonInfo(leagueSeason, season);
                            leagueSeasonMap.put(seasonYear, leagueSeason);
                        }
                    }
                } else {

                    leagueAggregate = new LeagueAggregate();
                    leagueAggregate.setId(leagueId);
                    LeagueInfo leagueInfo = new LeagueInfo();
                    Map<String, LeagueSeason> leagueSeasonMap = new HashMap<>();
                    LeagueSeason leagueSeason = new LeagueSeason();
                    leagueSeason.setLeagueYear(seasonYear);
                    populateLeagueSeasonInfo(leagueSeason, season);
                    leagueSeasonMap.put(seasonYear, leagueSeason);
                    leagueInfo.setLeagueSeasonMap(leagueSeasonMap);
                    leagueAggregate.setLeagueInfo(leagueInfo);
                }
                leagueRepository.save(leagueAggregate);
            }

        }
    }

    public void populateLeagueSeasonInfo( LeagueSeason leagueSeason, Season season){
        leagueSeason.setName(season.getName());
        leagueSeason.setStartDate(DateUtils.getDateFromString(season.getStartDate()));
        leagueSeason.setLeagueStartDate(season.getStartDate());
        leagueSeason.setEndDate(DateUtils.getDateFromString(season.getEndDate()));
        leagueSeason.setLeagueEndDate(season.getEndDate());
    }

    public LeagueDetails getLeagueInfo(String leagueId) {
        Optional<LeagueAggregate> leagueAggregateOptional = leagueRepository.findById(leagueId);
        if(leagueAggregateOptional.isPresent()){
            LeagueDetails leagueDetails = new LeagueDetails();
            LeagueAggregate leagueAggregate =  leagueAggregateOptional.get();
            leagueDetails.setLeagueId(leagueAggregate.getId());
            LeagueInfo leagueInfo = leagueAggregate.getLeagueInfo();
            leagueDetails.setLeagueName(leagueInfo.getLeagueName());
            leagueDetails.setLeagueSeasons(new ArrayList(leagueInfo.getLeagueSeasonMap().values()));
            return leagueDetails;

        }
        else return null;
    }

    public List<LeagueDetails> getLeagues() {
        List<LeagueDetails> leagueDetailsList = new ArrayList<>();
        List<LeagueAggregate> leagueAggregates = leagueRepository.findAll();
        if(null != leagueAggregates){
            leagueAggregates.forEach(leagueAggregate -> {
                LeagueInfo leagueInfo = leagueAggregate.getLeagueInfo();
                if(isTrendingLeague(leagueInfo)){
                    LeagueDetails leagueDetails = new LeagueDetails();
                    leagueDetails.setLeagueId(leagueAggregate.getId());
                    leagueDetails.setLeagueName(leagueInfo.getLeagueName());
                    leagueDetailsList.add(leagueDetails);
                }
            });
        }
        return  leagueDetailsList;
    }

    private boolean isTrendingLeague(LeagueInfo leagueInfo) {
        Map<String, LeagueSeason> seasonsMap =  leagueInfo.getLeagueSeasonMap();
        Optional<LeagueSeason> leagueSeasonOpt = geltLatestLeagueSeason(seasonsMap);
        if(leagueSeasonOpt.isPresent()){
            LeagueSeason leagueSeason = leagueSeasonOpt.get();
            Date startDate = leagueSeason.getStartDate();
            Date endDate = leagueSeason.getEndDate();

            if(null != startDate ){
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 10);
                if(startDate.compareTo(cal.getTime()) > 0){
                    return false;
                }
            }
            if(null != endDate ){
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -10);
                if(endDate.compareTo(cal.getTime()) < 0){
                    return false;
                }
            }
        }

        return true;
    }

    private Optional<LeagueSeason> geltLatestLeagueSeason(Map<String, LeagueSeason> seasonsMap) {
        Optional<LeagueSeason> leagueSeasonOpt = Optional.empty();
        if(null != seasonsMap && seasonsMap.size() > 0 ){
            List<String> seasonKeyList =  new ArrayList<>(seasonsMap.keySet());
            Collections.sort(seasonKeyList);
            leagueSeasonOpt = Optional.ofNullable(seasonsMap.get(seasonKeyList.get(seasonKeyList.size() -1)));

        }
        return leagueSeasonOpt;
    }

}
