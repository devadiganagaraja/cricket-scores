package edu.cricket.api.cricketscores.utils;

import com.cricketfoursix.cricketdomain.aggregate.GameAggregate;
import com.cricketfoursix.cricketdomain.aggregate.LeagueAggregate;
import com.cricketfoursix.cricketdomain.common.game.*;
import com.cricketfoursix.cricketdomain.common.game.Competitor;
import com.cricketfoursix.cricketdomain.common.league.LeagueInfo;
import com.cricketfoursix.cricketdomain.common.league.LeagueSeason;
import com.cricketfoursix.cricketdomain.common.league.LeagueTeam;
import com.cricketfoursix.cricketdomain.common.stats.BattingLeader;
import com.cricketfoursix.cricketdomain.common.stats.BowlingLeader;
import com.cricketfoursix.cricketdomain.repository.GameRepository;
import com.cricketfoursix.cricketdomain.repository.LeagueRepository;
import edu.cricket.api.cricketscores.async.RefreshPostGamesTask;
import edu.cricket.api.cricketscores.async.RefreshPreGamesTask;
import edu.cricket.api.cricketscores.rest.service.PlayerNameService;
import edu.cricket.api.cricketscores.rest.service.TeamNameService;
import edu.cricket.api.cricketscores.rest.source.CompetitorLineScore;
import edu.cricket.api.cricketscores.rest.source.CompetitorLineScores;
import edu.cricket.api.cricketscores.rest.source.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class GameServiceUtil {




    private static final Logger log = LoggerFactory.getLogger(GameServiceUtil.class);


    @Autowired
    GameRepository gameRepository;



    RestTemplate restTemplate = new RestTemplate();


    @Autowired
    TeamNameService teamNameService;



    @Autowired
    PlayerNameService playerNameService;


    @Autowired
    LeagueRepository leagueRepository;



    public GameAggregate populateGameAggregate(GameAggregate gameAggregate, RefreshPreGamesTask refreshPreGamesTask) {
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

    public void populateRoster(GameAggregate gameAggregate) {
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

    public void populateLineScores(GameAggregate gameAggregate) {

        GameInfo gameInfo = gameAggregate.getGameInfo();

        populateTeamLineScore(gameAggregate.getCompetitor1());
        populateTeamLineScore(gameAggregate.getCompetitor2());


        Competitor competitor1 = gameAggregate.getCompetitor1();
        if(null != competitor1) {
            gameInfo.setTeam1Name(teamNameService.getTeamNameByTeamId(competitor1.getId()));

            if(null != competitor1.getInningsScores() && competitor1.getInningsScores().size() > 0 ) {
                gameInfo.setTeam1Score("");
                competitor1.getInningsScores().values().stream().filter(inningsScoreCard -> inningsScoreCard.getInningsInfo() != null)

                        .forEach(inningsScoreCard -> {
                            if (inningsScoreCard.getInningsInfo() != null) {
                                InningsInfo inningsInfo = inningsScoreCard.getInningsInfo();
                                gameInfo.setTeam1Score((gameInfo.getTeam1Score()+ " "+formatScore(inningsInfo) +" ("+ inningsInfo.getOvers() +")").trim());
                            }


                        });
            }

        }

        Competitor competitor2 = gameAggregate.getCompetitor2();
        if(null != competitor2) {
            gameAggregate.getGameInfo().setTeam2Name(teamNameService.getTeamNameByTeamId(competitor2.getId()));

            if(null != competitor2.getInningsScores() && competitor2.getInningsScores().size() > 0 ) {
                gameInfo.setTeam2Score("");

                competitor2.getInningsScores().values().stream().filter(inningsScoreCard -> inningsScoreCard.getInningsInfo() != null).forEach( inningsScoreCard -> {
                    InningsInfo inningsInfo = inningsScoreCard.getInningsInfo();
                    if (inningsScoreCard.getInningsInfo() != null) {
                        gameInfo.setTeam2Score((gameInfo.getTeam2Score()+" " +formatScore(inningsInfo) +"("+ inningsInfo.getOvers() +")").trim());
                    }


                });
            }
        }
    }


    private String formatScore(InningsInfo score) {
        StringBuilder scoreStr = new StringBuilder();
        scoreStr.append(score.getRuns());
        if(score.getWickets() != 10){
            scoreStr.append("/").append(score.getWickets());
        }
        return scoreStr.toString();
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



    public void updateLeagueForEvent(GameAggregate gameAggregate) {
        log.info("updateLeagueForEventupdateLeagueForEventupdateLeagueForEventupdateLeagueForEvent");
        if(null != gameAggregate) {
            GameInfo gameInfo = gameAggregate.getGameInfo();
            long leagueId = gameInfo.getLeagueId();
            LeagueAggregate leagueAggregate;
            Optional<LeagueAggregate> leagueAggregateOptional = leagueRepository.findById(gameInfo.getLeagueId());
            if (leagueAggregateOptional.isPresent()) {
                leagueAggregate = leagueAggregateOptional.get();
                if (leagueAggregate.getLeagueInfo().getLeagueSeasonMap().containsKey(gameInfo.getSeason())) {
                    LeagueSeason leagueSeason = leagueAggregate.getLeagueInfo().getLeagueSeasonMap().get(gameInfo.getSeason());
                    populateSeason(gameInfo.getLeagueId()/13, gameInfo.getSeason(), leagueSeason);
                    leagueSeason.getEventSet().add(gameInfo);
                    log.info(" leagueSeason.getEventSet(). size:: "+ leagueSeason.getEventSet().size());
                    updateLastEvent(gameInfo, leagueSeason);
                    updateNextEvent(gameInfo, leagueSeason);
                    updateLiveEvent(gameInfo, leagueSeason);
                    populateLeaders(leagueSeason);
                    populateLeagueTeam(leagueSeason, gameAggregate);
                } else {
                    setNewLeagueSeason(gameAggregate, leagueAggregate);
                }
            }else{
                leagueAggregate = new LeagueAggregate();
                leagueAggregate.setId(leagueId);
                LeagueInfo leagueInfo = new LeagueInfo();
                String ref = "http://new.core.espnuk.org/v2/sports/cricket/leagues/"+(leagueId/13);
                log.info("refrefref=> "+ref);
                League league = restTemplate.getForObject(ref, League.class);
                if (null != league) {
                    leagueInfo.setLeagueName(league.getName());
                    leagueInfo.setLeagueId(leagueId);
                    log.info("league.isTournament()league.isTournament()league.isTournament()==>"+league.isTournament());
                    leagueInfo.setTournament(league.isTournament());
                    leagueInfo.setAbbreviation(league.getShortName());
                }
                leagueAggregate.setLeagueInfo(leagueInfo);
                setNewLeagueSeason(gameAggregate, leagueAggregate);
            }
            leagueRepository.save(leagueAggregate);
        }
    }


    public void updateLeagueEvents(Long  leagueId, RefreshPreGamesTask refreshPreGamesTask, RefreshPostGamesTask refreshPostGamesTask, boolean refreshGames) {
        Optional<LeagueAggregate> leagueAggregateOptional = leagueRepository.findById(leagueId);
        LeagueAggregate leagueAggregate;
        if (! leagueAggregateOptional.isPresent()) {
            leagueAggregate = new LeagueAggregate();
            leagueAggregate.setId(leagueId);
        }else {
            leagueAggregate = leagueAggregateOptional.get();
            LeagueInfo leagueInfo = new LeagueInfo();
            String ref = "http://new.core.espnuk.org/v2/sports/cricket/leagues/"+(leagueId/13);
            log.info("refrefref=> "+ref);
            League league = restTemplate.getForObject(ref, League.class);
            if (null != league) {
                leagueInfo.setLeagueName(league.getName());
                leagueInfo.setLeagueId(leagueId);
                log.info("league.isTournament()league.isTournament()league.isTournament()==>"+league.isTournament());
                leagueInfo.setTournament(league.isTournament());
                leagueInfo.setAbbreviation(league.getShortName());
            }
            leagueAggregate.setLeagueInfo(leagueInfo);
            if(StringUtils.isNotEmpty(league.getSeason().get$ref())) {
                String seasonRef = league.getSeason().get$ref();

                Season season = restTemplate.getForObject(seasonRef, Season.class);
                Map<Integer, LeagueSeason> leagueSeasonMap = leagueAggregate.getLeagueInfo().getLeagueSeasonMap();
                if(null == leagueSeasonMap){
                    leagueSeasonMap = new HashMap<>();
                }
                if(!leagueSeasonMap.containsKey(season.getYear())){
                    LeagueSeason leagueSeason = new LeagueSeason();
                    populateSeason(leagueId/13, season.getYear(), leagueSeason);
                    leagueSeasonMap.put(season.getYear(), leagueSeason);
                }

                EventListing eventListing = restTemplate.getForObject("http://core.espnuk.org/v2/sports/cricket/leagues/"+(leagueId/13)+"/seasons/"+season.getYear()+"/events", EventListing.class);


                if(null != eventListing){
                    eventListing.getItems().forEach(eventRef ->{
                        try {
                            log.info("eventRef::"+eventRef);

                            String sourceEventId = eventRef.get$ref().split("events/")[1];
                            Long gameId = Long.valueOf(sourceEventId) * 13;
                            Optional<GameAggregate> gameAggregateOptional = gameRepository.findById(gameId);
                            GameAggregate gameAggregate = null;
                            if (refreshGames || !gameAggregateOptional.isPresent()) {
                                gameAggregate = getGameAggregate(sourceEventId, refreshPreGamesTask, refreshPostGamesTask);
                            }else {
                                gameAggregate = gameAggregateOptional.get();
                            }
                            log.info("eventRef::gameAggregate"+gameAggregate);
                            updateLeagueForEvent(leagueAggregate, gameAggregate);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    });
                }



            }


        }
        leagueRepository.save(leagueAggregate);



    }


    public GameAggregate getGameAggregate(String sourceEventId, RefreshPreGamesTask refreshPreGamesTask, RefreshPostGamesTask refreshPostGamesTask ) {
        try {

            GameStatus gameStatus = getGameStatus(sourceEventId);

            Long gameId = Long.valueOf(sourceEventId)*13;
            GameAggregate gameAggregate = new GameAggregate();
            gameAggregate.setGameInfo(new GameInfo());
            gameAggregate.setId(gameId);

            if(GameStatus.pre.equals(gameStatus)){
                return  refreshPreGamesTask.populatePreGameAggregate(gameAggregate);

            }else if(GameStatus.live.equals(gameStatus)){
                return populateGameAggregate(gameAggregate, refreshPreGamesTask);

            }else if(GameStatus.post.equals(gameStatus)){
                return refreshPostGamesTask.populatePostGameAggregate(gameId);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }



    private GameStatus getGameStatus(String sourceEventId) {
        EventStatus eventStatus = restTemplate.getForObject("http://new.core.espnuk.org/v2/sports/cricket/events/"+sourceEventId+"/competitions/"+ sourceEventId+"/status", EventStatus.class);

        GameStatus gameStatus = GameStatus.cancled;
        if(null !=eventStatus.getType()) {
            EventStatusType eventStatusType = eventStatus.getType();
            log.info("eventStatusType:: {}", eventStatusType);

            if ("post".equalsIgnoreCase(eventStatusType.getState())) {
                gameStatus = GameStatus.post;
            } else if ("pre".equalsIgnoreCase(eventStatusType.getState())) {
                gameStatus = GameStatus.pre;
            } else if ("in".equalsIgnoreCase(eventStatusType.getState())) {
                gameStatus = GameStatus.live;
            } else if ("scheduled".equalsIgnoreCase(eventStatusType.getState())) {
                gameStatus = GameStatus.future;
            }
        }
        return gameStatus;
    }






    public void updateLeagueForEvent(LeagueAggregate leagueAggregate, GameAggregate gameAggregate) {
        log.info("updateLeagueForEvent with leagueId");
        if(null != gameAggregate) {
            GameInfo gameInfo = gameAggregate.getGameInfo();
            if (leagueAggregate.getLeagueInfo().getLeagueSeasonMap().containsKey(gameInfo.getSeason())) {
                LeagueSeason leagueSeason = leagueAggregate.getLeagueInfo().getLeagueSeasonMap().get(gameInfo.getSeason());
                populateSeason(gameInfo.getLeagueId()/13, gameInfo.getSeason(), leagueSeason);
                leagueSeason.getEventSet().add(gameInfo);
                updateLastEvent(gameInfo, leagueSeason);
                updateNextEvent(gameInfo, leagueSeason);
                updateLiveEvent(gameInfo, leagueSeason);
                populateLeaders(leagueSeason);
                populateLeagueTeam(leagueSeason, gameAggregate);
            } else {
                setNewLeagueSeason(gameAggregate, leagueAggregate);
            }
        }
    }



    private void setNewLeagueSeason(GameAggregate gameAggregate, LeagueAggregate leagueAggregate) {


        GameInfo gameInfo = gameAggregate.getGameInfo();
        LeagueSeason leagueSeason = new LeagueSeason();

        populateSeason(gameInfo.getLeagueId()/13, gameInfo.getSeason(), leagueSeason);


        leagueSeason.setBattingLeaders(new TreeSet<>());
        leagueSeason.setBowlingLeaders(new TreeSet<>());
        leagueSeason.setEventSet(new HashSet<>());
        updateLastEvent(gameInfo, leagueSeason);
        updateNextEvent(gameInfo, leagueSeason);
        updateLiveEvent(gameInfo, leagueSeason);

        leagueSeason.getEventSet().add(gameInfo);
        populateLeaders(leagueSeason);
        populateLeagueTeam(leagueSeason, gameAggregate);

        leagueAggregate.getLeagueInfo().getLeagueSeasonMap().put(gameInfo.getSeason(), leagueSeason);
    }

    private void populateSeason(long sourceLeagueId, int seasonYear, LeagueSeason leagueSeason) {
        try {
            Season season = restTemplate.getForObject("http://new.core.espnuk.org/v2/sports/cricket/leagues/"+sourceLeagueId+"/seasons/"+seasonYear, Season.class);

            if(null != season){
                leagueSeason.setName(season.getName());
                leagueSeason.setLeagueYear(String.valueOf(season.getYear()));
                leagueSeason.setStartDate(season.getStartDate());
                leagueSeason.setEndDate(season.getEndDate());

            }
        }catch (Exception e){
            log.error("Error while fetching season details source_league: {}, season: {}, massage: {}",sourceLeagueId, seasonYear, e.getMessage());
        }
    }

    private void populateLeagueTeam(LeagueSeason leagueSeason, GameAggregate gameAggregate) {
        if(null == leagueSeason.getTeams()) leagueSeason.setTeams(new HashSet<>());
        LeagueTeam team;
        com.cricketfoursix.cricketdomain.common.game.Competitor competitor1 = gameAggregate.getCompetitor1();
        Competitor competitor2 = gameAggregate.getCompetitor2();
        GameInfo gameInfo = gameAggregate.getGameInfo();
        long team1Id = competitor1.getId();
        Optional<LeagueTeam> team1Optional = leagueSeason.getTeams().stream().filter(leagueTeam -> leagueTeam.getId() == team1Id).findFirst();
        if(team1Optional.isPresent()){
            team = team1Optional.get();
            pushResult(gameInfo, team, competitor1, competitor2);
        }else{
            team = new LeagueTeam();
            team.setId(team1Id);
            team.setDisplayName(teamNameService.getTeamNameByTeamId(competitor1.getId()));
            pushResult(gameInfo, team, competitor1, competitor2);
            leagueSeason.getTeams().add(team);
        }


        long team2Id = competitor2.getId();
        Optional<LeagueTeam> team2Optional = leagueSeason.getTeams().stream().filter(leagueTeam -> leagueTeam.getId() == team2Id).findFirst();
        if(team2Optional.isPresent()){
            team = team2Optional.get();
            pushResult(gameInfo, team, competitor2, competitor1);
        }else{
            team = new LeagueTeam();
            team.setId(team2Id);
            team.setDisplayName(teamNameService.getTeamNameByTeamId(competitor2.getId()));
            pushResult(gameInfo, team, competitor2, competitor1);
            leagueSeason.getTeams().add(team);
        }
    }





    private void pushResult(GameInfo gameInfo, LeagueTeam team, Competitor competitor1, Competitor competitor2) {
        if(GameStatus.post.equals(gameInfo.getGameStatus())) {
            if (competitor1.isWinner()) {
                team.getWon().add(gameInfo.getGameId());
            } else if (competitor2.isWinner()) {
                team.getLost().add(gameInfo.getGameId());
            } else {
                team.getDrawn().add(gameInfo.getGameId());
            }
        }
    }

    private void populateLeaders(LeagueSeason leagueSeason) {
        Map<Long, BattingLeader> battingLeaderMap = new HashMap<>();
        Map<Long, BowlingLeader> bowlingLeaderMap = new HashMap<>();
        if(null != leagueSeason.getEventSet()){


            leagueSeason.getEventSet().stream().filter(gameInfo -> ! GameStatus.pre.equals(gameInfo.getGameStatus())).forEach(event -> {
                Optional<GameAggregate> gameAggregateOptional = gameRepository.findById(event.getGameId());
                if(gameAggregateOptional.isPresent()){
                    if(null != gameAggregateOptional.get().getCompetitor1())
                        populateCompetitorLeaders(battingLeaderMap, bowlingLeaderMap, gameAggregateOptional.get().getCompetitor1());
                    if(null != gameAggregateOptional.get().getCompetitor2())
                        populateCompetitorLeaders(battingLeaderMap, bowlingLeaderMap, gameAggregateOptional.get().getCompetitor2());
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

    private void populateCompetitorLeaders(Map<Long, BattingLeader> battingLeaderMap, Map<Long, BowlingLeader> bowlingLeaderMap, Competitor competitor) {
        if(null != competitor){
            competitor.getInningsScores().values().forEach(inningsScoreCard -> {
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
    }

    private void updateLastEvent(GameInfo gameInfo, LeagueSeason leagueSeason) {
        if(GameStatus.post.equals(gameInfo.getGameStatus())) {
            log.info("update post gameInfo : {}",gameInfo);
            leagueSeason.getPostGames().add(gameInfo);
            leagueSeason.getLiveGames().remove(gameInfo);
            leagueSeason.getNextGames().remove(gameInfo);
        }
    }

    private void updateNextEvent(GameInfo gameInfo, LeagueSeason leagueSeason) {
        if(GameStatus.pre.equals(gameInfo.getGameStatus())) {
            log.info("update pre gameInfo : {}",gameInfo);

            leagueSeason.getPostGames().remove(gameInfo);
            leagueSeason.getLiveGames().remove(gameInfo);
            leagueSeason.getNextGames().add(gameInfo);
        }
    }

    private void updateLiveEvent(GameInfo gameInfo, LeagueSeason leagueSeason) {
        leagueSeason.getLiveGames().remove(gameInfo);
        if (GameStatus.live.equals(gameInfo.getGameStatus())) {
            log.info("update live gameInfo : {}",gameInfo);

            leagueSeason.getPostGames().remove(gameInfo);
            leagueSeason.getLiveGames().add(gameInfo);
            leagueSeason.getNextGames().remove(gameInfo);
        }
    }

}
