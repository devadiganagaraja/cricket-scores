package edu.cricket.api.cricketscores.rest.controller;

import edu.cricket.api.cricketscores.domain.EventPlayerPointsAggregate;
import edu.cricket.api.cricketscores.domain.UserEventSquadAggregate;
import edu.cricket.api.cricketscores.repository.EventPlayerPointsRepository;
import edu.cricket.api.cricketscores.repository.UserEventSquadRepository;
import edu.cricket.api.cricketscores.rest.request.BestEleven;
import edu.cricket.api.cricketscores.rest.response.model.*;
import edu.cricket.api.cricketscores.rest.service.PlayerNameService;
import edu.cricket.api.cricketscores.rest.service.TeamNameService;
import edu.cricket.api.cricketscores.rest.service.UserService;
import edu.cricket.api.cricketscores.task.EventSquadsTask;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class CricketSquadController {


    private static final Logger logger = LoggerFactory.getLogger(CricketSquadController.class);



    @Autowired
    EventSquadsTask eventSquadsTask;


    @Autowired
    Map<String,Event> liveEvents;

    @Autowired
    UserService userService;

    @Autowired
    PlayerNameService playerNameService;

    @Autowired
    TeamNameService teamNameService;

    @Autowired
    UserEventSquadRepository userEventSquadRepository;

    @Autowired
    EventPlayerPointsRepository eventPlayerPointsRepository;

    @CrossOrigin
    @RequestMapping("/bestEleven/leaderboard/event/{eventId}")
    public List<LeaderBoard> leaderBoard(@PathVariable(value="eventId") String eventId) {
        Optional<EventPlayerPointsAggregate> eventPlayerPointsAggregateOptional = eventPlayerPointsRepository.findById(eventId);

        List<LeaderBoard> leaderBoards = new ArrayList<>();
        if(eventPlayerPointsAggregateOptional.isPresent()) {
            EventPlayerPointsAggregate eventPlayerPointsAggregate = eventPlayerPointsAggregateOptional.get();
            UserEventSquadAggregate userEventSquadAggregateEx = new UserEventSquadAggregate();
            userEventSquadAggregateEx.setEventId(eventId);
            List<UserEventSquadAggregate> userEventSquadAggregateList = userEventSquadRepository.findAll(Example.of(userEventSquadAggregateEx));
            if (null != userEventSquadAggregateList) {
                userEventSquadAggregateList.forEach(userEventSquadAggregate -> {
                    if(null != userEventSquadAggregate && null != userEventSquadAggregate.getUserSquadPlayers()){
                        LeaderBoard leaderBoard = new LeaderBoard();
                        leaderBoard.setUserName(userEventSquadAggregate.getUserName());
                        userEventSquadAggregate.getUserSquadPlayers().forEach(userSquadPlayer ->  {
                            if(eventPlayerPointsAggregate.getPlayerPointsMap().containsKey(playerNameService.getPlayerId(userSquadPlayer.getPlayerName()))) {
                                leaderBoard.setPoints(leaderBoard.getPoints()+ (eventPlayerPointsAggregate.getPlayerPointsMap().get(playerNameService.getPlayerId(userSquadPlayer.getPlayerName())).getPoints()));
                            }
                        });
                        leaderBoards.add(leaderBoard);
                    }

                });
            }

        }
        leaderBoards.sort(Comparator.comparing(LeaderBoard::getPoints, Comparator.reverseOrder()));
        AtomicInteger atomicInteger = new AtomicInteger(1);
        leaderBoards.forEach(leaderBoard -> leaderBoard.setPosition(atomicInteger.getAndIncrement()));

        return leaderBoards;


    }

    @CrossOrigin
    @RequestMapping("/bestEleven/userName/{userName}/event/{eventId}")
    public EventBestEleven getBestEleven(@PathVariable(value="userName") String userName, @PathVariable(value="eventId") String eventId) {

        logger.info("getBestEleven==>");

        EventBestEleven eventBestEleven = new EventBestEleven();
        Event event = liveEvents.get(eventId);
        if(null != event) {
            logger.info("getBestEleven==>event :{}",event);
            eventBestEleven.setEvent(event);
            Squad squad1 = new Squad();
            squad1.setTeamName(event.getTeam1().getTeamName());
            long sourceTeam1Id = Long.valueOf(event.getTeam1().getTeamName().split(":")[1]) / 13;

            squad1.setPlayers(eventSquadsTask.getLeagueTeamPlayers(event.getLeagueId(), sourceTeam1Id, event.getInternationalClassId()));

            eventBestEleven.setSquad1(squad1);
            Squad squad2 = new Squad();
            squad2.setTeamName(event.getTeam2().getTeamName());
            long sourceTeam2Id = Long.valueOf(event.getTeam2().getTeamName().split(":")[1]) / 13;
            squad2.setPlayers(eventSquadsTask.getLeagueTeamPlayers(event.getLeagueId(), sourceTeam2Id, event.getInternationalClassId()));
            eventBestEleven.setSquad2(squad2);

            Optional<UserEventSquadAggregate> userEventSquadAggregateOptional = userEventSquadRepository.findById(userName + ":" + eventId);
            if (userEventSquadAggregateOptional.isPresent()) {
                UserSquad userSquad = new UserSquad();
                List<UserSquadPlayer> userSquadPlayers = userEventSquadAggregateOptional.get().getUserSquadPlayers();
                logger.info("eventPlayerPointsRepository  => eventId :{}", eventId);
                Optional<EventPlayerPointsAggregate> eventPlayerPointsAggregateOptional = eventPlayerPointsRepository.findById(eventId);
                if(eventPlayerPointsAggregateOptional.isPresent()){
                    EventPlayerPointsAggregate eventPlayerPointsAggregate = eventPlayerPointsAggregateOptional.get();
                    Map<Long, PlayerPoints> playerPointsMap = eventPlayerPointsAggregate.getPlayerPointsMap();
                    if(null != playerPointsMap){
                        userSquadPlayers.stream().forEach(userSquadPlayer -> {

                            logger.info("eventPlayerPointsRepository  => userSquadPlayer.getPlayerName() :{}", userSquadPlayer.getPlayerName());

                            long squadPlayerId = Long.valueOf(userSquadPlayer.getPlayerName().split(":")[1]);
                            if(playerPointsMap.containsKey(squadPlayerId)){
                                PlayerPoints playerPoints = playerPointsMap.get(squadPlayerId);
                                logger.info("eventPlayerPointsRepository  => userSquadPlayer.getPlayerName() :{}, playerPoints :{}", userSquadPlayer.getPlayerName(), playerPoints);
                                playerPoints.setPoints(userSquadPlayer.isCaptain()? playerPoints.getPoints()*3 : userSquadPlayer.isVoiceCaptain() ? playerPoints.getPoints()*2 :  playerPoints.getPoints());
                                userSquadPlayer.setPoints(playerPoints);
                                userSquad.setTotalPoints(userSquad.getTotalPoints()+playerPoints.getPoints());
                            }
                        });
                    }
                }
                userSquad.setUserSquadPlayers(userSquadPlayers);
                eventBestEleven.setUserSquad(userSquad);
            }

        }
        return eventBestEleven;
    }


    @CrossOrigin
    @PostMapping("/bestEleven/event/{eventId}")
    public EventBestEleven postBestEleven(@PathVariable(value="eventId") String eventId, @RequestBody BestEleven bestEleven) {
        EventBestEleven eventBestEleven = new EventBestEleven();
        Event event = liveEvents.get(eventId);
        eventBestEleven.setEvent(event);
        Squad squad1 = new Squad();
        squad1.setTeamName(event.getTeam1().getTeamName());
        long sourceTeam1Id = Long.valueOf(event.getTeam1().getTeamName().split(":")[1])/13;

        squad1.setPlayers(eventSquadsTask.getLeagueTeamPlayers(event.getLeagueId(), sourceTeam1Id, event.getInternationalClassId()));

        eventBestEleven.setSquad1(squad1);
        Squad squad2 = new Squad();
        squad2.setTeamName(event.getTeam2().getTeamName());
        long sourceTeam2Id = Long.valueOf(event.getTeam2().getTeamName().split(":")[1])/13;
        squad2.setPlayers(eventSquadsTask.getLeagueTeamPlayers(event.getLeagueId(), sourceTeam2Id, event.getInternationalClassId()));
        eventBestEleven.setSquad2(squad2);

        UserInfo userInfo = userService.getUserInfoByUserNameAndPassword(bestEleven.getUserName(), bestEleven.getPassword());
        if(userInfo.getStatusCode() == 200){
            UserSquad userSquad = new UserSquad();
            userSquad.setUserSquadPlayers(new ArrayList<>());
            if(StringUtils.isNotBlank(bestEleven.getPlayerIds())){
                Arrays.stream(bestEleven.getPlayerIds().split(","))
                        .forEach(player -> {
                            long playerId = Long.parseLong(player.split(":")[0]);
                            long teamId = Long.parseLong(player.split(":")[1]);
                            UserSquadPlayer userSquadPlayer = new UserSquadPlayer();
                            userSquadPlayer.setPlayerName(playerNameService.getPlayerName(playerId/13));
                            userSquadPlayer.setTeamName(teamNameService.getTeamName(teamId/13));
                            userSquadPlayer.setPoints(new PlayerPoints());
                            userSquad.getUserSquadPlayers().add(userSquadPlayer);
                        } );

            }
            saveUserEventSquad(bestEleven.getUserName(), eventId,userSquad.getUserSquadPlayers());
            eventBestEleven.setUserSquad(userSquad);
        }
        return eventBestEleven;

    }
    private void saveUserEventSquad(String userName, String eventId, List<UserSquadPlayer> userSquadPlayers){
        UserEventSquadAggregate userEventSquadAggregate = new UserEventSquadAggregate();
        userEventSquadAggregate.setUserEventId(userName+":"+eventId);
        userEventSquadAggregate.setEventId(eventId);
        userEventSquadAggregate.setUserName(userName);
        userEventSquadAggregate.setUserSquadPlayers(userSquadPlayers);
        userEventSquadRepository.save(userEventSquadAggregate);
    }
}
