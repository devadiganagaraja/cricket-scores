package edu.cricket.api.cricketscores.rest.response.model;

public class EventBestEleven {
    private Event event;
    private Squad squad1;
    private Squad squad2;
    private Squad userSquad;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Squad getSquad1() {
        return squad1;
    }

    public void setSquad1(Squad squad1) {
        this.squad1 = squad1;
    }

    public Squad getSquad2() {
        return squad2;
    }

    public void setSquad2(Squad squad2) {
        this.squad2 = squad2;
    }

    public Squad getUserSquad() {
        return userSquad;
    }

    public void setUserSquad(Squad userSquad) {
        this.userSquad = userSquad;
    }
}
