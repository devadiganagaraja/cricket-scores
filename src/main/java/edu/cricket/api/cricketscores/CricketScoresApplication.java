package edu.cricket.api.cricketscores;

import com.cricketfoursix.cricketdomain.aggregate.GameAggregate;
import edu.cricket.api.cricketscores.rest.response.MatchCommentary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = {"com.cricketfoursix"})
public class CricketScoresApplication {

	private static final Logger log = LoggerFactory.getLogger(CricketScoresApplication.class);


	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Qualifier("preGames")
	public Map<Long,Boolean> preEvents(){
		return new ConcurrentHashMap<>();
	}


	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Qualifier("liveGames")
	public Map<Long,Boolean> liveEvents(){
		return new ConcurrentHashMap<>();
	}


	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Qualifier("postGames")
	public Map<Long,Boolean> postEvents(){
		return new ConcurrentHashMap<>();
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Qualifier("liveGamesCache")
	public Map<Long, GameAggregate> liveGamesCache(){
		return new ConcurrentHashMap<>();
	}


	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Qualifier("eventsCommsCache")
	public Map<String,MatchCommentary> eventsCommsCache(){
		return new ConcurrentHashMap<>();
	}


	public static void main(String[] args) {
		SpringApplication.run(CricketScoresApplication.class, args);
	}

}
