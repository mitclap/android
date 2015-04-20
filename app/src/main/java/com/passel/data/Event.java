package com.passel.data;

import java.util.Date;
import java.util.List;

import lombok.Value;

@Value
public class Event {
    String name;
    Date start;
    Date end;
    String description;
    List<String> guests;
    Location location;

    public Event(final String name, final Date start, final Date end,
                 final String description, final List<String> guests,
                 final Location location){
        this.name = name;
        this.start = start;
        this.end = end;
        this.description = description;
        this.guests = guests;
        this.location = location;
    }
}