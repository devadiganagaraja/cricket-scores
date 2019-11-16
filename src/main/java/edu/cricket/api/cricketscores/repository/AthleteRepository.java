package edu.cricket.api.cricketscores.repository;

import edu.cricket.api.cricketscores.domain.AthleteAggregate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface AthleteRepository extends MongoRepository<AthleteAggregate, String>, QuerydslPredicateExecutor<AthleteAggregate> {
}
