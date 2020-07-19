package edu.cricket.api.cricketscores.task;

import com.cricketfoursix.cricketdomain.aggregate.BBBAggregate;
import com.cricketfoursix.cricketdomain.aggregate.GameAggregate;
import com.cricketfoursix.cricketdomain.common.bbb.InningCommentarySummary;
import com.cricketfoursix.cricketdomain.domain.bbb.*;
import com.cricketfoursix.cricketdomain.repository.BallRepository;
import edu.cricket.api.cricketscores.rest.response.MatchCommentary;
import edu.cricket.api.cricketscores.rest.service.PlayerNameService;
import edu.cricket.api.cricketscores.rest.service.TeamNameService;
import edu.cricket.api.cricketscores.rest.source.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class EventBallsTask {


    private static final Logger logger = LoggerFactory.getLogger(EventBallsTask.class);


    @Autowired
    Map<Long, GameAggregate> liveEventsCache;

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    BallRepository ballRepository;

    @Autowired
    TeamNameService teamNameService;

    @Autowired
    PlayerNameService playerNameService;


    @Autowired
    public Map<Long,MatchCommentary> eventsCommsCache;


    public void refreshLiveEventBalls() {
        liveEventsCache.keySet().forEach(gameId -> {
                String $ref = "http://core.espnuk.org/v2/sports/cricket/leagues/8040/events/"+(gameId/13)+"/competitions/"+(gameId/13)+"/details?sort=id:desc&limit=3";
                EventListing detailsListing = restTemplate.getForObject($ref, EventListing.class);
                detailsListing.getItems().forEach(ref -> {
                    try {
                        BallDetail ballDetail = restTemplate.getForObject(ref.get$ref(), BallDetail.class);
                        persistBall(ballDetail, gameId);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                });
        });
    }

    public void persistBall(BallDetail ballDetail, Long eventId) {
        String ballId = eventId  + ":"+ ballDetail.getPeriod()+":"+ballDetail.getOver().getUnique();
        Optional<BBBAggregate> bbbAggregateOptional = ballRepository.findById(ballId);
        BBBAggregate bbbAggregate;
        if(bbbAggregateOptional.isPresent()){
            bbbAggregate = bbbAggregateOptional.get();
        }else{
            bbbAggregate = new BBBAggregate();
            bbbAggregate.setBallId(ballId);
        }

        BallSummary ballSummary = getBallSummary(ballDetail, eventId);
        bbbAggregate.setBallSummary(ballSummary);

        OverSummary overSummary = getOverSummary(ballDetail);
        bbbAggregate.setOverSummary(overSummary);

        InningCommentarySummary inningCommentarySummary = getInningCommentarySummary(ballDetail);
        bbbAggregate.setInningCommentarySummary(inningCommentarySummary);

        BatsmanSummary batsmanSummary = getBatsmanSummary(ballDetail.getBatsman());
        bbbAggregate.setBatsmanSummary(batsmanSummary);
        bbbAggregate.setBatsmanId(null != bbbAggregate.getBatsmanSummary().getBatsmanName() ? Long.parseLong(bbbAggregate.getBatsmanSummary().getBatsmanName().split(":")[1]):0);

        BatsmanSummary otherBatsmanSummary = getBatsmanSummary(ballDetail.getOtherBatsman());
        bbbAggregate.setOtherBatsmanSummary(otherBatsmanSummary);

        BowlerSummary bowlerSummary = getBowlerSummary(ballDetail.getBowler());
        bbbAggregate.setBowlerSummary(bowlerSummary);
        bbbAggregate.setBowlerId(null != bbbAggregate.getBowlerSummary().getBowlerName() ? Long.parseLong(bbbAggregate.getBowlerSummary().getBowlerName().split(":")[1]):0);
        bbbAggregate.setDismissalSummary(ballDetail.getDismissal());
        //pushToEventsCommsCache(ballDetail, bbbAggregate);
        ballRepository.save(bbbAggregate);
    }

    /*public MatchCommentary fetchBallDetailsForMatch(String eventId) {
        if(eventsCommsCache.containsKey(eventId)) {
            return eventsCommsCache.get(eventId);
        }else{

            List<BBBAggregate> bbbAggregates = ballRepository.findByEventId(eventId);
            MatchCommentary matchCommentary = new MatchCommentary();

            if(null != bbbAggregates){
                bbbAggregates.forEach(bbbAggregate -> {

                    Optional<InningsCommentary> inningsCommentaryOptional = matchCommentary.getInningsCommentary().stream().filter(inningsCommentary -> inningsCommentary.getInningSummary().getInningsNo() == bbbAggregate.getBallSummary().getInningsNo()).findFirst();

                    if (inningsCommentaryOptional.isPresent()) {
                        InningsCommentary inningsCommentary = inningsCommentaryOptional.get();


                        if (inningsCommentary.getInningSummary().getOversUnique() < bbbAggregate.getInningSummary().getOversUnique()) {
                            inningsCommentary.setInningSummary(bbbAggregate.getInningSummary());
                        }
                        Optional<OverCommentary> overCommentaryOptional = inningsCommentary.getOverCommentarySet().stream().filter(overCommentary -> overCommentary.getOverSummary().getOverNo() == bbbAggregate.getOverSummary().getOverNo()).findFirst();


                        if (overCommentaryOptional.isPresent()) {
                            OverCommentary overCommentary = overCommentaryOptional.get();

                            if (overCommentary.getOverSummary().getOversUnique() <= bbbAggregate.getOverSummary().getOversUnique()) {
                                overCommentary.setOverSummary(bbbAggregate.getOverSummary());
                            }
                            overCommentary.getBallCommentarySet().add(createBallCommentary(bbbAggregate));
                        } else {
                            createOverCommentary(bbbAggregate, inningsCommentary);
                        }

                    } else {
                        createInningsCommentary(bbbAggregate, matchCommentary);
                    }
                });
            }

            //TODO need to implement details here
            return matchCommentary;

        }

    }*/

    /*private void pushToEventsCommsCache(BallDetail ballDetail, BBBAggregate bbbAggregate) {
        try {
                if (eventsCommsCache.containsKey(bbbAggregate.getBallSummary().getEventId())) {
                    MatchCommentary matchCommentary = eventsCommsCache.get(bbbAggregate.getBallSummary().getEventId());
                    Optional<InningsCommentary> inningsCommentaryOptional = matchCommentary.getInningsCommentary()
                            .stream().filter(inningsCommentary -> inningsCommentary.getInningSummary().getInningsNo() == bbbAggregate.getBallSummary().getInningsNo())
                            .findFirst();

                    if (inningsCommentaryOptional.isPresent()) {
                        InningsCommentary inningsCommentary = inningsCommentaryOptional.get();


                        if (inningsCommentary.getInningSummary().getOversUnique() < bbbAggregate.getInningSummary().getOversUnique()) {
                            inningsCommentary.setInningSummary(bbbAggregate.getInningSummary());
                        }
                        Optional<OverCommentary> overCommentaryOptional = inningsCommentary.getOverCommentarySet().stream().filter(overCommentary -> overCommentary.getOverSummary().getOverNo() == bbbAggregate.getOverSummary().getOverNo()).findFirst();


                        if (overCommentaryOptional.isPresent()) {
                            OverCommentary overCommentary = overCommentaryOptional.get();

                            if (overCommentary.getOverSummary().getOversUnique() <= ballDetail.getOver().getUnique()) {
                                overCommentary.setOverSummary(bbbAggregate.getOverSummary());
                            }
                            overCommentary.getBallCommentarySet().add(createBallCommentary(bbbAggregate));
                        } else {
                            createOverCommentary(bbbAggregate, inningsCommentary);
                        }

                    } else {
                        createInningsCommentary(bbbAggregate, matchCommentary);
                    }
                } else {
                    eventsCommsCache.put(bbbAggregate.getBallSummary().getEventId(), createMatchCommentary(bbbAggregate));
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

/*    private MatchCommentary createMatchCommentary(BBBAggregate bbbAggregate) {
        MatchCommentary matchCommentary = new MatchCommentary();
        matchCommentary.setEventId(bbbAggregate.getBallSummary().getEventId());
        createInningsCommentary(bbbAggregate, matchCommentary);
        return matchCommentary;
    }*/

   /* private void createInningsCommentary(BBBAggregate bbbAggregate, MatchCommentary matchCommentary) {
        InningsCommentary inningsCommentary = new InningsCommentary();
        inningsCommentary.setInningSummary(bbbAggregate.getInningSummary());
        createOverCommentary(bbbAggregate, inningsCommentary);
        matchCommentary.getInningsCommentary().add(inningsCommentary);
    }*/

/*    private void createOverCommentary(BBBAggregate bbbAggregate, InningsCommentary inningsCommentary) {
        OverCommentary overCommentary = new OverCommentary();
        BallCommentary ballCommentary = createBallCommentary(bbbAggregate);
        overCommentary.getBallCommentarySet().add(ballCommentary);
        overCommentary.setOverNumber(bbbAggregate.getOverSummary().getOverNo());
        overCommentary.setOverSummary(bbbAggregate.getOverSummary());
        inningsCommentary.getOverCommentarySet().add(overCommentary);
    }*/

    /*private BallCommentary createBallCommentary(BBBAggregate bbbAggregate) {
        BallCommentary ballCommentary = new BallCommentary();
        ballCommentary.setBallId(bbbAggregate.getBallId());
        ballCommentary.setBallSummary(bbbAggregate.getBallSummary());
        ballCommentary.setBatsmanSummary(bbbAggregate.getBatsmanSummary());
        ballCommentary.setBowlerSummary(bbbAggregate.getBowlerSummary());
        ballCommentary.setOtherBatsmanSummary(bbbAggregate.getOtherBatsmanSummary());
        ballCommentary.setDismissalSummary(bbbAggregate.getDismissalSummary());
        return ballCommentary;
    }*/

    private InningCommentarySummary getInningCommentarySummary(BallDetail ballDetail) {
        InningCommentarySummary inningCommentarySummary = new InningCommentarySummary();
        if(null != ballDetail.getInnings()){
            inningCommentarySummary.setBattingTeamName(teamNameService.getTeamName(getSourceTeamId(ballDetail.getTeam())));
            inningCommentarySummary.setTotalRuns(ballDetail.getInnings().getRuns());
            inningCommentarySummary.setWickets(ballDetail.getInnings().getWickets());
            inningCommentarySummary.setInningsNo(ballDetail.getInnings().getNumber());
        }
        if(null != ballDetail.getOver()) {
            inningCommentarySummary.setOversUnique(ballDetail.getOver().getUnique());
        }

        return inningCommentarySummary;
    }

    private BowlerSummary getBowlerSummary(Bowler bowler) {
        BowlerSummary bowlerSummary = new BowlerSummary();
        bowlerSummary.setBowlerBalls(bowler.getBalls());
        bowlerSummary.setBowlerOvers(Double.valueOf(bowler.getBalls()/6+"."+bowler.getBalls()%6));
        bowlerSummary.setBowlerRuns(bowler.getConceded());
        bowlerSummary.setBowlerWickets(bowler.getWickets());
        bowlerSummary.setBowlerName(playerNameService.getPlayerName(getSourceAthleteId(bowler.getAthlete())));
        return bowlerSummary;
    }

    private BatsmanSummary getBatsmanSummary(Batsman batsman) {
        BatsmanSummary batsmanSummary = new BatsmanSummary();
        batsmanSummary.setBatsmanBalls(batsman.getFaced());
        batsmanSummary.setBatsmanRuns(batsman.getTotalRuns());
        batsmanSummary.setBatsmanName(playerNameService.getPlayerName(getSourceAthleteId(batsman.getAthlete())));
        return batsmanSummary;
    }

    private long getSourceAthleteId(Ref ref) {
        try {
            return null != ref.get$ref() ? Long.parseLong(ref.get$ref().split("athletes/")[1]) : 0;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }


    private long getSourceTeamId(Ref ref) {
        try {
            return null != ref.get$ref() ? Long.parseLong(ref.get$ref().split("teams/")[1]) : 0;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    private OverSummary getOverSummary(BallDetail ballDetail) {
        OverSummary overSummary = new OverSummary();
        if(null != ballDetail.getOver()) {
            overSummary.setComplete(ballDetail.getOver().isComplete());
            overSummary.setMaiden(ballDetail.getOver().isMaiden());
            overSummary.setOverNo(ballDetail.getOver().getNumber());
            overSummary.setOversUnique(ballDetail.getOver().getUnique());
            overSummary.setTotalRuns(ballDetail.getOver().getRuns());
            overSummary.setTotalRuns(ballDetail.getOver().getRuns());
        }
        return overSummary;
    }

    private BallSummary getBallSummary(BallDetail ballDetail, Long gameId) {
        BallSummary ballSummary = new BallSummary();
        if(null != ballDetail.getInnings()) {
            ballSummary.setByes(ballDetail.getInnings().getByes() > 0 ? true : false);
            ballSummary.setLegByes(ballDetail.getInnings().getLegByes() > 0 ? true : false);
            ballSummary.setWide(ballDetail.getInnings().getWides() > 0 ? true : false);
            ballSummary.setNoBall(ballDetail.getInnings().getNoBalls() > 0 ? true : false);
            ballSummary.setEventId(gameId);
            ballSummary.setInningsNo(ballDetail.getPeriod());
        }
        if(null != ballDetail.getOver()) {
            ballSummary.setOverActual(ballDetail.getOver().getActual());
            ballSummary.setOverUnique(ballDetail.getOver().getUnique());
            ballSummary.setOvers(ballDetail.getOver().getOvers());
        }
        ballSummary.setText(ballDetail.getShortText());
        ballSummary.setRuns(ballDetail.getScoreValue());

        if(null != ballDetail.getBatsman()){
            ballSummary.setBatsmanRuns(ballDetail.getBatsman().getRuns());
        }
        return ballSummary;
    }
}
