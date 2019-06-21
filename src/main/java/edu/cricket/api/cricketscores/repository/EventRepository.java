package edu.cricket.api.cricketscores.repository;

import edu.cricket.api.cricketscores.domain.EventAggregate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<EventAggregate, String > {
}
