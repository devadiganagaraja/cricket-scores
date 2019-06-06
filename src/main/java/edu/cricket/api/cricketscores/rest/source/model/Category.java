package edu.cricket.api.cricketscores.rest.source.model;

import java.util.List;

public class Category {
    private List<Stat> stats;

    public List<Stat> getStats() {
        return stats;
    }

    public void setStats(List<Stat> stats) {
        this.stats = stats;
    }
}
