package edu.cricket.api.cricketscores.rest.response.model;

import java.util.Set;

public class BowlingCard {
    private Set<BowlerCard> bowlerCardSet;

    public Set<BowlerCard> getBowlerCardSet() {
        return bowlerCardSet;
    }

    public void setBowlerCardSet(Set<BowlerCard> bowlerCardSet) {
        this.bowlerCardSet = bowlerCardSet;
    }
}
