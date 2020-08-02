package edu.cricket.api.cricketscores.async;

import com.cricketfoursix.cricketdomain.aggregate.GameAggregate;
import com.cricketfoursix.cricketdomain.common.game.GameInfo;
import com.cricketfoursix.cricketdomain.repository.GameRepository;
import edu.cricket.api.cricketscores.utils.GameServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Optional;


@Component
@Scope("prototype")
public class RefreshLiveGamesTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RefreshLiveGamesTask.class);

    @Autowired
    Map<Long, Boolean> liveGames;


    @Autowired
    Map<Long, GameAggregate> liveGamesCache;


    @Autowired
    GameRepository gameRepository;


    @Autowired
    GameServiceUtil gameServiceUtil;



    @Override
    public void run() {

        liveGames.keySet().forEach(gameId -> refreshLiveGame(gameId));

        log.info("completed refreshLiveEvent job at {}", new Date());

    }

    public void refreshLiveGame(Long gameId) {
        log.info("processing live game: {}",gameId);
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
        }
        gameServiceUtil.populateGameAggregate(gameAggregate);
        liveGamesCache.put(gameId, gameAggregate);
    }


}
