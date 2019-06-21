package edu.cricket.api.cricketscores.rest.response;

import edu.cricket.api.cricketscores.domain.OverSummary;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class OverCommentary implements Comparable<OverCommentary>{
    private int overNumber;
    private OverSummary overSummary;
    private Set<BallCommentary> ballCommentarySet = new TreeSet<>();

    public OverSummary getOverSummary() {
        return overSummary;
    }

    public void setOverSummary(OverSummary overSummary) {
        this.overSummary = overSummary;
    }

    public Set<BallCommentary> getBallCommentarySet() {
        return ballCommentarySet;
    }

    public void setBallCommentarySet(Set<BallCommentary> ballCommentarySet) {
        this.ballCommentarySet = ballCommentarySet;
    }

    @Override
    public int compareTo(OverCommentary o) {
        return  o.getOverNumber() - this.getOverNumber();
    }

    public int getOverNumber() {
        return overNumber;
    }

    public void setOverNumber(int overNumber) {
        this.overNumber = overNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OverCommentary that = (OverCommentary) o;
        return overNumber == that.overNumber;
    }

    @Override
    public int hashCode() {

        return Objects.hash(overNumber);
    }


    @Override
    public String toString() {
        return "OverCommentary{" +
                "overNumber=" + overNumber +
                ", overSummary=" + overSummary +
                ", ballCommentarySet=" + ballCommentarySet +
                '}';
    }
}
