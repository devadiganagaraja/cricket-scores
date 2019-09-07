package edu.cricket.api.cricketscores.task;

import edu.cricket.api.cricketscores.domain.AthleteAggregate;
import edu.cricket.api.cricketscores.repository.AthleteRepository;
import edu.cricket.api.cricketscores.rest.response.model.AthleteInfo;
import edu.cricket.api.cricketscores.rest.response.model.PlayerStats;
import edu.cricket.api.cricketscores.rest.service.PlayerNameService;
import edu.cricket.api.cricketscores.rest.service.TeamNameService;
import edu.cricket.api.cricketscores.rest.source.model.Athlete;
import edu.cricket.api.cricketscores.rest.source.model.AthleteStat;
import edu.cricket.api.cricketscores.rest.source.model.Category;
import edu.cricket.api.cricketscores.rest.source.model.Style;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerDetailTask {

    private static final Logger logger = LoggerFactory.getLogger(PlayerDetailTask.class);

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    TeamNameService teamNameService;

    @Autowired
    PlayerNameService playerNameService;


    @Autowired
    AthleteRepository athleteRepository;




    public AthleteInfo getPlayerDetails(String playerId){
        AthleteInfo athleteInfo = getBasicPlayerDetails(playerId);
        athleteInfo.setPlayerStats(getPlayerStats(playerId));
        return athleteInfo;
    }


    public AthleteInfo getBasicPlayerDetails(String playerId){

        Optional<AthleteAggregate> athleteAggregateOpt =  athleteRepository.findById(playerId);
        if(athleteAggregateOpt.isPresent()){
            return athleteAggregateOpt.get().getAthleteInfo();
        }else {

            AthleteAggregate athleteAggregate = new AthleteAggregate();
            athleteAggregate.setAthleteId(playerId);
            long sourcePlayerId = Long.parseLong(playerId) / 13;
            String ref = "http://core.espnuk.org/v2/sports/cricket/athletes/" + sourcePlayerId;
            Athlete athlete = restTemplate.getForObject(ref, Athlete.class);
            AthleteInfo athleteInfo = new AthleteInfo();
            if(null != athlete.getPosition()) {
                athleteInfo.setPlayerType(getBattingStyle(athlete.getPosition().getName()));
            }
            athleteInfo.setAthleteName(playerNameService.getPlayerName(sourcePlayerId));
            athleteInfo.setAge(athlete.getAge());

            athleteInfo.setBattingStyle(getBattingStyle(athlete.getStyles()));
            athleteInfo.setBowlingStyle(getBowlingStyle(athlete.getStyles()));

            athleteInfo.setCountry(teamNameService.getTeamName(athlete.getCountry()));
            athleteAggregate.setAthleteInfo(athleteInfo);
            athleteRepository.save(athleteAggregate);
            return athleteInfo;
        }
    }


    public List<PlayerStats> getPlayerStats(String playerId){

        List<PlayerStats> playerStats = new ArrayList<>();
        long sourcePlayerId = Long.parseLong(playerId) / 13;

        String testStatRef  =  "http://core.espnuk.org/v2/sports/cricket/athletes/"+sourcePlayerId+"/statistics?internationalClassId=1";
        String odiStatRef  =  "http://core.espnuk.org/v2/sports/cricket/athletes/"+sourcePlayerId+"/statistics?internationalClassId=2";
        String t20StatRef  =  "http://core.espnuk.org/v2/sports/cricket/athletes/"+sourcePlayerId+"/statistics?internationalClassId=3";

        AthleteStat athleteTestStat = restTemplate.getForObject(testStatRef, AthleteStat.class);
        playerStats.add(getAthleteStats(athleteTestStat, "Test"));

        AthleteStat athleteOdiStat = restTemplate.getForObject(odiStatRef, AthleteStat.class);
        playerStats.add(getAthleteStats(athleteOdiStat, "ODI"));

        AthleteStat athleteT20Stat = restTemplate.getForObject(t20StatRef, AthleteStat.class);
        playerStats.add(getAthleteStats(athleteT20Stat, "T20"));

        return playerStats;

    }

    private PlayerStats getAthleteStats(AthleteStat athleteStat, String format) {
        PlayerStats playerStats = new PlayerStats();
        playerStats.setFormatName(format);
        if(null != athleteStat && null != athleteStat.getSplits() && null != athleteStat.getSplits().getCategories()){
            Category category = athleteStat.getSplits().getCategories().get(0);
            if(null != category.getStats()){
                category.getStats().stream().forEach(stat -> {
                    switch (stat.getName()) {

                        case "matches":
                            playerStats.setMatches(stat.getDisplayValue());
                            break;

                        case "runs":
                            playerStats.setRuns(stat.getDisplayValue());
                            break;

                        case "battingAverage":
                            playerStats.setBattingAverage(stat.getDisplayValue());
                            break;

                        case "highScore":
                            playerStats.setHighScore(stat.getDisplayValue());
                            break;

                        case "battingStrikeRate":
                            playerStats.setBattingStrikeRate(stat.getDisplayValue());
                            break;

                        case "wickets":
                            playerStats.setWickets(stat.getDisplayValue());
                            break;

                        case "bowlingAverage":
                            playerStats.setBowlingAverage(stat.getDisplayValue());
                            break;

                        case "bowlingStrikeRate":
                            playerStats.setBowlingStrikeRate(stat.getDisplayValue());
                            break;

                        case "bestBowlingFigures":
                            playerStats.setBestBowling(stat.getDisplayValue());
                            break;

                        case "economyRate":
                            playerStats.setEconomyRate(stat.getDisplayValue());
                            break;

                    }
                });

            }
        }
        return playerStats;


    }


    private String getBattingStyle(List<Style> styles) {
        StringBuilder battingStyle = new StringBuilder();
        styles.forEach(style -> {
            if("batting".equalsIgnoreCase(style.getType())){
                battingStyle.append(style.getDescription());
                return;
            }

        });
        return battingStyle.toString();
    }

    public static  String getBattingStyle(String type) {
        if(StringUtils.isNotBlank(type)){
            if(type.toLowerCase().contains("batsman")) return "Batsman";
            if(type.toLowerCase().contains("bowler")) return "Bowler";
            if(type.toLowerCase().contains("wicketkeeper")) return "Wicketkeeper";

        }
        return "Allrounder";

    }

    private String getBowlingStyle(List<Style> styles) {
        StringBuilder bowlingStyle = new StringBuilder();
        styles.forEach(style -> {
            if("bowling".equalsIgnoreCase(style.getType())){

                if(style.getDescription().toLowerCase().contains("right")) bowlingStyle.append("Right Arm ");
                else if(style.getDescription().toLowerCase().contains("left")) bowlingStyle.append("Left Arm ");

                if(style.getDescription().toLowerCase().contains("leg")) bowlingStyle.append("Leg Spin ");
                else if(style.getDescription().toLowerCase().contains("off")) bowlingStyle.append("Off Spin ");
                else if(style.getDescription().toLowerCase().contains("fast-medium")) bowlingStyle.append("Fast ");
                else if(style.getDescription().toLowerCase().contains("medium-fast")) bowlingStyle.append("Medium Fast ");
                else if(style.getDescription().toLowerCase().contains("slow")) bowlingStyle.append("Spin");
                else if(style.getDescription().toLowerCase().contains("fast")) bowlingStyle.append("Fast");

                return;
            }
        });
        return bowlingStyle.toString();
    }
}
