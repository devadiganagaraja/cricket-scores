package edu.cricket.api.cricketscores;

import edu.cricket.api.cricketscores.domain.EventAggregate;
import edu.cricket.api.cricketscores.rest.response.MatchCommentary;
import edu.cricket.api.cricketscores.rest.response.model.Event;
import edu.cricket.api.cricketscores.rest.response.model.ScoreCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@EnableScheduling
@SpringBootApplication
public class CricketScoresApplication {

	private static final Logger log = LoggerFactory.getLogger(CricketScoresApplication.class);


	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Qualifier("preEvents")
	public Map<String,Boolean> preEvents(){
		return new ConcurrentHashMap<>();
	}


	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Qualifier("liveEvents")
	public Map<String,Boolean> liveEvents(){
		return new ConcurrentHashMap<>();
	}


	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Qualifier("postEvents")
	public Map<String,Boolean> postEvents(){
		return new ConcurrentHashMap<>();
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Qualifier("liveEventsCache")
	public Map<String,EventAggregate> liveEventsCache(){
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
