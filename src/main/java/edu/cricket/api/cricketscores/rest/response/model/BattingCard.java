package edu.cricket.api.cricketscores.rest.response.model;

import java.util.Set;

public class BattingCard {
    private Set<BatsmanCard> batsmanCardSet;

    public Set<BatsmanCard> getBatsmanCardSet() {
        return batsmanCardSet;
    }

    public void setBatsmanCardSet(Set<BatsmanCard> batsmanCardSet) {
        this.batsmanCardSet = batsmanCardSet;
    }
}
