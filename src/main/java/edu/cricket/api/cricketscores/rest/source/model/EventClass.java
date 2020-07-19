package edu.cricket.api.cricketscores.rest.source.model;

public class EventClass {
    private String internationalClassId;
    private String generalClassId;
    private String name;
    private String eventType;


    public String getInternationalClassId() {
        return internationalClassId;
    }

    public void setInternationalClassId(String internationalClassId) {
        this.internationalClassId = internationalClassId;
    }

    public String getGeneralClassId() {
        return generalClassId;
    }

    public void setGeneralClassId(String generalClassId) {
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

    @Override
    public String toString() {
        return "EventClass{" +
                "internationalClassId='" + internationalClassId + '\'' +
                ", generalClassId='" + generalClassId + '\'' +
                ", name='" + name + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
