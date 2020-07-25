package edu.cricket.api.cricketscores.task;

import com.cricketfoursix.cricketdomain.repository.BallRepository;
import edu.cricket.api.cricketscores.async.RefreshAllLiveEventBallsTask;
import edu.cricket.api.cricketscores.async.RefreshAllPostEventBallsTask;
import edu.cricket.api.cricketscores.async.RefreshLiveEventBallsTask;
import edu.cricket.api.cricketscores.rest.service.PlayerNameService;
import edu.cricket.api.cricketscores.rest.service.TeamNameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class EventBallsTask{


    private static final Logger logger = LoggerFactory.getLogger(EventBallsTask.class);





    @Autowired
    private RefreshAllLiveEventBallsTask refreshAllEventBallsTask;

    private void refreshLiveEventAllBallsAsync() {
        taskExecutor.execute(refreshAllEventBallsTask);
    }



    @Autowired
    private RefreshAllPostEventBallsTask refreshAllPostEventBallsTask;

    private void refreshPostEventAllBallsAsync() {
        taskExecutor.execute(refreshAllPostEventBallsTask);
    }



    @Autowired
    private RefreshLiveEventBallsTask refreshLiveEventBallsTask;


    private void refreshLiveEventNewBallsAsync() {
        taskExecutor.execute(refreshLiveEventBallsTask);
    }












    public void refreshLiveEventAllBalls() {
        refreshLiveEventAllBallsAsync();
    }


    public void refreshPostEventAllBalls() {
        refreshPostEventAllBallsAsync();
    }

    public void refreshLiveEventNewBalls() {
        refreshLiveEventNewBallsAsync();
    }




    @Autowired
    Map<Long, Boolean> liveGames;

    @Autowired
    Map<Long, Boolean> postGames;

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    BallRepository ballRepository;

    @Autowired
    TeamNameService teamNameService;

    @Autowired
    PlayerNameService playerNameService;


    @Autowired
    TaskExecutor taskExecutor;




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



}
