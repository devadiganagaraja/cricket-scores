package edu.cricket.api.cricketscores.rest.source.model;

public class EventStatusType {
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    @Override
    public String toString() {
        return "EventStatusType{" +
                "state='" + state + '\'' +
                '}';
    }
}
