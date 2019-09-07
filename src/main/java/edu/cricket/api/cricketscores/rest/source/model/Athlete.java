package edu.cricket.api.cricketscores.rest.source.model;

import java.util.List;

public class Athlete {
    private String displayName;
    private int age;
    private String gender;

    private List<Style> styles;
    private long country;

    private Position position;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<Style> getStyles() {
        return styles;
    }

    public void setStyles(List<Style> styles) {
        this.styles = styles;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public long getCountry() {
        return country;
    }

    public void setCountry(long country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Athlete{" +
                "displayName='" + displayName + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", styles=" + styles +
                ", country=" + country +
                ", position=" + position +
                '}';
    }
}
