package edu.cricket.api.cricketscores.task;

import edu.cricket.api.cricketscores.domain.BattingLeader;
import edu.cricket.api.cricketscores.domain.BowlingLeader;
import edu.cricket.api.cricketscores.domain.EventAggregate;
import edu.cricket.api.cricketscores.domain.EventPlayerPointsAggregate;
import edu.cricket.api.cricketscores.repository.EventPlayerPointsRepository;
import edu.cricket.api.cricketscores.rest.response.model.*;
import edu.cricket.api.cricketscores.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EventPlayerPointsTask {

    private static final Logger logger = LoggerFactory.getLogger(EventPlayerPointsTask.class);

    @Autowired
    Map<String, EventAggregate> liveEventsCache;


    @Autowired
    EventPlayerPointsRepository eventPlayerPointsRepository;


    public void updateEventPlayerPoints() {

        liveEventsCache.values().stream().forEach(eventAggregate -> {
            Event event = eventAggregate.getEventInfo();

            if(!"pre".equalsIgnoreCase(event.getState())){

                    EventPlayerPointsAggregate eventPlayerPointsAggregate = new EventPlayerPointsAggregate();
                    eventPlayerPointsAggregate.setId(event.getEventId());
                    Map<Long, PlayerPoints> playerPointsMap = new HashMap<>();
                    ScoreCard scoreCard = eventAggregate.getScoreCard();

                    if(null != scoreCard){
                        scoreCard.getInningsScores().values().stream().forEach(inningsScoreCard -> {
                            BattingCard battingCard = inningsScoreCard.getBattingCard();
                            if(null != battingCard){
                                battingCard.getBatsmanCardSet().forEach(batsmanCard -> {
                                    if(playerPointsMap.containsKey(batsmanCard.getPlayerId())){
                                        PlayerPoints playerPoints = playerPointsMap.get(batsmanCard.getPlayerId());
                                        playerPoints.setRuns(playerPoints.getRuns() + CommonUtils.getIntegerFromString(batsmanCard.getRuns()));
                                        playerPoints.setFours(playerPoints.getFours() + CommonUtils.getIntegerFromString(batsmanCard.getFours()));
                                        playerPoints.setSixes(playerPoints.getSixes() + CommonUtils.getIntegerFromString(batsmanCard.getSixes()));

                                    }else{
                                        PlayerPoints playerPoints = new PlayerPoints();
                                        playerPoints.setRuns(CommonUtils.getIntegerFromString(batsmanCard.getRuns()));
                                        playerPoints.setFours(CommonUtils.getIntegerFromString(batsmanCard.getFours()));
                                        playerPoints.setSixes(CommonUtils.getIntegerFromString(batsmanCard.getSixes()));
                                        playerPointsMap.put(batsmanCard.getPlayerId(), playerPoints);
                                    }
                                });
                            }

                            BowlingCard bowlingCard = inningsScoreCard.getBowlingCard();
                            if(null != bowlingCard){
                                bowlingCard.getBowlerCardSet().forEach(bowlerCard -> {
                                    if(playerPointsMap.containsKey(bowlerCard.getPlayerId())){
                                        PlayerPoints playerPoints = playerPointsMap.get(bowlerCard.getPlayerId());
                                        playerPoints.setWickets(playerPoints.getWickets() + CommonUtils.getIntegerFromString(bowlerCard.getWickets()));
                                        playerPoints.setMaidens(playerPoints.getMaidens() + CommonUtils.getIntegerFromString(bowlerCard.getMaidens()));
                                        playerPoints.setCatches(playerPoints.getCatches() + CommonUtils.getIntegerFromString(bowlerCard.getCaught()));
                                        playerPoints.setStumped(playerPoints.getStumped() + CommonUtils.getIntegerFromString(bowlerCard.getStumped()));


                                    }else{
                                        PlayerPoints playerPoints = new PlayerPoints();
                                        playerPoints.setWickets(CommonUtils.getIntegerFromString(bowlerCard.getWickets()));
                                        playerPoints.setMaidens(CommonUtils.getIntegerFromString(bowlerCard.getMaidens()));
                                        playerPoints.setCatches(CommonUtils.getIntegerFromString(bowlerCard.getCaught()));
                                        playerPoints.setStumped(playerPoints.getSixes() + CommonUtils.getIntegerFromString(bowlerCard.getStumped()));

                                        playerPointsMap.put(bowlerCard.getPlayerId(), playerPoints);
                                    }
                                });
                            }
                        });
                    }
                    eventPlayerPointsAggregate.setPlayerPointsMap(playerPointsMap);
                    if(null != eventPlayerPointsAggregate.getPlayerPointsMap()){
                        eventPlayerPointsAggregate.getPlayerPointsMap().values().forEach(playerPoints -> {
                            playerPoints.setRunsPoints(playerPoints.getRuns()/2);
                            playerPoints.setFoursPoints(playerPoints.getFours()*2);
                            playerPoints.setSixesPoints(playerPoints.getSixes()*3);
                            playerPoints.setWicketsPoints(playerPoints.getWickets()*10);
                            playerPoints.setMaidensPoints(playerPoints.getMaidens()* (event.getType().equalsIgnoreCase("Test")? 1 :5 ));
                            playerPoints.setCatchesPoints(playerPoints.getCatches()*5);
                            playerPoints.setStumpedPoints(playerPoints.getStumped()*5);
                            playerPoints.setPoints(playerPoints.getRunsPoints() + playerPoints.getFoursPoints() + playerPoints.getSixesPoints() + playerPoints.getWicketsPoints() + playerPoints.getMaidensPoints() + playerPoints.getCatchesPoints() + playerPoints.getStumpedPoints());

                        });
                    }
                    eventPlayerPointsRepository.save(eventPlayerPointsAggregate);

            }
        });

    }

}
