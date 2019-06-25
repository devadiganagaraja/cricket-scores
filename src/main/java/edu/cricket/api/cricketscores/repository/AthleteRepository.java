package edu.cricket.api.cricketscores.repository;

import edu.cricket.api.cricketscores.domain.AthleteAggregate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AthleteRepository extends MongoRepository<AthleteAggregate, String> {
}
