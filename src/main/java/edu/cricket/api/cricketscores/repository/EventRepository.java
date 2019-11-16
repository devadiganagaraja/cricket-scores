package edu.cricket.api.cricketscores.repository;

import edu.cricket.api.cricketscores.domain.BBBAggregate;
import edu.cricket.api.cricketscores.domain.EventAggregate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface EventRepository extends MongoRepository<EventAggregate, String >, QuerydslPredicateExecutor<EventAggregate> {
}
