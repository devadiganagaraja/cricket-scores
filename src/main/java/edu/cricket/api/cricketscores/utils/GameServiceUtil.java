package edu.cricket.api.cricketscores.utils;

import com.cricketfoursix.cricketdomain.aggregate.GameAggregate;
import com.cricketfoursix.cricketdomain.common.game.GameStatus;
import com.cricketfoursix.cricketdomain.common.game.LiveScoreCard;
import com.cricketfoursix.cricketdomain.repository.GameRepository;
import edu.cricket.api.cricketscores.async.RefreshPreGamesTask;
import edu.cricket.api.cricketscores.rest.service.PlayerNameService;
import edu.cricket.api.cricketscores.rest.service.TeamNameService;
import edu.cricket.api.cricketscores.rest.source.CompetitorLineScore;
import edu.cricket.api.cricketscores.rest.source.CompetitorLineScores;
import edu.cricket.api.cricketscores.rest.source.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class GameServiceUtil {




    private static final Logger log = LoggerFactory.getLogger(GameServiceUtil.class);


    @Autowired
    GameRepository gameRepository;



    RestTemplate restTemplate = new RestTemplate();


    @Autowired
    RefreshPreGamesTask refreshPreGamesTask;


    @Autowired
    TeamNameService teamNameService;



    @Autowired
    PlayerNameService playerNameService;



    public GameAggregate populateGameAggregate(GameAggregate gameAggregate) {
        try {
            EventDetail event = restTemplate.getForObject("http://new.core.espnuk.org/v2/sports/cricket/events/" + (gameAggregate.getId()/13) , EventDetail.class);

            populateMatchNote(gameAggregate, event);
            populateGameStatus(gameAggregate);
            return gameRepository.save(gameAggregate);
        }catch (Exception e){
            refreshPreGamesTask.populatePreGameAggregate(gameAggregate);
        }
        return null;
    }


    private void populateGameStatus(GameAggregate gameAggregate) {
        MatchStatus gameStatus = restTemplate.getForObject(gameAggregate.getGameStatusApiRef(), MatchStatus.class);
        log.info("gameStatus:: {}",gameStatus);

        if (null != gameStatus) {
            populateGameStatusType(gameAggregate, gameStatus);
            populateLineScores(gameAggregate);
            populateRoster(gameAggregate);
        }
    }

    private void populateMatchNote(GameAggregate gameAggregate, EventDetail event){
        gameAggregate.getGameInfo().setNote(event.getCompetitions().get(0).getNote());
    }



    private void populateTeamLineScore(com.cricketfoursix.cricketdomain.common.game.Competitor competitor){
        CompetitorLineScores competitorLineScores = restTemplate.getForObject(competitor.getLineScoreRef(), CompetitorLineScores.class);
        if (null != competitorLineScores && null != competitorLineScores.getItems()) {
            competitorLineScores.getItems().stream().forEach(competitorLineScore -> {
                if (competitorLineScore.isBatting()) {
                    LineScoreStatistics competitorLineScoreStats = restTemplate.getForObject(competitorLineScore.getStatistics().get$ref(), LineScoreStatistics.class);
                    if (null != competitorLineScoreStats && null != competitorLineScoreStats.getSplits() && null != competitorLineScoreStats.getSplits().getCategories() && competitorLineScoreStats.getSplits().getCategories().size() > 0) {
                        Category BattingStatsCategory = competitorLineScoreStats.getSplits().getCategories().get(0);
                        if (null != BattingStatsCategory && null != BattingStatsCategory.getStats()) {
                            com.cricketfoursix.cricketdomain.common.game.InningsInfo inningsInfo = new com.cricketfoursix.cricketdomain.common.game.InningsInfo();
                            inningsInfo.setBattingTeamName(teamNameService.getTeamNameByTeamId(competitor.getId()));
                            inningsInfo.setBattingTeamId(competitor.getId());

                            populateInningsName(competitorLineScore, inningsInfo);

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
                            inningsInfo.setExtras(inningsInfo.getByes()+inningsInfo.getLegByes()+inningsInfo.getWides()+inningsInfo.getNoBalls());
                            if(competitor.getInningsScores().containsKey(competitorLineScore.getPeriod())) {
                                competitor.getInningsScores().get(competitorLineScore.getPeriod()).setInningsInfo(inningsInfo);
                            }else{
                                com.cricketfoursix.cricketdomain.common.game.InningsScoreCard inningsScoreCard = new com.cricketfoursix.cricketdomain.common.game.InningsScoreCard();
                                inningsScoreCard.setInningsInfo(inningsInfo);
                                competitor.getInningsScores().put(competitorLineScore.getPeriod(), inningsScoreCard);
                            }

                        }

                    }

                }
            });
        }


    }

    private void populateInningsName(CompetitorLineScore competitorLineScore, com.cricketfoursix.cricketdomain.common.game.InningsInfo inningsInfo) {
        switch (competitorLineScore.getPeriod()) {

            case 1:
                inningsInfo.setInningsName("1st innings");
                break;


            case 2:
                inningsInfo.setInningsName("2nd innings");
                break;

            case 3:
                inningsInfo.setInningsName("3rd innings");
                break;

            case 4:
                inningsInfo.setInningsName("4th innings");
                break;

            default:
                inningsInfo.setInningsName("Extra innings");
        }
    }

    private void populateRoster(GameAggregate gameAggregate) {
        if(gameAggregate.getGameInfo().getGameStatus().equals(GameStatus.post) || gameAggregate.getGameInfo().getGameStatus().equals(GameStatus.live)){

            LiveScoreCard liveScoreCard = gameAggregate.getLiveScoreCard();
            if(null == liveScoreCard) gameAggregate.setLiveScoreCard(new LiveScoreCard());

            populateCompetitorRoster(gameAggregate.getCompetitor1(),gameAggregate.getLiveScoreCard());
            populateCompetitorRoster(gameAggregate.getCompetitor2(), gameAggregate.getLiveScoreCard());
        }
    }

    private void populateCompetitorRoster(com.cricketfoursix.cricketdomain.common.game.Competitor competitor, LiveScoreCard liveScoreCard) {
        if(null != competitor) {
            Roster roster = restTemplate.getForObject(competitor.getRosterRef(), Roster.class);
            if (null != roster && null != roster.getEntries()) {
                AtomicInteger unknownRosterIndex = new AtomicInteger(101);

                roster.getEntries().forEach(playerRoster -> {
                    log.debug("playerRoster :{}", playerRoster);

                    if("striker".equalsIgnoreCase(playerRoster.getActiveName())){
                        liveScoreCard.setStriker(playerRoster.getPlayerId()*13);
                    }

                    if("non-striker".equalsIgnoreCase(playerRoster.getActiveName())){
                        liveScoreCard.setNonStriker(playerRoster.getPlayerId()*13);
                    }

                    if("current bowler".equalsIgnoreCase(playerRoster.getActiveName())){
                        liveScoreCard.setCurrentBowler(playerRoster.getPlayerId()*13);
                    }

                    if("previous bowler".equalsIgnoreCase(playerRoster.getActiveName())){
                        liveScoreCard.setPreviousBowler(playerRoster.getPlayerId()*13);
                    }


                    RosterLineScores rosterLineScores = restTemplate.getForObject(playerRoster.getLinescores().get$ref(), RosterLineScores.class);

                    if (null != rosterLineScores.getItems() && rosterLineScores.getItems().size() > 0) {
                        log.debug("playerRoster1 :{}", playerRoster);
                        rosterLineScores.getItems().forEach(rosterLineScore -> {
                            log.debug("playerRoster2 :{}, linescore {}", playerRoster, rosterLineScore);


                            LineScoreStatistics stats = restTemplate.getForObject(rosterLineScore.getStatistics(), LineScoreStatistics.class);

                            com.cricketfoursix.cricketdomain.common.game.InningsScoreCard inningsScoreCard;
                            if (competitor.getInningsScores().containsKey(rosterLineScore.getPeriod())) {
                                inningsScoreCard = competitor.getInningsScores().get(rosterLineScore.getPeriod());
                            } else {
                                inningsScoreCard = new com.cricketfoursix.cricketdomain.common.game.InningsScoreCard();

                                competitor.getInningsScores().put(rosterLineScore.getPeriod(), inningsScoreCard);
                            }
                            if (rosterLineScore.isBatting()) {
                                log.debug("playerRoster3 :{}, linescore {}", playerRoster, rosterLineScore);

                                com.cricketfoursix.cricketdomain.common.game.BattingCard battingCard;
                                if (null == inningsScoreCard.getBattingCard()) {
                                    battingCard = new com.cricketfoursix.cricketdomain.common.game.BattingCard();
                                    battingCard.setBatsmanCardSet(new TreeSet<>());
                                    inningsScoreCard.setBattingCard(battingCard);
                                } else {
                                    battingCard = inningsScoreCard.getBattingCard();
                                }
                                com.cricketfoursix.cricketdomain.common.game.BatsmanCard batsmanCard = new com.cricketfoursix.cricketdomain.common.game.BatsmanCard();
                                batsmanCard.setPlayerId(playerRoster.getPlayerId() * 13);
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
                                batsmanCard.setPosition(rosterLineScore.getOrder() > 0 && batsmanCard.isBatted() ? rosterLineScore.getOrder() : unknownRosterIndex.incrementAndGet());
                                log.debug("playerRoster4 :{}, linescore {}, batsmanCard {}", playerRoster, rosterLineScore, batsmanCard);

                                battingCard.getBatsmanCardSet().add(batsmanCard);
                            } else {
                                com.cricketfoursix.cricketdomain.common.game.BowlingCard bowlingCard;
                                if (null == inningsScoreCard.getBowlingCard()) {
                                    bowlingCard = new com.cricketfoursix.cricketdomain.common.game.BowlingCard();
                                    bowlingCard.setBowlerCardSet(new TreeSet<>());
                                    inningsScoreCard.setBowlingCard(bowlingCard);
                                } else {
                                    bowlingCard = inningsScoreCard.getBowlingCard();
                                }
                                com.cricketfoursix.cricketdomain.common.game.BowlerCard bowlerCard = new com.cricketfoursix.cricketdomain.common.game.BowlerCard();
                                bowlerCard.setPlayerId(playerRoster.getPlayerId() * 13);
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
        }
    }

    private void populateLineScores(GameAggregate gameAggregate) {


        populateTeamLineScore(gameAggregate.getCompetitor1());
        populateTeamLineScore(gameAggregate.getCompetitor2());
    }



    private void populateGameStatusType(GameAggregate gameAggregate, MatchStatus matchStatus) {

        if(null !=matchStatus.getType()){
            Type eventStatusType = matchStatus.getType();
            log.info("eventStatusType:: {}", eventStatusType);
            GameStatus gameStatus = GameStatus.cancled;
            if("post".equalsIgnoreCase(eventStatusType.getState())){
                gameStatus = GameStatus.post;
            }else if("pre".equalsIgnoreCase(eventStatusType.getState())){
                gameStatus = GameStatus.pre;
            }else if("in".equalsIgnoreCase(eventStatusType.getState())){
                gameStatus = GameStatus.live;
            }else if("scheduled".equalsIgnoreCase(eventStatusType.getState())){
                gameStatus = GameStatus.future;
            }

            gameAggregate.getGameInfo().setGameStatus(gameStatus);
        }
    }

}
