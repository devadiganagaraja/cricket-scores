package edu.cricket.api.cricketscores.task;

import edu.cricket.api.cricketscores.async.RefreshLeagueListingTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class LeagueListingTask {
    private static final Logger log = LoggerFactory.getLogger(LeagueListingTask.class);



    @Autowired
    TaskExecutor taskExecutor;




    @Autowired
    RefreshLeagueListingTask refreshLeagueListingTask;

    public void refreshEventsAndLeaguesAsync() {
        taskExecutor.execute(refreshLeagueListingTask);
    }


    public void refreshEventsAndLeagues() {
        refreshEventsAndLeaguesAsync();

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
                Optional<gate> gateOptional = leagueRepository.findById(leagueId);
                gate gate;
                if (gateOptional.isPresent()) {
                    gate = gateOptional.get();

                    if (null != gate.getLeagueInfo()) {
                        Map<String, LeagueSeason> leagueSeasonMap = gate.getLeagueInfo().getLeagueSeasonMap();
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

                    gate = new gate();
                    gate.setId(leagueId);
                    LeagueInfo leagueInfo = new LeagueInfo();
                    Map<String, LeagueSeason> leagueSeasonMap = new HashMap<>();
                    LeagueSeason leagueSeason = new LeagueSeason();
                    leagueSeason.setLeagueYear(seasonYear);
                    populateLeagueSeasonInfo(leagueSeason, season);
                    leagueSeasonMap.put(seasonYear, leagueSeason);
                    leagueInfo.setLeagueSeasonMap(leagueSeasonMap);
                    gate.setLeagueInfo(leagueInfo);
                }
                leagueRepository.save(gate);
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
