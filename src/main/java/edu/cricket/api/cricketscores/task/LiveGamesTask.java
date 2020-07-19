package edu.cricket.api.cricketscores.task;

import com.cricketfoursix.cricketdomain.aggregate.GameAggregate;
import com.cricketfoursix.cricketdomain.common.game.*;
import com.cricketfoursix.cricketdomain.repository.GameRepository;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LiveGamesTask {

    private static final Logger log = LoggerFactory.getLogger(LiveGamesTask.class);

    @Autowired
    GameRepository gameRepository;




    RestTemplate restTemplate = new RestTemplate();


    @Autowired
    PlayerNameService playerNameService;

    @Autowired
    PreGamesTask preGamesTask;


    @Autowired
    TeamNameService teamNameService;

    @Autowired
    Map<Long, Boolean> liveEvents;

    @Autowired
    Map<Long, GameAggregate> liveGamesCache;


    public void refreshLiveEvents() {
        liveEvents.keySet().forEach(gameId ->{
            GameAggregate gameAggregate = liveGamesCache.get(gameId);
            if(null == gameAggregate){
                Optional<GameAggregate> gameAggregateOptional= gameRepository.findById(gameId);
                if(gameAggregateOptional.isPresent()){
                    gameAggregate = gameAggregateOptional.get();
                }else{
                    gameAggregate = new GameAggregate();
                    gameAggregate.setId(gameId);
                    gameAggregate.setGameInfo(new GameInfo());
                }
                populateGameAggregate(gameAggregate);
            }
        });
    }



    public GameAggregate populateGameAggregate(GameAggregate gameAggregate) {
        try {
            EventDetail event = restTemplate.getForObject("http://new.core.espnuk.org/v2/sports/cricket/events/" + (gameAggregate.getId()/13) , EventDetail.class);

            populateMatchNote(gameAggregate, event);
            populateGameStatus(gameAggregate);
            return gameRepository.save(gameAggregate);
        }catch (Exception e){
            preGamesTask.populatePreGameAggregate(gameAggregate);
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


    private void populateAwards(GameAggregate gameAggregate, MatchStatus eventStatus) {
        if(gameAggregate.getGameInfo().getGameStatus().equals(GameStatus.post)) {
            if (null != eventStatus.getFeaturedAthletes()) {
                eventStatus.getFeaturedAthletes().forEach(featuredAthletes -> {
                    Award award = new Award();
                    award.setPlayerId(Long.valueOf(featuredAthletes.getPlayerId()) * 13);
                    award.setTeamId(Long.valueOf(featuredAthletes.getTeam().get$ref().split("teams/")[1]) * 13);
                    if (null == gameAggregate.getGameInfo().getGameSummary())
                        gameAggregate.getGameInfo().setGameSummary(new GameSummary());
                    if ("Player Of The Match".equalsIgnoreCase(featuredAthletes.getDisplayName())) {
                        gameAggregate.getGameInfo().getGameSummary().setPlayerOfMatch(award);
                    } else if ("Player Of The Series".equalsIgnoreCase(featuredAthletes.getDisplayName())) {
                        gameAggregate.getGameInfo().getGameSummary().setPlayerOfSeries(award);
                    }
                });
            }
        }
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








    /*private void updateEventScoreCard(EventAggregate eventAggregate) {
        if(null != eventAggregate.getScoreCard()){
            setEventScoreCard(eventAggregate.getScoreCard());
        }else{
            eventAggregate.setScoreCard(getEventScoreCard(eventAggregate.getId()));
        }
    }*/


   /* public ScoreCard setEventScoreCard(ScoreCard scoreCard) {
        try {

            Long eventIdLong = Long.parseLong(scoreCard.getEventId()) / 13;
            scoreCard.setInningsScores(new HashMap<>());

            scoreCard.getCompetitors().forEach(competitorId -> {
                String competitorRef = "http://core.espnuk.org/v2/sports/cricket/events/"+eventIdLong+"/competitions/"+eventIdLong+"/competitors/"+competitorId;
                Competitor competitor = restTemplate.getForObject(competitorRef, Competitor.class);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scoreCard;

    }


    public ScoreCard getEventScoreCard(String eventId) {
        ScoreCard scoreCard = new ScoreCard();
        scoreCard.setEventId(eventId);
        try {


            Long eventIdLong = Long.parseLong(eventId) / 13;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scoreCard;

    }


    public void updateEventScore(Event event) {
        try {
            if(null != event) {

                String eventId = event.getEventId();
                long sourceEventId = Long.valueOf(eventId) / 13;
                String eventRef = "http://core.espnuk.org/v2/sports/cricket/events/" + sourceEventId;

                EventDetail eventDetail = restTemplate.getForObject(eventRef, EventDetail.class);
                Competition competition = eventDetail.getCompetitions().get(0);
                int intClassId = competition.getCompetitionClass().getInternationalClassId();
                if (intClassId > 10) {
                    liveEvents.remove(eventId);
                    return;
                }
                event.setInternationalClassId(intClassId);
                int genClassId = competition.getCompetitionClass().getGeneralClassId();
                if (intClassId == 0 && genClassId > 10) {
                    liveEvents.remove(eventId);
                    return;
                }
                event.setGeneralClassId(genClassId);
                event.setEventId(String.valueOf(Integer.parseInt(competition.getId()) * 13));
                event.setStartDate(DateUtils.getDateFromString(eventDetail.getDate()));
                event.setEndDate(DateUtils.getDateFromString(eventDetail.getEndDate()));

                if(null == event.getVenue())
                    event.setVenue(getEventVenue(eventDetail.getVenues().get(0).get$ref()));

                event.setNote(competition.getNote());
                setSeason(eventDetail, event);
                event.setType(competition.getCompetitionClass().getEventType());


                MatchStatus matchStatus = restTemplate.getForObject(competition.getStatus().get$ref(), MatchStatus.class);
                if (null != matchStatus) {
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
                    if(null != event.getTeam1()) {
                        event.getTeam1().setScore(getEventScore(competitorList.get(0).getScore().get$ref()));
                    }else {
                        edu.cricket.api.cricketscores.rest.response.model.Competitor team1 = new edu.cricket.api.cricketscores.rest.response.model.Competitor();
                        team1.setTeamName(getEventTeam(competitorList.get(0).getTeam().get$ref()));
                        team1.setScore(getEventScore(competitorList.get(0).getScore().get$ref()));
                        event.setTeam1(team1);
                    }

                    if(null != event.getTeam2()) {
                        event.getTeam2().setScore(getEventScore(competitorList.get(1).getScore().get$ref()));
                    }else {
                        edu.cricket.api.cricketscores.rest.response.model.Competitor team2 = new edu.cricket.api.cricketscores.rest.response.model.Competitor();
                        team2.setTeamName(getEventTeam(competitorList.get(1).getTeam().get$ref()));
                        team2.setScore(getEventScore(competitorList.get(1).getScore().get$ref()));
                        event.setTeam2(team2);
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }*/

   /* public String getEventVenue(String $ref){
        Venue venue = restTemplate.getForObject($ref , Venue.class);
        return venue.getFullName();
    }

    public String getEventTeam(String $ref){
        Team team = restTemplate.getForObject($ref , Team.class);
        return teamNameService.getTeamName(team.getId());
    }

    private void setSeason(EventDetail eventDetail, Event event) {
        if(event.getLeagueId() == 0) {
            Season season = restTemplate.getForObject(eventDetail.getSeason().get$ref(), Season.class);
            event.setLeagueId(season.getId() * 13);
            event.setLeagueName(season.getName());
            event.setLeagueYear(String.valueOf(season.getYear()));
        }
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
    }*/
}
