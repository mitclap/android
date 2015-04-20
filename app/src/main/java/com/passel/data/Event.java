package com.passel.data;

import java.util.Date;
import java.util.List;

import lombok.Value;

@Value
public class Event {
    private String name;
    private Date start;
    private Date end;
    private String description;
    private List<String> guests;
    private Location location;

    public Event(String name, Date start, Date end, String description, List<String> guests, Location location){
        this.name = name;
        this.start = start;
        this.end = end;
        this.description = description;
        this.guests = guests;
        this.location = location;
    }
}