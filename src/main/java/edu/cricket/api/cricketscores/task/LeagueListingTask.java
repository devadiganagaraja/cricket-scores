package edu.cricket.api.cricketscores.task;

import com.cricketfoursix.cricketdomain.aggregate.GameAggregate;
import com.cricketfoursix.cricketdomain.aggregate.LeagueAggregate;
import com.cricketfoursix.cricketdomain.common.game.*;
import com.cricketfoursix.cricketdomain.common.league.LeagueInfo;
import com.cricketfoursix.cricketdomain.common.league.LeagueSeason;
import com.cricketfoursix.cricketdomain.common.league.LeagueTeam;
import com.cricketfoursix.cricketdomain.common.stats.BattingLeader;
import com.cricketfoursix.cricketdomain.common.stats.BowlingLeader;
import com.cricketfoursix.cricketdomain.repository.GameRepository;
import com.cricketfoursix.cricketdomain.repository.LeagueRepository;
import edu.cricket.api.cricketscores.rest.service.TeamNameService;
import edu.cricket.api.cricketscores.rest.source.model.EventListing;
import edu.cricket.api.cricketscores.utils.CommonUtils;
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
    Map<Long, GameAggregate> liveEventsCache;


    @Autowired
    GameRepository gameRepository;

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private EventsListingTask eventsListingTask;


    @Autowired
    TeamNameService teamNameService;




    public void updateLeagueForEvent(GameAggregate gameAggregate) {
        if(null != gameAggregate) {
            GameInfo gameInfo = new GameInfo();
            long leagueId = gameInfo.getLeagueId();
            LeagueAggregate leagueAggregate;
            Optional<LeagueAggregate> leagueAggregateOptional = leagueRepository.findById(gameInfo.getGameId());
            if (leagueAggregateOptional.isPresent()) {
                leagueAggregate = leagueAggregateOptional.get();
                if (leagueAggregate.getLeagueInfo().getLeagueSeasonMap().containsKey(gameInfo.getSeason())) {
                    LeagueSeason leagueSeason = leagueAggregate.getLeagueInfo().getLeagueSeasonMap().get(gameInfo.getSeason());
                    leagueSeason.getEventSet().add(gameInfo);
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
                leagueInfo.setLeagueName(gameInfo.getLeagueName());
                leagueAggregate.setLeagueInfo(leagueInfo);
                setNewLeagueSeason(gameAggregate, leagueAggregate);
            }
            leagueRepository.save(leagueAggregate);
        }
    }

    private void setNewLeagueSeason(GameAggregate gameAggregate, LeagueAggregate leagueAggregate) {
        GameInfo gameInfo = gameAggregate.getGameInfo();
        LeagueSeason leagueSeason = new LeagueSeason();
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

    private void populateLeagueTeam(LeagueSeason leagueSeason, GameAggregate gameAggregate) {
        if(null == leagueSeason.getTeams()) leagueSeason.setTeams(new HashSet<>());
        LeagueTeam team;
        Competitor competitor1 = gameAggregate.getCompetitor1();
        Competitor competitor2 = gameAggregate.getCompetitor1();
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


    public void refreshEventsAndLeagues() {

        liveEventsCache.values().forEach(gameAggregate -> {
            GameInfo gameInfo = gameAggregate.getGameInfo();
            EventListing eventListing = restTemplate.getForObject("http://core.espnuk.org/v2/sports/cricket/leagues/"+(gameInfo.getLeagueId()/13)+"/events?eventsrange=100&limit=100", EventListing.class);
            if(null != eventListing){
                eventListing.getItems().stream().map(ref -> ref.get$ref()).forEach($ref -> {
                    try {

                        String sourceEventId = $ref.split("events/")[1];
                        Long gameId = Long.valueOf(sourceEventId) * 13;


                        Optional<GameAggregate> gameAggregateOptional = gameRepository.findById(gameId);
                        if (!gameAggregateOptional.isPresent()) {
                            updateLeagueForEvent(gameAggregateOptional.get());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                });
            }


        });

    }


/*
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
    }*/

  /*  public void populateLeagueSeasonInfo( LeagueSeason leagueSeason, Season season){
        leagueSeason.setName(season.getName());
        leagueSeason.setStartDate(DateUtils.getDateFromString(season.getStartDate()));
        leagueSeason.setLeagueStartDate(season.getStartDate());
        leagueSeason.setEndDate(DateUtils.getDateFromString(season.getEndDate()));
        leagueSeason.setLeagueEndDate(season.getEndDate());
    }*/



}
