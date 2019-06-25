package edu.cricket.api.cricketscores.domain;

import edu.cricket.api.cricketscores.rest.response.model.AthleteInfo;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "athletes")
public class AthleteAggregate {
    @Id
    private String athleteId;

    AthleteInfo athleteInfo;


    public String getAthleteId() {
        return athleteId;
    }

    public void setAthleteId(String athleteId) {
        this.athleteId = athleteId;
    }

    public AthleteInfo getAthleteInfo() {
        return athleteInfo;
    }

    public void setAthleteInfo(AthleteInfo athleteInfo) {
        this.athleteInfo = athleteInfo;
    }
}
