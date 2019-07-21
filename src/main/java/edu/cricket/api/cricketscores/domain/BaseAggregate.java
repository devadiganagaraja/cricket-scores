package edu.cricket.api.cricketscores.domain;

import org.springframework.data.annotation.Version;

public abstract class BaseAggregate {

    @Version
    Long version;


}
