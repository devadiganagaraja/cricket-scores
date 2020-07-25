package edu.cricket.api.cricketscores;

import com.cricketfoursix.cricketdomain.aggregate.GameAggregate;
import edu.cricket.api.cricketscores.rest.response.MatchCommentary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = {"com.cricketfoursix", "edu.cricket.api.cricketscores"})
public class CricketScoresApplication {

	private static final Logger log = LoggerFactory.getLogger(CricketScoresApplication.class);


	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Qualifier("preGames")
	public Map<Long,Boolean> preGames(){
		return new ConcurrentHashMap<>();
	}


	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Qualifier("liveGames")
	public Map<Long,Boolean> liveGames(){
		return new ConcurrentHashMap<>();
	}


	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Qualifier("postGames")
	public Map<Long,Boolean> postGames(){
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
	public Map<Long,MatchCommentary> eventsCommsCache(){
		return new ConcurrentHashMap<>();
	}



	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(8);
		executor.setMaxPoolSize(8);
		executor.setThreadNamePrefix("cricket_consumer_thread");
		executor.initialize();
		return executor;
	}


	public static void main(String[] args) {
		SpringApplication.run(CricketScoresApplication.class, args);
	}

}
