package edu.cricket.api.cricketscores.task;

import com.cricketfoursix.cricketdomain.aggregate.GameAggregate;
import com.cricketfoursix.cricketdomain.aggregate.GamePlayerPointsAggregate;
import com.cricketfoursix.cricketdomain.common.game.BattingCard;
import com.cricketfoursix.cricketdomain.common.game.BowlingCard;
import com.cricketfoursix.cricketdomain.common.game.Competitor;
import com.cricketfoursix.cricketdomain.common.game.GameInfo;
import com.cricketfoursix.cricketdomain.common.game.GameStatus;
import com.cricketfoursix.cricketdomain.common.squad.PlayerPoints;
import com.cricketfoursix.cricketdomain.repository.GamePlayerPointsRepository;
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
    Map<Long, GameAggregate> liveGamesCache;


    @Autowired
    GamePlayerPointsRepository gamePlayerPointsRepository;


    public void updateEventPlayerPoints() {

        liveGamesCache.values().stream().forEach(gameAggregate -> {
            GameInfo gameInfo = gameAggregate.getGameInfo();

            if (!GameStatus.pre.equals(gameInfo.getGameStatus())) {

                GamePlayerPointsAggregate gamePlayerPointsAggregate = new GamePlayerPointsAggregate();
                gamePlayerPointsAggregate.setId(gameAggregate.getId());
                Map<Long, PlayerPoints> playerPointsMap = new HashMap<>();

                populateCompletitorPlayerPoints(gameAggregate.getCompetitor1(), playerPointsMap);
                populateCompletitorPlayerPoints(gameAggregate.getCompetitor2(), playerPointsMap);
                gamePlayerPointsAggregate.setPlayerPointsMap(playerPointsMap);
                if (null != gamePlayerPointsAggregate.getPlayerPointsMap()) {
                    gamePlayerPointsAggregate.getPlayerPointsMap().values().forEach(playerPoints -> {
                        playerPoints.setRunsPoints(playerPoints.getRuns() / 2);
                        playerPoints.setFoursPoints(playerPoints.getFours() * 2);
                        playerPoints.setSixesPoints(playerPoints.getSixes() * 3);
                        playerPoints.setWicketsPoints(playerPoints.getWickets() * 10);
                        playerPoints.setMaidensPoints(playerPoints.getMaidens() * (gameInfo.getGameClass().getShortName().equalsIgnoreCase("Test") ? 1 : 5));
                        playerPoints.setCatchesPoints(playerPoints.getCatches() * 5);
                        playerPoints.setStumpedPoints(playerPoints.getStumped() * 5);
                        playerPoints.setPoints(playerPoints.getRunsPoints() + playerPoints.getFoursPoints() + playerPoints.getSixesPoints() + playerPoints.getWicketsPoints() + playerPoints.getMaidensPoints() + playerPoints.getCatchesPoints() + playerPoints.getStumpedPoints());

                    });
                }
                gamePlayerPointsRepository.save(gamePlayerPointsAggregate);

            }
        });

    }

    private void populateCompletitorPlayerPoints(Competitor competitor, Map<Long, PlayerPoints> playerPointsMap) {
        if(null != competitor){
            competitor.getInningsScores().values().stream().forEach(inningsScoreCard -> {
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
    }

}
