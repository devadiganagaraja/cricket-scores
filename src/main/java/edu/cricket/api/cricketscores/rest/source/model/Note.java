package edu.cricket.api.cricketscores.rest.source.model;

public class Note {
    private String text;
    private String type;

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Note{" +
                "text='" + text + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
