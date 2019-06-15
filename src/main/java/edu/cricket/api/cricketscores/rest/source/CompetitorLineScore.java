package edu.cricket.api.cricketscores.rest.source;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.cricket.api.cricketscores.rest.source.model.Ref;

public class CompetitorLineScore {
    @JsonProperty(value = "isBatting")
    private boolean batting;
    private Ref statistics;
    private int period;


    public boolean isBatting() {
        return batting;
    }

    public void setBatting(boolean batting) {
        this.batting = batting;
    }

    public Ref getStatistics() {
        return statistics;
    }

    public void setStatistics(Ref statistics) {
        this.statistics = statistics;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "CompetitorLineScore{" +
                "batting=" + batting +
                ", statistics=" + statistics +
                ", period=" + period +
                '}';
    }
}
