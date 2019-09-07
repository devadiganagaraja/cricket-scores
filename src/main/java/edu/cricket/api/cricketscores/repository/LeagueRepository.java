package edu.cricket.api.cricketscores.repository;

import edu.cricket.api.cricketscores.domain.LeagueAggregate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LeagueRepository  extends MongoRepository<LeagueAggregate, String > {
}
