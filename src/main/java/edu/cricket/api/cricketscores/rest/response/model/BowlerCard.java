package edu.cricket.api.cricketscores.rest.response.model;

public class BowlerCard implements Comparable<BowlerCard>{

    private long playerId;
    private String playerName;
    private String overs;
    private String wickets;
    private String conceded;
    private String maidens;
    private String wides;
    private String noballs;
    private String byes;
    private String legbyes;
    private Integer position;
    private boolean bowled;
    private String stumped;
    private String caught;
    private String live;


    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getOvers() {
        return overs;
    }

    public void setOvers(String overs) {
        this.overs = overs;
    }

    public String getWickets() {
        return wickets;
    }

    public void setWickets(String wickets) {
        this.wickets = wickets;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public boolean isBowled() {
        return bowled;
    }

    public void setBowled(boolean bowled) {
        this.bowled = bowled;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getStumped() {
        return stumped;
    }

    public void setStumped(String stumped) {
        this.stumped = stumped;
    }

    public String getCaught() {
        return caught;
    }

    public void setCaught(String caught) {
        this.caught = caught;
    }

    public String getConceded() {
        return conceded;
    }

    public void setConceded(String conceded) {
        this.conceded = conceded;
    }

    public String getMaidens() {
        return maidens;
    }

    public String getByes() {
        return byes;
    }

    public void setByes(String byes) {
        this.byes = byes;
    }

    public String getLegbyes() {
        return legbyes;
    }

    public void setLegbyes(String legbyes) {
        this.legbyes = legbyes;
    }

    public void setMaidens(String maidens) {
        this.maidens = maidens;
    }

    public String getWides() {
        return wides;
    }

    public void setWides(String wides) {
        this.wides = wides;
    }

    public String getNoballs() {
        return noballs;
    }

    public void setNoballs(String noballs) {
        this.noballs = noballs;
    }

    public String getLive() {
        return live;
    }

    public void setLive(String live) {
        this.live = live;
    }

    @Override
    public int compareTo(BowlerCard o) {
        return this.position.compareTo(o.position);
    }
}
