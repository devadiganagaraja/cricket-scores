package edu.cricket.api.cricketscores.repository;

import edu.cricket.api.cricketscores.domain.UserAggregate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserAggregate, String>{
}
