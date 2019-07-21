package edu.cricket.api.cricketscores.rest.response.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventBestEleven {
    private Event event;
    private Squad squad1;
    private Squad squad2;
    private UserSquad userSquad;


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

    public UserSquad getUserSquad() {
        return userSquad;
    }

    public void setUserSquad(UserSquad userSquad) {
        this.userSquad = userSquad;
    }
}
