package edu.cricket.api.cricketscores.rest.source.model;

public class CompetitionClass {
    private int internationalClassId;
    private int generalClassId;
    private String name;
    private String eventType;

    public int getInternationalClassId() {
        return internationalClassId;
    }

    public void setInternationalClassId(int internationalClassId) {
        this.internationalClassId = internationalClassId;
    }

    public int getGeneralClassId() {
        return generalClassId;
    }

    public void setGeneralClassId(int generalClassId) {
        this.generalClassId = generalClassId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
