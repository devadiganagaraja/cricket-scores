package edu.cricket.api.cricketscores.repository;

import edu.cricket.api.cricketscores.domain.BBBAggregate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BallRepository extends MongoRepository<BBBAggregate, String>{
}
