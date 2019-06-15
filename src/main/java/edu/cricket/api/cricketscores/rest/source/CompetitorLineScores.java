package edu.cricket.api.cricketscores.rest.source;

import java.util.List;

public class CompetitorLineScores {
    private List<CompetitorLineScore> items;


    public List<CompetitorLineScore> getItems() {
        return items;
    }

    public void setItems(List<CompetitorLineScore> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "CompetitorLineScores{" +
                "items=" + items +
                '}';
    }
}
