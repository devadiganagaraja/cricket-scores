package edu.cricket.api.cricketscores.rest.source.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RosterLineScore {
    private String statistics;
    private int order;

    @JsonProperty(value = "isBatting")
    private boolean isBatting;
    private int period;

    public String getStatistics() {
        return statistics;
    }

    public void setStatistics(String statistics) {
        this.statistics = statistics;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isBatting() {
        return isBatting;
    }

    public void setBatting(boolean batting) {
        isBatting = batting;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
