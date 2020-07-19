package edu.cricket.api.cricketscores.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.cricketfoursix.cricketdomain.aggregate.QBBBAggregate;

@Configuration
public class QueryDSLConfig {


    @Bean
    public QBBBAggregate  qBBBAggregate(){
        return new QBBBAggregate("balls");
    }


}
