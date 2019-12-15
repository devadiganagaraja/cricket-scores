package edu.cricket.api.cricketscores.task;

import edu.cricket.api.cricketscores.domain.EventAggregate;
import edu.cricket.api.cricketscores.repository.EventRepository;
import edu.cricket.api.cricketscores.rest.response.model.*;
import edu.cricket.api.cricketscores.rest.service.PlayerNameService;
import edu.cricket.api.cricketscores.rest.service.TeamNameService;
import edu.cricket.api.cricketscores.rest.source.CompetitorLineScores;
import edu.cricket.api.cricketscores.rest.source.model.*;
import edu.cricket.api.cricketscores.rest.source.model.Competitor;
import edu.cricket.api.cricketscores.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PostEventTask {

    private static final Logger log = LoggerFactory.getLogger(PostEventTask.class);

    @Autowired
    Map<String, Boolean> postEvents;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    TeamNameService teamNameService;


    @Autowired
    PlayerNameService playerNameService;

    RestTemplate restTemplate = new RestTemplate();


    public void refreshPostEvent() {

        postEvents.keySet().forEach(eventId ->{

            EventAggregate eventAggregate = eventRepository.findById(eventId).orElse(getNewEventAggregate(eventId));

            updateEventInfo(eventAggregate);
            updateEventScoreCard(eventAggregate);
            if(null != eventAggregate.getEventInfo() && (eventAggregate.getEventInfo().getInternationalClassId() > 0 || eventAggregate.getEventInfo().getGeneralClassId() > 0 )) {
                eventRepository.save(eventAggregate);
            }

        });
    }

    public void updateEventScoreCard(EventAggregate eventAggregate) {
        ScoreCard scoreCard = new ScoreCard();
        scoreCard.setEventId(eventAggregate.getId());
        try {


            Long eventIdLong = Long.parseLong(eventAggregate.getId()) / 13;
            String $ref = "http://core.espnuk.org/v2/sports/cricket/events/" + eventIdLong + "/competitions/" + eventIdLong + "/competitors";
            scoreCard.setInningsScores(new HashMap<>());

            EventListing eventCompetitorListing = restTemplate.getForObject($ref, EventListing.class);

            eventCompetitorListing.getItems().forEach(competitorRef -> {
                Competitor competitor = restTemplate.getForObject(competitorRef.get$ref(), Competitor.class);
                scoreCard.getCompetitors().add(competitor.getId());

                Roster roster = restTemplate.getForObject(competitor.get$ref() + "/roster", Roster.class);
                if (null != roster && null != roster.getEntries()) {
                    AtomicInteger unknownRosterIndex = new AtomicInteger(101);

                    roster.getEntries().forEach(playerRoster -> {
                        log.debug("playerRoster :{}",playerRoster);

                        RosterLineScores rosterLineScores = restTemplate.getForObject(playerRoster.getLinescores().get$ref(), RosterLineScores.class);

                        if (null != rosterLineScores.getItems() && rosterLineScores.getItems().size() > 0) {
                            log.debug("playerRoster1 :{}",playerRoster);
                            rosterLineScores.getItems().forEach(rosterLineScore -> {
                                log.debug("playerRoster2 :{}, linescore {}",playerRoster, rosterLineScore);


                                LineScoreStatistics stats = restTemplate.getForObject(rosterLineScore.getStatistics(), LineScoreStatistics.class);

                                InningsScoreCard inningsScoreCard;
                                if (scoreCard.getInningsScores().containsKey(rosterLineScore.getPeriod())) {
                                    inningsScoreCard = scoreCard.getInningsScores().get(rosterLineScore.getPeriod());
                                } else {
                                    inningsScoreCard = new InningsScoreCard();

                                    scoreCard.getInningsScores().put(rosterLineScore.getPeriod(), inningsScoreCard);
                                }
                                if (rosterLineScore.isBatting()) {
                                    log.debug("playerRoster3 :{}, linescore {}",playerRoster, rosterLineScore);

                                    BattingCard battingCard;
                                    if (null == inningsScoreCard.getBattingCard()) {
                                        battingCard = new BattingCard();
                                        battingCard.setBatsmanCardSet(new TreeSet<>());
                                        inningsScoreCard.setBattingCard(battingCard);
                                    } else {
                                        battingCard = inningsScoreCard.getBattingCard();
                                    }
                                    BatsmanCard batsmanCard = new BatsmanCard();
                                    batsmanCard.setPlayerId(playerRoster.getPlayerId()*13);
                                    batsmanCard.setPlayerName(playerNameService.getPlayerName(playerRoster.getPlayerId()));

                                    try {
                                        stats.getSplits().getCategories().get(0).getStats().stream().forEach(stat -> {
                                            switch (stat.getName()) {
                                                case "ballsFaced":
                                                    batsmanCard.setBalls(stat.getDisplayValue());
                                                    break;

                                                case "batted":
                                                    batsmanCard.setBatted(stat.getDisplayValue().equals("1") ? true : false);
                                                    break;

                                                case "outs":
                                                    batsmanCard.setOut(stat.getDisplayValue().equals("1") ? true : false);
                                                    break;

                                                case "runs":
                                                    batsmanCard.setRuns(stat.getDisplayValue());
                                                    break;

                                                case "fours":
                                                    batsmanCard.setFours(stat.getDisplayValue());
                                                    break;

                                                case "sixes":
                                                    batsmanCard.setSixes(stat.getDisplayValue());
                                                    break;
                                            }
                                        });
                                        if (null != stats.getSplits().getBatting() && null != stats.getSplits().getBatting().getOutDetails() && null != stats.getSplits().getBatting().getOutDetails().getShortText())
                                            batsmanCard.setBattingDescription(stats.getSplits().getBatting().getOutDetails().getShortText().replace("&dagger;", "").replace("&amp;", "&"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    batsmanCard.setPosition(rosterLineScore.getOrder()> 0 && batsmanCard.isBatted() ? rosterLineScore.getOrder():unknownRosterIndex.incrementAndGet());
                                    log.debug("playerRoster4 :{}, linescore {}, batsmanCard {}",playerRoster, rosterLineScore,batsmanCard);

                                    battingCard.getBatsmanCardSet().add(batsmanCard);
                                } else {
                                    BowlingCard bowlingCard;
                                    if (null == inningsScoreCard.getBowlingCard()) {
                                        bowlingCard = new BowlingCard();
                                        bowlingCard.setBowlerCardSet(new TreeSet<>());
                                        inningsScoreCard.setBowlingCard(bowlingCard);
                                    } else {
                                        bowlingCard = inningsScoreCard.getBowlingCard();
                                    }
                                    BowlerCard bowlerCard = new BowlerCard();
                                    bowlerCard.setPlayerId(playerRoster.getPlayerId()*13);
                                    bowlerCard.setPlayerName(playerNameService.getPlayerName(playerRoster.getPlayerId()));
                                    try {
                                        stats.getSplits().getCategories().get(0).getStats().stream().forEach(stat -> {
                                            switch (stat.getName()) {
                                                case "overs":
                                                    bowlerCard.setOvers(stat.getDisplayValue());
                                                    break;

                                                case "bowled":
                                                    bowlerCard.setBowled(stat.getDisplayValue().equals("1") ? true : false);
                                                    break;

                                                case "conceded":
                                                    bowlerCard.setConceded(stat.getDisplayValue());
                                                    break;

                                                case "maidens":
                                                    bowlerCard.setMaidens(stat.getDisplayValue());
                                                    break;

                                                case "noballs":
                                                    bowlerCard.setNoballs(stat.getDisplayValue());
                                                    break;
                                                case "wides":
                                                    bowlerCard.setWides(stat.getDisplayValue());
                                                    break;

                                                case "byes":
                                                    bowlerCard.setByes(stat.getDisplayValue());
                                                    break;

                                                case "legbyes":
                                                    bowlerCard.setLegbyes(stat.getDisplayValue());
                                                    break;

                                                case "wickets":
                                                    bowlerCard.setWickets(stat.getDisplayValue());
                                                    break;

                                                case "stumped":
                                                    bowlerCard.setStumped(stat.getDisplayValue());
                                                    break;

                                                case "caught":
                                                    bowlerCard.setCaught(stat.getDisplayValue());
                                                    break;
                                            }
                                        });

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    bowlerCard.setLive(playerRoster.getActiveName());
                                    bowlerCard.setPosition(rosterLineScore.getOrder());
                                    if (bowlerCard.isBowled())
                                        bowlingCard.getBowlerCardSet().add(bowlerCard);
                                }
                            });
                        }

                    });
                }

                CompetitorLineScores competitorLineScores = restTemplate.getForObject(competitor.get$ref() + "/linescores/1", CompetitorLineScores.class);
                if (null != competitorLineScores && null != competitorLineScores.getItems()) {
                    competitorLineScores.getItems().stream().forEach(competitorLineScore -> {
                        if (competitorLineScore.isBatting()) {
                            LineScoreStatistics competitorLineScoreStats = restTemplate.getForObject(competitorLineScore.getStatistics().get$ref(), LineScoreStatistics.class);
                            if (null != competitorLineScoreStats && null != competitorLineScoreStats.getSplits() && null != competitorLineScoreStats.getSplits().getCategories() && competitorLineScoreStats.getSplits().getCategories().size() > 0) {
                                Category BattingStatsCategory = competitorLineScoreStats.getSplits().getCategories().get(0);
                                if (null != BattingStatsCategory && null != BattingStatsCategory.getStats()) {
                                    InningsInfo inningsInfo = new InningsInfo();

                                    setBattingTeamInfo(inningsInfo, competitorLineScoreStats);

                                    BattingStatsCategory.getStats().forEach(stat -> {
                                        switch (stat.getName()) {
                                            case "runs":
                                                inningsInfo.setRuns(Integer.valueOf(stat.getDisplayValue()));
                                                break;
                                            case "target":
                                                inningsInfo.setTarget(Integer.valueOf(stat.getDisplayValue()));
                                                break;
                                            case "wickets":
                                                inningsInfo.setWickets(Integer.valueOf(stat.getDisplayValue()));
                                                break;
                                            case "overLimit":
                                                inningsInfo.setOverLimit(stat.getDisplayValue());
                                                break;
                                            case "runRate":
                                                inningsInfo.setRunRate(stat.getDisplayValue());
                                                break;
                                            case "legbyes":
                                                inningsInfo.setLegByes(Integer.valueOf(stat.getDisplayValue()));
                                                break;
                                            case "byes":
                                                inningsInfo.setByes(Integer.valueOf(stat.getDisplayValue()));
                                                break;
                                            case "wides":
                                                inningsInfo.setWides(Integer.valueOf(stat.getDisplayValue()));
                                                break;
                                            case "noballs":
                                                inningsInfo.setNoBalls(Integer.valueOf(stat.getDisplayValue()));
                                                break;
                                            case "lead":
                                                inningsInfo.setLead(Integer.valueOf(stat.getDisplayValue()));

                                            case "overs":
                                                inningsInfo.setOvers(stat.getDisplayValue());
                                                break;

                                            case "liveCurrent":
                                                inningsInfo.setLiveInnings(stat.getDisplayValue().equals("1")?true:false);
                                                break;

                                        }
                                    });
                                    scoreCard.getInningsScores().get(competitorLineScore.getPeriod()).setInningsInfo(inningsInfo);
                                }
                            }
                        }
                    });
                }
            });
            eventAggregate.setScoreCard(scoreCard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateEventInfo(EventAggregate eventAggregate) {
        try {

            String eventId = eventAggregate.getId();
            long sourceEventId = Long.valueOf(eventId)/13;
            String eventRef = "http://core.espnuk.org/v2/sports/cricket/events/"+sourceEventId;

            EventDetail eventDetail = restTemplate.getForObject(eventRef, EventDetail.class);

            Competition competition = eventDetail.getCompetitions().get(0);
            Event event = new Event();
            int intClassId = competition.getCompetitionClass().getInternationalClassId();
            if (intClassId > 10) {
                postEvents.remove(eventId);
                return;
            }
            event.setInternationalClassId(intClassId);
            int genClassId = competition.getCompetitionClass().getGeneralClassId();
            if (intClassId == 0 && genClassId > 10){
                postEvents.remove(eventId);
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
                event.setTeam1(team1);

                edu.cricket.api.cricketscores.rest.response.model.Competitor team2 = new edu.cricket.api.cricketscores.rest.response.model.Competitor();
                team2.setTeamName(getEventTeam(competitorList.get(1).getTeam().get$ref()));
                team2.setScore(getEventScore(competitorList.get(1).getScore().get$ref()));
                event.setTeam2(team2);
            }
            eventAggregate.setEventInfo(event);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }{
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

    private void setBattingTeamInfo(InningsInfo inningsInfo, LineScoreStatistics lineScoreStatistics) {
        if(null != lineScoreStatistics.getTeam() && null != lineScoreStatistics.getTeam().get$ref()){
            Team team = restTemplate.getForObject(lineScoreStatistics.getTeam().get$ref(), Team.class);
            inningsInfo.setBattingTeamId(teamNameService.getTeamId(team.getId()));
            inningsInfo.setBattingTeamName(teamNameService.getTeamName(team.getId()));
        }
    }

}
