package edu.cricket.api.cricketscores.repository;

import edu.cricket.api.cricketscores.domain.BBBAggregate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface BallRepository extends MongoRepository<BBBAggregate, String>, QuerydslPredicateExecutor<BBBAggregate> {
    @Query("{'ballSummary.eventId': ?0}")
    List<BBBAggregate> findByEventId(String eventId);

}
