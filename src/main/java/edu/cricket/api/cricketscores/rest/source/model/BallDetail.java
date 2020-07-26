package edu.cricket.api.cricketscores.rest.source.model;

import com.cricketfoursix.cricketdomain.domain.bbb.Dismissal;

public class BallDetail {
    private int period;
    private Ref team;
    private String shortText;
    private String text;
    private int scoreValue;
    private Batsman batsman;
    private Batsman otherBatsman;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private Bowler bowler;
    private Bowler otherBowler;
    private Dismissal dismissal;
    private Innings innings;
    private Over over;


    public int getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(int scoreValue) {
        this.scoreValue = scoreValue;
    }

    public Innings getInnings() {
        return innings;
    }

    public void setInnings(Innings innings) {
        this.innings = innings;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getShortText() {
        return shortText;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
    }

    public Batsman getBatsman() {
        return batsman;
    }

    public Ref getTeam() {
        return team;
    }

    public void setTeam(Ref team) {
        this.team = team;
    }

    public void setBatsman(Batsman batsman) {
        this.batsman = batsman;
    }

    public Batsman getOtherBatsman() {
        return otherBatsman;
    }

    public void setOtherBatsman(Batsman otherBatsman) {
        this.otherBatsman = otherBatsman;
    }

    public Bowler getBowler() {
        return bowler;
    }

    public void setBowler(Bowler bowler) {
        this.bowler = bowler;
    }

    public Bowler getOtherBowler() {
        return otherBowler;
    }

    public void setOtherBowler(Bowler otherBowler) {
        this.otherBowler = otherBowler;
    }

    public Dismissal getDismissal() {
        return dismissal;
    }

    public void setDismissal(Dismissal dismissal) {
        this.dismissal = dismissal;
    }

    public Over getOver() {
        return over;
    }

    public void setOver(Over over) {
        this.over = over;
    }

    @Override
    public String toString() {
        return "BallDetail{" +
                "period=" + period +
                ", shortText='" + shortText + '\'' +
                ", batsman=" + batsman +
                ", otherBatsman=" + otherBatsman +
                ", bowler=" + bowler +
                ", otherBowler=" + otherBowler +
                ", dismissal=" + dismissal +
                ", over=" + over +
                '}';
    }
}
