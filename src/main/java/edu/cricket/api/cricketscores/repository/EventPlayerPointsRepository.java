package edu.cricket.api.cricketscores.repository;

import edu.cricket.api.cricketscores.domain.EventPlayerPointsAggregate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventPlayerPointsRepository extends MongoRepository<EventPlayerPointsAggregate, String> {
}
