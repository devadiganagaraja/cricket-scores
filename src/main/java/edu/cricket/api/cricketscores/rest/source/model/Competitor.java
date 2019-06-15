package edu.cricket.api.cricketscores.rest.source.model;

public class Competitor {
    private String $ref;
    private String homeAway;
    private int order;
    private Ref score;
    private Ref team;
    private boolean winner;

    public String get$ref() {
        return $ref;
    }

    public void set$ref(String $ref) {
        this.$ref = $ref;
    }

    public String getHomeAway() {
        return homeAway;
    }

    public void setHomeAway(String homeAway) {
        this.homeAway = homeAway;
    }

    public Ref getScore() {
        return score;
    }

    public void setScore(Ref score) {
        this.score = score;
    }

    public Ref getTeam() {
        return team;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setTeam(Ref team) {
        this.team = team;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }
}
