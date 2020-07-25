package edu.cricket.api.cricketscores.rest.source.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Competition {
    private List<Competitor> competitors;
    private String id;
    private String note;
    private Ref status;

    private Ref details;
    private Ref tiebreaker;

    private Venue venue;

    @JsonProperty(value="class")
    private EventClass eventClass;


    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }



    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    private List<Note>  notes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Competitor> getCompetitors() {
        return competitors;
    }

    public void setCompetitors(List<Competitor> competitors) {
        this.competitors = competitors;
    }

    @Override
    public String toString() {
        return "Competition{" +
                "competitors=" + competitors +
                ", id='" + id + '\'' +
                ", note='" + note + '\'' +
                ", status=" + status +
                ", details=" + details +
                ", tiebreaker=" + tiebreaker +
                ", venue=" + venue +
                ", eventClass=" + eventClass +
                ", notes=" + notes +
                '}';
    }

    public EventClass getEventClass() {
        return eventClass;
    }

    public void setEventClass(EventClass eventClass) {
        this.eventClass = eventClass;
    }

    public String getNote() {
        return note;
    }

    public Ref getDetails() {
        return details;
    }

    public void setDetails(Ref details) {
        this.details = details;
    }

    public Ref getTiebreaker() {
        return tiebreaker;
    }

    public void setTiebreaker(Ref tiebreaker) {
        this.tiebreaker = tiebreaker;
    }

    public void setNote(String note) {
        this.note = note;
    }


    public Ref getStatus() {
        return status;
    }

    public void setStatus(Ref status) {
        this.status = status;
    }

}
