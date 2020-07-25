package edu.cricket.api.cricketscores.rest.source.model;

import java.util.List;

public class EventListing {
    private int count;

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    private int pageCount;
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

    @Override
    public String toString() {
        return "EventListing{" +
                "count=" + count +
                ", items=" + items +
                '}';
    }
}
