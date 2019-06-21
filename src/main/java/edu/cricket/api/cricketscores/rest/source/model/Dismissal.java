package edu.cricket.api.cricketscores.rest.source.model;

public class Dismissal {
    private boolean dismissal;
    private boolean bowled;
    private String type;
    private String text;

    public boolean isDismissal() {
        return dismissal;
    }

    public void setDismissal(boolean dismissal) {
        this.dismissal = dismissal;
    }

    public boolean isBowled() {
        return bowled;
    }

    public void setBowled(boolean bowled) {
        this.bowled = bowled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Dismissal{" +
                "dismissal=" + dismissal +
                ", bowled=" + bowled +
                ", type='" + type + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
