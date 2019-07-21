package edu.cricket.api.cricketscores.repository;

import edu.cricket.api.cricketscores.domain.UserEventSquadAggregate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserEventSquadRepository extends MongoRepository<UserEventSquadAggregate, String> {
}
