package edu.cricket.api.cricketscores.rest.source.model;

import java.util.List;

public class EventListing {
    private int count;
    private  List<Ref> items;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Ref> getItems() {
        return items;
    }

    public void setItems(List<Ref> items) {
        this.items = items;
    }
}
