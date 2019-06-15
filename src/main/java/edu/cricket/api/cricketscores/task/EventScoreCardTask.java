package edu.cricket.api.cricketscores.task;

import edu.cricket.api.cricketscores.rest.response.model.*;
import edu.cricket.api.cricketscores.rest.scheduler.EventListingScheduler;
import edu.cricket.api.cricketscores.rest.service.PlayerNameService;
import edu.cricket.api.cricketscores.rest.source.CompetitorLineScores;
import edu.cricket.api.cricketscores.rest.source.model.*;
import edu.cricket.api.cricketscores.rest.source.model.Competitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

@Service
public class EventScoreCardTask {

    private static final Logger logger = LoggerFactory.getLogger(EventScoreCardTask.class);


    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    PlayerNameService playerNameService;


    @Autowired
    Map<String,Event> liveEvents;

    @Autowired
    public Map<String,ScoreCard> eventsScoreCardCache;



    public EventInfo getEventInfo(String eventId){
        return populateEventInfo(eventId);


    }

    private EventInfo populateEventInfo(String eventId) {
        EventInfo eventInfo = new EventInfo();
        eventInfo.setEvent(liveEvents.get(eventId));
        eventInfo.setScoreCard(eventsScoreCardCache.get(eventId));
        return eventInfo;
    }

    public ScoreCard getEventScoreCard(String eventId) {
        Long eventIdLong = Long.parseLong(eventId) /13;
        String $ref = "http://core.espnuk.org/v2/sports/cricket/events/"+eventIdLong+"/competitions/"+eventIdLong+"/competitors";
        ScoreCard scoreCard = new ScoreCard();
        scoreCard.setInningsScores(new HashMap<>());


        logger.info("$ref==> {}",$ref);
        EventListing eventCompetitorListing = restTemplate.getForObject($ref, EventListing.class);

        eventCompetitorListing.getItems().forEach(competitorRef -> {
            Competitor competitor = restTemplate.getForObject(competitorRef.get$ref(), Competitor.class);
            Roster roster = restTemplate.getForObject(competitor.get$ref()+"/roster", Roster.class);
            if(null != roster && null != roster.getEntries()){
                roster.getEntries().forEach(playerRoster -> {

                    RosterLineScores rosterLineScores = restTemplate.getForObject(playerRoster.getLinescores().get$ref(), RosterLineScores.class);

                    if(null != rosterLineScores.getItems() && rosterLineScores.getItems().size() > 0) {
                        rosterLineScores.getItems().forEach(rosterLineScore -> {


                            LineScoreStatistics stats = restTemplate.getForObject(rosterLineScore.getStatistics(), LineScoreStatistics.class);

                            InningsScoreCard inningsScoreCard;
                            if(scoreCard.getInningsScores().containsKey(rosterLineScore.getPeriod())){
                                inningsScoreCard = scoreCard.getInningsScores().get(rosterLineScore.getPeriod());
                            }else{
                                inningsScoreCard = new InningsScoreCard();

                                scoreCard.getInningsScores().put(rosterLineScore.getPeriod(), inningsScoreCard);
                            }
                            if(rosterLineScore.isBatting()) {
                                BattingCard battingCard;
                                if(null == inningsScoreCard.getBattingCard()) {
                                    battingCard = new BattingCard();
                                    battingCard.setBatsmanCardSet(new TreeSet<>());
                                    inningsScoreCard.setBattingCard(battingCard);
                                }else{
                                    battingCard = inningsScoreCard.getBattingCard();
                                }
                                BatsmanCard batsmanCard = new BatsmanCard();
                                batsmanCard.setPlayerId(playerRoster.getPlayerId());
                                batsmanCard.setPlayerName(playerNameService.getPlayerName(playerRoster.getPlayerId()));

                                try {
                                    stats.getSplits().getCategories().get(0).getStats().stream().forEach(stat -> {
                                        switch (stat.getName()){
                                            case "ballsFaced":
                                                batsmanCard.setBalls(stat.getDisplayValue());
                                                break;

                                            case "batted":
                                                batsmanCard.setBatted(stat.getDisplayValue().equals("1")?true:false);
                                                break;

                                            case "outs":
                                                batsmanCard.setOut(stat.getDisplayValue().equals("1")?true:false);
                                                break;

                                            case "runs":
                                                batsmanCard.setRuns(stat.getDisplayValue());
                                                break;
                                        }
                                    });
                                    if(null != stats.getSplits().getBatting() && null != stats.getSplits().getBatting().getOutDetails() &&  null != stats.getSplits().getBatting().getOutDetails().getShortText())
                                        batsmanCard.setBattingDescription(stats.getSplits().getBatting().getOutDetails().getShortText().replace("&dagger;", ""));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                batsmanCard.setBatted(rosterLineScore.isBatting());
                                batsmanCard.setPosition(rosterLineScore.getOrder());
                                battingCard.getBatsmanCardSet().add(batsmanCard);
                            }else{
                                BowlingCard bowlingCard;
                                if(null == inningsScoreCard.getBowlingCard()) {
                                    bowlingCard = new BowlingCard();
                                    bowlingCard.setBowlerCardSet(new TreeSet<>());
                                    inningsScoreCard.setBowlingCard(bowlingCard);
                                }else{
                                    bowlingCard = inningsScoreCard.getBowlingCard();
                                }
                                BowlerCard bowlerCard = new BowlerCard();
                                bowlerCard.setPlayerId(playerRoster.getPlayerId());
                                bowlerCard.setPlayerName(playerNameService.getPlayerName(playerRoster.getPlayerId()));
                                try {
                                    stats.getSplits().getCategories().get(0).getStats().stream().forEach(stat -> {
                                        switch (stat.getName()){
                                            case "overs":
                                                bowlerCard.setOvers(stat.getDisplayValue());
                                                break;

                                            case "bowled":
                                                bowlerCard.setBowled(stat.getDisplayValue().equals("1")?true:false);
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

                                            case "wickets":
                                                bowlerCard.setWickets(stat.getDisplayValue());
                                                break;
                                        }
                                    });

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                bowlerCard.setLive(playerRoster.getActiveName());
                                bowlerCard.setPosition(rosterLineScore.getOrder());
                                if(bowlerCard.isBowled())
                                    bowlingCard.getBowlerCardSet().add(bowlerCard);
                            }
                        });
                    }

                });
            }

            CompetitorLineScores competitorLineScores = restTemplate.getForObject(competitor.get$ref()+"/linescores/1", CompetitorLineScores.class);
            System.out.println("competitorLineScores: "+ competitorLineScores);
            if(null != competitorLineScores && null != competitorLineScores.getItems()){
                competitorLineScores.getItems().stream().forEach(competitorLineScore -> {
                    if(competitorLineScore.isBatting()){
                        LineScoreStatistics competitorLineScoreStats = restTemplate.getForObject(competitorLineScore.getStatistics().get$ref(), LineScoreStatistics.class);
                        System.out.println("competitorLineScoreStats: "+ competitorLineScoreStats);
                        if(null != competitorLineScoreStats && null != competitorLineScoreStats.getSplits() && null != competitorLineScoreStats.getSplits().getCategories() && competitorLineScoreStats.getSplits().getCategories().size() > 0){
                            Category BattingStatsCategory =  competitorLineScoreStats.getSplits().getCategories().get(0);
                            if(null != BattingStatsCategory && null != BattingStatsCategory.getStats()){
                                InningsInfo inningsInfo = new InningsInfo();
                                BattingStatsCategory.getStats().forEach(stat -> {
                                    switch (stat.getName()){
                                        case "runs":
                                            inningsInfo.setRuns(stat.getDisplayValue());
                                            break;
                                        case "target":
                                            inningsInfo.setTarget(stat.getDisplayValue());
                                            break;
                                        case "wickets":
                                            inningsInfo.setWickets(stat.getDisplayValue());
                                            break;
                                        case "overLimit":
                                            inningsInfo.setOverLimit(stat.getDisplayValue());
                                            break;
                                        case "runRate":
                                            inningsInfo.setRunRate(stat.getDisplayValue());
                                            break;
                                        case "legbyes":
                                            inningsInfo.setLegByes(stat.getDisplayValue());
                                            break;
                                        case "byes":
                                            inningsInfo.setByes(stat.getDisplayValue());
                                            break;
                                        case "wides":
                                            inningsInfo.setWides(stat.getDisplayValue());
                                            break;
                                        case "noballs":
                                            inningsInfo.setNoBalls(stat.getDisplayValue());
                                            break;
                                        case "lead":
                                            inningsInfo.setLead(stat.getDisplayValue());
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
        return scoreCard;
    }

    public void refreshLiveEventScoreCards() {
        liveEvents.keySet().forEach(eventId -> eventsScoreCardCache.put(eventId, getEventScoreCard(eventId)));
        logger.info("eventsScoreCardCache:{}", eventsScoreCardCache);
    }
}
