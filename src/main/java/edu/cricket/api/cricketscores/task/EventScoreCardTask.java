package edu.cricket.api.cricketscores.task;

import edu.cricket.api.cricketscores.rest.response.model.*;
import edu.cricket.api.cricketscores.rest.source.model.*;
import edu.cricket.api.cricketscores.rest.source.model.Competitor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.TreeSet;

@Service
public class EventScoreCardTask {

    RestTemplate restTemplate = new RestTemplate();

    public ScoreCard getEventScoreCard(String $ref) {
        ScoreCard scoreCard = new ScoreCard();
        scoreCard.setInningsScores(new HashMap<>());


        EventListing eventCompetitorListing = restTemplate.getForObject($ref, EventListing.class);

        eventCompetitorListing.getItems().forEach(competitorRef -> {
            Competitor competitor = restTemplate.getForObject(competitorRef.get$ref(), Competitor.class);
            Roster roster = restTemplate.getForObject(competitor.get$ref()+"/roster", Roster.class);
            if(null != roster && null != roster.getEntries()){
                roster.getEntries().forEach(playerRoster -> {
                    Athlete athlete = restTemplate.getForObject(playerRoster.getAthlete().get$ref(), Athlete.class);

                    RosterLineScores rosterLineScores = restTemplate.getForObject(playerRoster.getLinescores().get$ref(), RosterLineScores.class);

                    if(null != rosterLineScores.getItems()) {
                        rosterLineScores.getItems().forEach(rosterLineScore -> {


                            RosterLineScoreStatistics stats = restTemplate.getForObject(rosterLineScore.getStatistics(), RosterLineScoreStatistics.class);

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
                                batsmanCard.setPlayerName(athlete.getDisplayName());

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
                                bowlerCard.setPlayerName(athlete.getDisplayName());
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


        });
        return scoreCard;
    }
}
