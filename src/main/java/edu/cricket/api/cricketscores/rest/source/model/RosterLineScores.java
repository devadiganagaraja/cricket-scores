package edu.cricket.api.cricketscores.rest.source.model;

import java.util.List;

public class RosterLineScores {

    private List<RosterLineScore> items;


    public List<RosterLineScore> getItems() {
        return items;
    }

    public void setItems(List<RosterLineScore> items) {
        this.items = items;
    }
}
