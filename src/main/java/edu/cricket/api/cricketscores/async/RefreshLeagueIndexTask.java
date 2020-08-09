package edu.cricket.api.cricketscores.async;

import com.cricketfoursix.cricketdomain.aggregate.LeagueAggregate;
import com.cricketfoursix.cricketdomain.aggregate.LeagueIndexAggregate;
import com.cricketfoursix.cricketdomain.common.league.ChildLeague;
import com.cricketfoursix.cricketdomain.common.league.LeagueType;
import com.cricketfoursix.cricketdomain.repository.LeagueIndexRepository;
import com.cricketfoursix.cricketdomain.repository.LeagueRepository;
import edu.cricket.api.cricketscores.rest.source.model.EventDetail;
import edu.cricket.api.cricketscores.rest.source.model.EventListing;
import edu.cricket.api.cricketscores.rest.source.model.League;
import edu.cricket.api.cricketscores.rest.source.model.Season;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;


@Component
@Scope("prototype")
public class RefreshLeagueIndexTask implements Runnable {


    private static final Logger logger = LoggerFactory.getLogger(RefreshLeagueIndexTask.class);


    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    LeagueIndexRepository leagueIndexRepository;

    @Override
    public void run() {


        logger.info("started with RefreshLeagueIndexTask");

        EventListing leagueListing = restTemplate.getForObject("http://new.core.espnuk.org/v2/sports/cricket/leagues", EventListing.class);
        if(null != leagueListing && leagueListing.getItems().size() > 0){

            leagueListing.getItems().forEach(league -> {

                League leagueSource =  restTemplate.getForObject(league.get$ref(), League.class);


                logger.info("leagueSource incoming ==>"+leagueSource);

                long leagueId = Long.valueOf(leagueSource.getId());

                if(null != leagueSource){

                    Season season  = restTemplate.getForObject(leagueSource.getSeason().get$ref(), Season.class);
                    Calendar startDateCalendar = Calendar.getInstance();
                    startDateCalendar.setTime(season.getStartDate());
                    startDateCalendar.add(Calendar.MONTH, -2);
                    Calendar endDateCalendar = Calendar.getInstance();
                    endDateCalendar.setTime(season.getEndDate());
                    endDateCalendar.add(Calendar.MONTH, 2);

                    Date today = new Date();
                    if(today.compareTo(startDateCalendar.getTime())> 0 && endDateCalendar.getTime().compareTo(today) > 0) {

                        EventListing eventListing = restTemplate.getForObject(leagueSource.getSeason().get$ref() + "/events", EventListing.class);


                        if (null != eventListing && eventListing.getItems().size() > 0) {

                            EventDetail eventDetail = restTemplate.getForObject(eventListing.getItems().get(0).get$ref(), EventDetail.class);

                            if (null != eventDetail) {
                                long tourId = Long.valueOf(eventDetail.get$ref().split("leagues/")[1].split("/")[0]);

                                logger.info("leagueSource incoming leagueId : {} tourId: {}", leagueId, tourId);
                                if (tourId > 0) {
                                    Optional<LeagueIndexAggregate> leagueIndexAggregateOpt = leagueIndexRepository.findById(tourId*13);
                                    LeagueIndexAggregate leagueIndexAggregate;
                                    if (leagueIndexAggregateOpt.isPresent()) {
                                        leagueIndexAggregate = leagueIndexAggregateOpt.get();
                                    } else {
                                        League tourSource = restTemplate.getForObject("http://new.core.espnuk.org/v2/sports/cricket/leagues/" + tourId, League.class);

                                        leagueIndexAggregate = new LeagueIndexAggregate();
                                        leagueIndexAggregate.setLeagueId(tourId*13);
                                        leagueIndexAggregate.setName(tourSource.getName());
                                        leagueIndexAggregate.setSeriesNote(tourSource.getSeriesNote());
                                        leagueIndexAggregate.setLeagueType(tourSource.isTournament() ? LeagueType.tournament : tourSource.getName().contains("series") ? LeagueType.series : LeagueType.tour);
                                        leagueIndexAggregate.setAbbreviation(tourSource.getShortName());
                                        if (null != tourSource.getClassId()) {
                                            tourSource.getClassId()
                                                    .forEach(classId -> leagueIndexAggregate.getClassIds()
                                                            .add(Integer.valueOf(classId)));
                                        }

                                    }
                                    if (leagueId != tourId) {
                                        ChildLeague childLeague = new ChildLeague();
                                        childLeague.setLeagueId(leagueId * 13);
                                        childLeague.setAbbreviation(leagueSource.getShortName());
                                        childLeague.setName(leagueSource.getName());
                                        if(null != leagueSource.getClassId() && leagueSource.getClassId().size() > 0 )
                                            childLeague.setClassId(Integer.valueOf(leagueSource.getClassId().get(0)));

                                        leagueIndexAggregate.getChildLeagues().add(childLeague);
                                    }

                                    leagueIndexRepository.save(leagueIndexAggregate);
                                }
                            }
                        }
                    }
                }

            });

        }

    }
}
