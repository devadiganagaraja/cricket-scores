package edu.cricket.api.cricketscores.rest.response;

import edu.cricket.api.cricketscores.domain.*;
import edu.cricket.api.cricketscores.rest.source.model.Dismissal;

import java.util.Objects;

public class BallCommentary implements Comparable<BallCommentary>{
    private String ballId;
    private BallSummary ballSummary;
    private BatsmanSummary batsmanSummary;
    private BatsmanSummary otherBatsmanSummary;
    private BowlerSummary bowlerSummary;
    private Dismissal dismissalSummary;

    public BallSummary getBallSummary() {
        return ballSummary;
    }

    public void setBallSummary(BallSummary ballSummary) {
        this.ballSummary = ballSummary;
    }

    public BatsmanSummary getBatsmanSummary() {
        return batsmanSummary;
    }

    public void setBatsmanSummary(BatsmanSummary batsmanSummary) {
        this.batsmanSummary = batsmanSummary;
    }

    public BatsmanSummary getOtherBatsmanSummary() {
        return otherBatsmanSummary;
    }

    public void setOtherBatsmanSummary(BatsmanSummary otherBatsmanSummary) {
        this.otherBatsmanSummary = otherBatsmanSummary;
    }

    public BowlerSummary getBowlerSummary() {
        return bowlerSummary;
    }

    public void setBowlerSummary(BowlerSummary bowlerSummary) {
        this.bowlerSummary = bowlerSummary;
    }

    public Dismissal getDismissalSummary() {
        return dismissalSummary;
    }

    public void setDismissalSummary(Dismissal dismissalSummary) {
        this.dismissalSummary = dismissalSummary;
    }

    public String getBallId() {
        return ballId;
    }

    public void setBallId(String ballId) {
        this.ballId = ballId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BallCommentary that = (BallCommentary) o;
        return Objects.equals(ballId, that.ballId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ballId);
    }

    @Override
    public int compareTo(BallCommentary o) {
        return Double.compare(o.ballSummary.getOverUnique(), this.ballSummary.getOverUnique());
    }

    @Override
    public String toString() {
        return "BallCommentary{" +
                "ballId='" + ballId + '\'' +
                '}';
    }
}
