package edu.cricket.api.cricketscores.rest.response;

import edu.cricket.api.cricketscores.domain.InningSummary;

import java.util.Set;
import java.util.TreeSet;

public class InningsCommentary implements Comparable<InningsCommentary>{
    private InningSummary inningSummary;
    private Set<OverCommentary> overCommentarySet = new TreeSet<>();


    public InningSummary getInningSummary() {
        return inningSummary;
    }

    public void setInningSummary(InningSummary inningSummary) {
        this.inningSummary = inningSummary;
    }

    public Set<OverCommentary> getOverCommentarySet() {
        return overCommentarySet;
    }

    public void setOverCommentarySet(Set<OverCommentary> overCommentarySet) {
        this.overCommentarySet = overCommentarySet;
    }

    @Override
    public int compareTo(InningsCommentary o) {
        return (o.inningSummary.getInningsNo()) - this.inningSummary.getInningsNo();
    }

    @Override
    public String toString() {
        return "InningsCommentary{" +
                "inningSummary=" + inningSummary +
                ", overCommentarySet=" + overCommentarySet +
                '}';
    }
}
