package edu.cricket.api.cricketscores;

import edu.cricket.api.cricketscores.rest.source.model.EventDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CricketScoresApplication {

	private static final Logger log = LoggerFactory.getLogger(CricketScoresApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(CricketScoresApplication.class, args);
	}

}
