package edu.cricket.api.cricketscores.repository;

import edu.cricket.api.cricketscores.domain.LeagueSquadAggregate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LeagueSquadRepository extends MongoRepository<LeagueSquadAggregate, String > {
}
