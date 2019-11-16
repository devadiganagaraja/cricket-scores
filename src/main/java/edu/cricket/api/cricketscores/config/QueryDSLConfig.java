package edu.cricket.api.cricketscores.config;

import edu.cricket.api.cricketscores.domain.QAthleteAggregate;
import edu.cricket.api.cricketscores.domain.QBBBAggregate;
import edu.cricket.api.cricketscores.domain.QEventAggregate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryDSLConfig {


    @Bean
    public QBBBAggregate  qBBBAggregate(){
        return new QBBBAggregate("balls");
    }


    @Bean
    public QEventAggregate qEventAggregate(){
        return new QEventAggregate("events");
    }


    @Bean
    public QAthleteAggregate  qAthleteAggregate(){
        return new QAthleteAggregate("athletes");
    }



}
